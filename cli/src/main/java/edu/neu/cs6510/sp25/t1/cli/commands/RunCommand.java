package edu.neu.cs6510.sp25.t1.cli.commands;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import edu.neu.cs6510.sp25.t1.cli.validation.parser.YamlParser;
import edu.neu.cs6510.sp25.t1.cli.validation.utils.GitUtils;
import edu.neu.cs6510.sp25.t1.cli.validation.validator.YamlPipelineValidator;
import edu.neu.cs6510.sp25.t1.common.logging.PipelineLogger;
import edu.neu.cs6510.sp25.t1.common.model.Job;
import edu.neu.cs6510.sp25.t1.common.model.Pipeline;
import edu.neu.cs6510.sp25.t1.common.model.Stage;
import picocli.CommandLine;

/**
 * Executes a CI/CD pipeline with support for local and remote execution.
 */
@CommandLine.Command(name = "run", description = "Runs a CI/CD pipeline.")
public class RunCommand implements Callable<Integer> {

  @CommandLine.Option(names = {"-r", "--repo"}, description = "Repository URL or path")
  private String repo;

  @CommandLine.Option(names = "--pipeline", description = "Pipeline name")
  private String pipeline;

  @CommandLine.Option(names = {"-f", "--file"}, description = "Path to the pipeline configuration file")
  private String file;

  @CommandLine.Option(names = "--branch", description = "Git branch (default: main)")
  private String branch = "main";

  @CommandLine.Option(names = "--commit", description = "Git commit hash (default: latest)")
  private String commit;

  @CommandLine.Option(names = "--local", description = "Run the pipeline locally")
  private boolean local;

  private static final Set<String> runningPipelines = new HashSet<>();

  @Override
  public Integer call() {
    if (file == null && pipeline == null) {
      System.err.println("Error: Either --file or --pipeline must be specified.");
      return 1;
    }
    if (file != null && pipeline != null) {
      System.err.println("Error: --file and --pipeline are mutually exclusive.");
      return 1;
    }

    GitUtils.validateRepo(file);
    String executionKey = repo + ":" + (file != null ? file : pipeline);

    synchronized (runningPipelines) {
      if (runningPipelines.contains(executionKey)) {
        System.err.println("Warning: Duplicate execution detected. Keeping oldest request.");
        return 1;
      }
      runningPipelines.add(executionKey);
    }

    try {
      YamlPipelineValidator.validatePipeline(file);
      Pipeline pipelineConfig = YamlParser.parseYaml(new File(file));

      if (pipelineConfig == null) {
        System.err.println("Error: Failed to parse pipeline configuration.");
        return 1;
      }

      PipelineLogger.info("Executing pipeline: " + pipelineConfig.getName());
      return executePipeline(pipelineConfig);
    } catch (Exception e) {
      System.err.println("Error: " + e.getMessage());
      return 1;
    } finally {
      synchronized (runningPipelines) {
        runningPipelines.remove(executionKey);
      }
    }
  }

  /**
   * Executes the pipeline while ensuring correct job dependencies and status tracking.
   */
  private int executePipeline(Pipeline pipeline) {
    Map<String, String> stageStatuses = new HashMap<>();
    Map<String, String> jobStatuses = new HashMap<>();

    List<Stage> orderedStages = orderStagesByExecution(pipeline);

    for (Stage stage : orderedStages) {
      PipelineLogger.info("Starting stage: " + stage.getName());
      stageStatuses.put(stage.getName(), "RUNNING");

      List<Job> jobs = stage.getJobs();
      Map<Job, Future<Boolean>> jobResults = new HashMap<>();

      ExecutorService executor = Executors.newFixedThreadPool(jobs.size());

      for (Job job : jobs) {
        if (job.getDependencies().isEmpty() || dependenciesCompleted(job, jobStatuses)) {
          jobStatuses.put(job.getName(), "RUNNING");
          jobResults.put(job, executor.submit(() -> executeJob(job)));
        } else {
          jobStatuses.put(job.getName(), "PENDING");
        }
      }

      boolean stageSuccess = true;
      for (Map.Entry<Job, Future<Boolean>> entry : jobResults.entrySet()) {
        try {
          boolean success = entry.getValue().get();
          jobStatuses.put(entry.getKey().getName(), success ? "SUCCESSFUL" : "FAILED");
          if (!success) stageSuccess = false;
        } catch (Exception e) {
          jobStatuses.put(entry.getKey().getName(), "FAILED");
          stageSuccess = false;
        }
      }

      executor.shutdown();
      if (!stageSuccess) {
        stageStatuses.put(stage.getName(), "FAILED");
        PipelineLogger.error("Stage " + stage.getName() + " failed. Stopping execution.");
        return 1;
      }
      stageStatuses.put(stage.getName(), "SUCCESSFUL");
    }

    PipelineLogger.info("Pipeline execution completed successfully.");
    return 0;
  }

  /**
   * Executes an individual job inside a Docker container or locally.
   */
  private boolean executeJob(Job job) {
    PipelineLogger.info("Executing job: " + job.getName());

    List<String> command = new ArrayList<>();
    command.add("docker");
    command.add("run");
    command.add("--rm");
    command.add(job.getDockerImage());

    for (String scriptCommand : job.getScript()) {
      command.add("/bin/sh");
      command.add("-c");
      command.add(scriptCommand);
    }

    ProcessBuilder processBuilder = new ProcessBuilder(command);
    processBuilder.redirectErrorStream(true);

    try {
      Process process = processBuilder.start();
      int exitCode = process.waitFor();
      return exitCode == 0;
    } catch (IOException | InterruptedException e) {
      Thread.currentThread().interrupt();
      PipelineLogger.error("Error executing job: " + job.getName() + " - " + e.getMessage());
      return false;
    }
  }

  /**
   * Orders stages based on dependencies.
   */
  private List<Stage> orderStagesByExecution(Pipeline pipeline) {
    List<Stage> orderedStages = new ArrayList<>();
    Map<String, Stage> stageMap = new HashMap<>();

    for (Stage stage : pipeline.getStages()) {
      stageMap.put(stage.getName(), stage);
    }

    Set<String> visited = new HashSet<>();
    for (Stage stage : pipeline.getStages()) {
      if (!visited.contains(stage.getName())) {
        visitStage(stage, stageMap, visited, orderedStages);
      }
    }
    return orderedStages;
  }

  /**
   * Recursively processes job dependencies to determine execution order.
   */
  private void visitStage(Stage stage, Map<String, Stage> stageMap, Set<String> visited, List<Stage> orderedStages) {
    if (visited.contains(stage.getName())) return;
    visited.add(stage.getName());

    for (Job job : stage.getJobs()) {
      for (UUID dependencyUUID : job.getDependencies()) {
        String dependency = dependencyUUID.toString();
        Stage dependentStage = findStageContainingJob(dependency, stageMap);
        if (dependentStage != null) {
          visitStage(dependentStage, stageMap, visited, orderedStages);
        }
      }
    }
    orderedStages.add(stage);
  }

  /**
   * Finds the stage that contains a specific job.
   */
  private Stage findStageContainingJob(String jobUUID, Map<String, Stage> stageMap) {
    for (Stage stage : stageMap.values()) {
      for (Job job : stage.getJobs()) {
        if (job.getId().toString().equals(jobUUID)) {
          return stage;
        }
      }
    }
    return null;
  }

  /**
   * Checks if all dependencies of a job have completed successfully.
   */
  private boolean dependenciesCompleted(Job job, Map<String, String> jobStatuses) {
    for (UUID dependencyUUID : job.getDependencies()) {
      String dependency = dependencyUUID.toString();
      if (!"SUCCESSFUL".equals(jobStatuses.get(dependency))) {
        return false;
      }
    }
    return true;
  }
}
