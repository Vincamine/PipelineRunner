package edu.neu.cs6510.sp25.t1.cli.util;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

/**
 * Utility class to parse a pipeline YAML file and determine the execution order of stages and jobs.
 * The output respects dependencies but does not include the "needs" key.
 */
public class PipelineExecutionOrderGenerator {

  /**
   * Parses the pipeline YAML file and generates the execution order.
   *
   * @param yamlFilePath Path to the pipeline YAML file
   * @return A LinkedHashMap representing the execution order of stages and jobs
   * @throws IOException If the file cannot be read
   */
  public Map<String, Map<String, Object>> generateExecutionOrder(String yamlFilePath) throws IOException {
    Yaml yaml = new Yaml();
    Map<String, Object> pipelineConfig;

    try (FileInputStream inputStream = new FileInputStream(yamlFilePath)) {
      pipelineConfig = yaml.load(inputStream);
    }

    if (pipelineConfig == null || !pipelineConfig.containsKey("stages")) {
      throw new IllegalArgumentException("Invalid YAML structure: 'stages' key is missing.");
    }

    Map<String, List<Map<String, Object>>> stages = (Map<String, List<Map<String, Object>>>) pipelineConfig.get("stages");
    return processStages(stages);
  }

  /**
   * Processes the stages and resolves dependencies while omitting "needs".
   *
   * @param stages The pipeline stages from the YAML file
   * @return A LinkedHashMap maintaining the correct execution order
   */
  private Map<String, Map<String, Object>> processStages(Map<String, List<Map<String, Object>>> stages) {
    Map<String, List<String>> dependencies = new HashMap<>();
    Map<String, List<String>> jobsByStage = new LinkedHashMap<>();
    Set<String> processedStages = new HashSet<>();

    // Extract jobs and dependencies
    for (Map.Entry<String, List<Map<String, Object>>> entry : stages.entrySet()) {
      String stageName = entry.getKey();
      List<String> jobs = new ArrayList<>();

      for (Map<String, Object> jobDef : entry.getValue()) {
        for (String jobName : jobDef.keySet()) {
          jobs.add(jobName);

          // Extract "needs" dependencies
          if (jobDef.get(jobName) instanceof Map) {
            Map<String, Object> jobProps = (Map<String, Object>) jobDef.get(jobName);
            if (jobProps.containsKey("needs")) {
              List<String> neededJobs = (List<String>) jobProps.get("needs");
              dependencies.put(jobName, neededJobs);
            }
          }
        }
      }
      jobsByStage.put(stageName, jobs);
    }

    return resolveExecutionOrder(jobsByStage, dependencies);
  }

  /**
   * Resolves job execution order while ensuring dependencies are respected.
   *
   * @param jobsByStage The mapping of stages to jobs
   * @param dependencies The dependency graph of jobs
   * @return A LinkedHashMap with execution order (stage -> jobs)
   */
  private Map<String, Map<String, Object>> resolveExecutionOrder(Map<String, List<String>> jobsByStage, Map<String, List<String>> dependencies) {
    Map<String, Map<String, Object>> executionOrder = new LinkedHashMap<>();
    Set<String> executedJobs = new HashSet<>();

    for (Map.Entry<String, List<String>> stageEntry : jobsByStage.entrySet()) {
      String stage = stageEntry.getKey();
      Map<String, Object> stageJobs = new LinkedHashMap<>();

      for (String job : stageEntry.getValue()) {
        if (canExecuteJob(job, dependencies, executedJobs)) {
          stageJobs.put(job, new LinkedHashMap<>());
          executedJobs.add(job);
        }
      }

      executionOrder.put(stage, stageJobs);
    }

    return executionOrder;
  }

  /**
   * Checks if a job can be executed based on its dependencies.
   *
   * @param job The job to check
   * @param dependencies The dependency mapping
   * @param executedJobs The set of already executed jobs
   * @return true if the job can execute, false otherwise
   */
  private boolean canExecuteJob(String job, Map<String, List<String>> dependencies, Set<String> executedJobs) {
    if (!dependencies.containsKey(job)) {
      return true;
    }

    for (String dependency : dependencies.get(job)) {
      if (!executedJobs.contains(dependency)) {
        return false;
      }
    }
    return true;
  }
}
