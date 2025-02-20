package edu.neu.cs6510.sp25.t1.validation;

import org.yaml.snakeyaml.error.Mark;

import edu.neu.cs6510.sp25.t1.util.ErrorHandler;
import edu.neu.cs6510.sp25.t1.util.ErrorHandler.Location;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Top-level validator for YAML pipeline configurations. This validator
 * coordinates the validation process by:
 * - Loading and parsing the YAML file
 * - Validating the overall pipeline structure
 * - Validating individual jobs
 * - Checking job dependencies for cycles
 * 
 */
public class YamlPipelineValidator {

  /**
   * Validates a pipeline configuration file.
   *
   * @param filePath The path to the YAML configuration file
   * @return true if the pipeline is valid, false otherwise
   */
  public boolean validatePipeline(String filePath) {
    try {
      // Load YAML file with locations
      final YamlLoadResult loadResult = YamlLoader.loadYamlWithLocations(filePath);
      final Map<String, Object> data = loadResult.getData();
      final Map<String, Mark> locations = loadResult.getLocations();

      // Create root location for error reporting
      final Location rootLocation = ErrorHandler.createLocation(
          filePath,
          locations.get("pipeline"),
          "pipeline");

      // Validate structure of pipeline
      final PipelineStructureValidator structureValidator = new PipelineStructureValidator();
      if (!structureValidator.validate(data, locations, filePath)) {
        return false;
      }

      // Extract 'pipeline' section
      if (!(data.get("pipeline") instanceof Map<?, ?> pipeline)) {
        System.err.println(ErrorHandler.formatTypeError(
            rootLocation,
            "pipeline",
            data.get("pipeline"),
            Map.class));
        return false;
      }

      // Extract 'stages' safely
      final Object stagesObj = pipeline.get("stages");
      final Location stagesLocation = ErrorHandler.createLocation(
          filePath,
          locations.get("pipeline.stages"),
          "pipeline.stages");
      if (!(stagesObj instanceof List<?> rawStages)) {
        System.err.println(ErrorHandler.formatTypeError(
            stagesLocation,
            "stages",
            stagesObj,
            List.class));
        return false;
      }

      final List<String> stages = rawStages.stream()
          .filter(String.class::isInstance)
          .map(String.class::cast)
          .toList();
      // Try to get jobs from either 'job' or 'jobs' key
      Object jobsObj = data.get("jobs"); // First try 'jobs'
      String jobKey = "jobs";
      if (jobsObj == null) { // If 'jobs' not found, try 'job'
        jobsObj = data.get("job");
        jobKey = "job";
      }

      // Use dynamic jobKey for location
      final Location jobsLocation = ErrorHandler.createLocation(
          filePath,
          locations.get(jobKey),
          jobKey);

      if (!(jobsObj instanceof List<?> rawJobs)) {
        System.err.println(ErrorHandler.formatTypeError(
            jobsLocation,
            jobKey,
            jobsObj,
            List.class));
        return false;
      }

      @SuppressWarnings("unchecked")
      final List<Map<String, Object>> jobs = rawJobs.stream()
          .filter(item -> item instanceof Map)
          .map(item -> (Map<String, Object>) item)
          .toList();

      // Validate jobs
      final JobValidator jobValidator = new JobValidator(stages);
      if (!jobValidator.validateJobs(jobs, locations, filePath)) {
        return false;
      }

      final Map<String, List<String>> jobDependencies = extractJobDependencies(jobs, locations,
          filePath);
      if (jobDependencies == null) {
        return false;
      }

      // Use the correct jobKey for dependency mark
      final Mark dependencyMark = locations.get(jobKey);
      final DependencyValidator dependencyValidator = new DependencyValidator(jobDependencies,
          dependencyMark, filePath);
      return dependencyValidator.validateDependencies();

    } catch (IOException e) {
      final Location errorLocation = new Location(filePath, 1, 1, "file");
      System.err.println(ErrorHandler.formatException(
          errorLocation,
          "Error reading YAML file: " + e.getMessage()));
      return false;
    }
  }

  /**
   * Validates a pipeline configuration from a map.
   * 
   * @param pipelineConfig
   * @return true if the pipeline is valid, false otherwise
   */
  public boolean validatePipeline(Map<String, Object> pipelineConfig) {
    if (pipelineConfig == null || !pipelineConfig.containsKey("pipeline")) {
      System.err.println("❌ Error: Pipeline configuration is invalid or missing.");
      return false;
    }

    // Validate using existing structure validation logic
    final PipelineStructureValidator structureValidator = new PipelineStructureValidator();
    if (!structureValidator.validate(pipelineConfig, new HashMap<>(), "in-memory")) {
      return false;
    }

    return true;
  }

  /**
   * Extracts and validates job dependencies from the job definitions.
   *
   * @param jobs      List of job definitions from YAML
   * @param locations Map containing source locations for all YAML elements
   * @return A map of job names to their dependencies, or null if validation fails
   */
  private Map<String, List<String>> extractJobDependencies(
      final List<Map<String, Object>> jobs,
      final Map<String, Mark> locations,
      final String filePath) {
    final Map<String, List<String>> jobDependencies = new HashMap<>();

    for (int i = 0; i < jobs.size(); i++) {
      final Map<String, Object> job = jobs.get(i);
      final String jobPath = String.format("job[%d]", i);
      final Location jobLocation = ErrorHandler.createLocation(
          filePath,
          locations.get(jobPath),
          jobPath);

      final Object jobNameObj = job.get("name");
      if (!(jobNameObj instanceof String jobName)) {
        System.err.println(ErrorHandler.formatTypeError(
            jobLocation,
            "name",
            jobNameObj,
            String.class));
        return null;
      }

      final Object needsObj = job.get("needs");
      final String needsPath = jobPath + ".needs";
      final Location needsLocation = ErrorHandler.createLocation(
          filePath,
          locations.get(needsPath),
          needsPath);

      if (needsObj != null) {
        if (!(needsObj instanceof List<?> rawNeeds)) {
          System.err.println(ErrorHandler.formatTypeError(
              needsLocation,
              "needs",
              needsObj,
              List.class));
          return null;
        }

        // Validate each dependency is a string
        final List<String> needs = new ArrayList<>();
        for (int j = 0; j < rawNeeds.size(); j++) {
          final Object need = rawNeeds.get(j);
          final String needPath = String.format("%s[%d]", needsPath, j);
          final Location needLocation = ErrorHandler.createLocation(
              filePath,
              locations.get(needPath),
              needPath);

          if (!(need instanceof String)) {
            System.err.println(ErrorHandler.formatTypeError(
                needLocation,
                "need",
                need,
                String.class));
            return null;
          }
          needs.add((String) need);
        }
        jobDependencies.put(jobName, needs);
      } else {
        jobDependencies.put(jobName, Collections.emptyList());
      }
    }

    return jobDependencies;
  }
}
