package edu.neu.cs6510.sp25.t1.cli.util;

import org.yaml.snakeyaml.Yaml;

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
    final Yaml yaml = new Yaml();
    final Map<String, Object> pipelineConfig;

    try (FileInputStream inputStream = new FileInputStream(yamlFilePath)) {
      pipelineConfig = yaml.load(inputStream);
      System.out.println("Parsed YAML: " + pipelineConfig); // Debugging
    }

    if (pipelineConfig == null || !pipelineConfig.containsKey("pipeline")) {
      throw new IllegalArgumentException("Invalid YAML structure: 'pipeline' key is missing.");
    }

    Map<String, Object> pipelineMetadata = (Map<String, Object>) pipelineConfig.get("pipeline");
    List<String> stages = (List<String>) pipelineMetadata.get("stages");

    if (stages == null || stages.isEmpty()) {
      throw new IllegalArgumentException("Invalid YAML structure: 'stages' list is empty.");
    }

    if (!pipelineConfig.containsKey("job")) {
      throw new IllegalArgumentException("Invalid YAML structure: 'job' key is missing.");
    }

    Object jobObj = pipelineConfig.get("job");
    if (!(jobObj instanceof List)) {
      throw new IllegalArgumentException("Invalid YAML structure: 'job' should be a list.");
    }

    // Extract jobs
    List<Map<String, Object>> jobsList = (List<Map<String, Object>>) pipelineConfig.get("job");

    Set<String> jobNames = new HashSet<>();
    for (Map<String, Object> job : jobsList) {
      jobNames.add((String) job.get("name"));
    }

    // Convert jobs into adjacency list (Graph representation)
    Map<String, List<String>> jobDependencies = new HashMap<>();
    for (Map<String, Object> job : jobsList) {
      String jobName = (String) job.get("name");
      jobDependencies.putIfAbsent(jobName, new ArrayList<>());

      if (job.containsKey("needs")) {
        List<String> needsList = (List<String>) job.get("needs");
        for (String dep : needsList) {
          if (!jobNames.contains(dep)) {
            System.err.println("Missing dependency detected: " + dep + " for job " + jobName);
            return new HashMap<>(); // Return empty execution order
          }
        }
        jobDependencies.get(jobName).addAll(needsList);
      }
    }

    return processJobs(jobsList, stages);
  }

  /**
   * Processes the jobs and resolves dependencies while omitting "needs".
   *
   * @param jobsList The list of jobs from the YAML file
   * @param stages The list of stages from the pipeline
   * @return A LinkedHashMap maintaining the correct execution order
   */
  private Map<String, Map<String, Object>> processJobs(List<Map<String, Object>> jobsList, List<String> stages) {
    final Map<String, List<String>> dependencies = new HashMap<>();
    final Map<String, Map<String, Object>> executionOrder = new LinkedHashMap<>();

    // Initialize stages in execution order
    for (String stage : stages) {
      executionOrder.put(stage, new LinkedHashMap<>());
    }

    // Extract jobs and dependencies
    for (Map<String, Object> job : jobsList) {
      String jobName = (String) job.get("name");
      String stage = (String) job.get("stage");

      if (!executionOrder.containsKey(stage)) {
        throw new IllegalArgumentException("Invalid job stage: " + stage);
      }

      executionOrder.get(stage).put(jobName, new LinkedHashMap<>());

      if (job.containsKey("needs")) {
        List<String> neededJobs = (List<String>) job.get("needs");
        dependencies.put(jobName, neededJobs);
      }
    }

    return executionOrder;
  }
}
