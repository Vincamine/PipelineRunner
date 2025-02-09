package edu.neu.cs6510.sp25.t1.cli.validation;

import java.io.IOException;
import java.util.*;

/**
 * YamlPipelineValidator is responsible for validating a YAML pipeline configuration.
 * It ensures:
 * - The YAML structure is valid.
 * - Jobs and stages follow the correct format.
 * - Job dependencies do not form cycles.
 */
public class YamlPipelineValidator {

  /**
   * Validates a pipeline configuration file.
   *
   * @param filePath The path to the YAML configuration file.
   * @return true if the pipeline is valid, false otherwise.
   */
  public boolean validatePipeline(String filePath) {
    try {
      // Load YAML file
      final Map<String, Object> data = YamlLoader.loadYaml(filePath);

      // Validate structure of pipeline
      final PipelineStructureValidator structureValidator = new PipelineStructureValidator();
      if (!structureValidator.validate(data)) {
        return false;
      }

      // Extract 'stages' safely
      final Object pipelineObj = data.get("pipeline");
      if (!(pipelineObj instanceof Map<?, ?> pipeline)) {
        System.err.println("Error: Invalid 'pipeline' format.");
        return false;
      }

      final Object stagesObj = pipeline.get("stages");
      if (!(stagesObj instanceof List<?> rawStages)) {
        System.err.println("Error: 'stages' must be a list.");
        return false;
      }

      final List<String> stages = rawStages.stream()
          .filter(String.class::isInstance)
          .map(String.class::cast)
          .toList();

      // Extract 'job' safely
      final Object jobsObj = data.get("job");
      if (!(jobsObj instanceof List<?> rawJobs)) {
        System.err.println("Error: 'job' must be a list.");
        return false;
      }

      @SuppressWarnings("unchecked")
      final List<Map<String, Object>> jobs = rawJobs.stream()
          .filter(item -> item instanceof Map)
          .map(item -> (Map<String, Object>) item)
          .toList();

      // Validate jobs
      final JobValidator jobValidator = new JobValidator(stages);
      if (!jobValidator.validateJobs(jobs)) {
        return false;
      }

      final Map<String, List<String>> jobDependencies = extractJobDependencies(jobs);

      // Validate dependencies
      final DependencyValidator dependencyValidator = new DependencyValidator(jobDependencies);
      return dependencyValidator.validateDependencies();

    } catch (IOException e) {
      System.err.println("Error reading YAML file: " + e.getMessage());
      return false;
    }
  }

  /**
   * Extracts job dependencies from the job definitions.
   *
   * @param jobs List of job definitions from YAML.
   * @return A map where each job name is mapped to its dependencies (needs).
   */
  private Map<String, List<String>> extractJobDependencies(List<Map<String, Object>> jobs) {
    final Map<String, List<String>> jobDependencies = new HashMap<>();

    for (Map<String, Object> job : jobs) {
      final Object jobNameObj = job.get("name");
      if (!(jobNameObj instanceof String jobName)) {
        System.err.println("Error: Job without a valid 'name'.");
        continue;
      }

      final Object needsObj = job.get("needs");
      if (needsObj instanceof List<?> rawNeeds) {
        final List<String> needs = rawNeeds.stream()
            .filter(String.class::isInstance)
            .map(String.class::cast)
            .toList();
        jobDependencies.put(jobName, needs);
      } else {
        jobDependencies.put(jobName, Collections.emptyList()); // No dependencies
      }
    }

    return jobDependencies;
  }
}
