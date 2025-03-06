package edu.neu.cs6510.sp25.t1.cli.commands;

import edu.neu.cs6510.sp25.t1.cli.validation.parser.YamlParser;
import edu.neu.cs6510.sp25.t1.cli.validation.validator.YamlPipelineValidator;
import edu.neu.cs6510.sp25.t1.common.model.Job;
import edu.neu.cs6510.sp25.t1.common.model.Pipeline;
import edu.neu.cs6510.sp25.t1.common.model.Stage;
import picocli.CommandLine;

import java.io.File;
import java.util.*;
import java.util.concurrent.Callable;

/**
 * Handles the --dry-run command, which validates a pipeline file
 * and prints out the execution order in a structured YAML format.
 */
@CommandLine.Command(name = "dry-run", description = "Validates and prints execution order of a pipeline.")
public class DryRunCommand implements Callable<Integer> {

  @CommandLine.Option(names = {"-f", "--file"}, description = "Path to the pipeline configuration file", required = true)
  private String filePath;

  @Override
  public Integer call() {
    File pipelineFile = new File(filePath);
    if (!pipelineFile.exists()) {
      System.err.println("Error: Specified pipeline file does not exist: " + filePath);
      return 1;
    }

    try {
      // Validate the pipeline
      YamlPipelineValidator.validatePipeline(filePath);

      // Parse the YAML
      Pipeline pipeline = YamlParser.parseYaml(pipelineFile);

      // Generate execution order based on dependencies
      List<Stage> orderedStages = orderStagesByExecution(pipeline);

      // Print the execution order in YAML format
      printExecutionPlan(orderedStages);

      return 0;
    } catch (Exception e) {
      System.err.println("Validation failed: " + e.getMessage());
      return 1;
    }
  }

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

  private void visitStage(Stage stage, Map<String, Stage> stageMap, Set<String> visited, List<Stage> orderedStages) {
    if (visited.contains(stage.getName())) return;
    visited.add(stage.getName());

    for (Job job : stage.getJobs()) {
      for (UUID dependency : job.getDependencies()) {
        Stage dependentStage = findStageContainingJob(dependency, stageMap);
        if (dependentStage != null) {
          visitStage(dependentStage, stageMap, visited, orderedStages);
        }
      }
    }
    orderedStages.add(stage);
  }

  private Stage findStageContainingJob(UUID jobUUID, Map<String, Stage> stageMap) {
    for (Stage stage : stageMap.values()) {
      for (Job job : stage.getJobs()) {
        if (job.getId().equals(jobUUID)) {
          return stage;
        }
      }
    }
    return null;
  }

  private void printExecutionPlan(List<Stage> orderedStages) {
    StringBuilder yamlOutput = new StringBuilder();

    for (Stage stage : orderedStages) {
      yamlOutput.append(stage.getName()).append(":\n");
      for (Job job : stage.getJobs()) {
        yamlOutput.append("  ").append(job.getName()).append(":\n");
        yamlOutput.append("    image: ").append(job.getDockerImage()).append("\n");
        yamlOutput.append("    script:\n");
        for (String scriptLine : job.getScript()) {
          yamlOutput.append("    - ").append(scriptLine).append("\n");
        }
      }
    }

    System.out.println(yamlOutput.toString());
  }
}
