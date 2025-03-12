package edu.neu.cs6510.sp25.t1.cli.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import edu.neu.cs6510.sp25.t1.common.model.Job;
import edu.neu.cs6510.sp25.t1.common.model.Pipeline;
import edu.neu.cs6510.sp25.t1.common.model.Stage;
import edu.neu.cs6510.sp25.t1.common.validation.parser.YamlParser;
import edu.neu.cs6510.sp25.t1.common.validation.validator.YamlPipelineValidator;
import picocli.CommandLine;

/**
 * Implements the `dry-run` command to validate a pipeline file
 * and print the execution order in structured YAML format.
 */
@CommandLine.Command(
        name = "dry-run",
        description = "Validates a pipeline file and prints execution order in YAML format."
)
public class DryRunCommand implements Callable<Integer> {

  @CommandLine.Option(
          names = {"--file", "-f"},
          description = "Path to the pipeline YAML configuration file.",
          required = true
  )
  private String filePath;

  @Override
  public Integer call() {
    File pipelineFile = new File(filePath);
    if (!pipelineFile.exists() || !pipelineFile.isFile()) {
      System.err.println("Error: Specified pipeline file does not exist: " + filePath);
      return 1;
    }

    try {
      System.out.println("Validating pipeline configuration: " + filePath);
      YamlPipelineValidator.validatePipeline(filePath);

      Pipeline pipeline = YamlParser.parseYaml(pipelineFile);
      List<Stage> orderedStages = orderStagesByExecution(pipeline);

      System.out.println("\nExecution Plan:");
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
    Set<String> visited = new HashSet<>();
    Set<String> recursionStack = new HashSet<>();

    for (Stage stage : pipeline.getStages()) {
      stageMap.put(stage.getName(), stage);
    }

    for (Stage stage : pipeline.getStages()) {
      if (!visited.contains(stage.getName())) {
        if (!visitStage(stage, stageMap, visited, recursionStack, orderedStages)) {
          throw new RuntimeException("Error: Cyclic dependency detected in pipeline stages.");
        }
      }
    }
    return orderedStages;
  }

  private boolean visitStage(Stage stage, Map<String, Stage> stageMap, Set<String> visited, Set<String> recursionStack, List<Stage> orderedStages) {
    if (recursionStack.contains(stage.getName())) {
      return false;
    }
    if (visited.contains(stage.getName())) {
      return true;
    }

    visited.add(stage.getName());
    recursionStack.add(stage.getName());

    for (Job job : stage.getJobs()) {
      for (String dependency : job.getDependencies()) {  // 🔹 Changed from UUID to job name
        Stage dependentStage = findStageContainingJob(dependency, stageMap);
        if (dependentStage != null && !visitStage(dependentStage, stageMap, visited, recursionStack, orderedStages)) {
          return false;
        }
      }
    }

    recursionStack.remove(stage.getName());
    orderedStages.add(stage);
    return true;
  }

  private Stage findStageContainingJob(String jobName, Map<String, Stage> stageMap) {  // 🔹 Changed parameter from UUID to String
    for (Stage stage : stageMap.values()) {
      for (Job job : stage.getJobs()) {
        if (job.getName().equals(jobName)) {
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

    System.out.println(yamlOutput);
  }
}
