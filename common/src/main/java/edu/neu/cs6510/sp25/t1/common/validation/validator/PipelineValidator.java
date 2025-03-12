package edu.neu.cs6510.sp25.t1.common.validation.validator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.neu.cs6510.sp25.t1.common.logging.PipelineLogger;
import edu.neu.cs6510.sp25.t1.common.model.Job;
import edu.neu.cs6510.sp25.t1.common.model.Pipeline;
import edu.neu.cs6510.sp25.t1.common.model.Stage;
import edu.neu.cs6510.sp25.t1.common.validation.error.ErrorHandler;
import edu.neu.cs6510.sp25.t1.common.validation.error.ValidationException;
import edu.neu.cs6510.sp25.t1.common.validation.parser.YamlParser;

/**
 * Validates a parsed PipelineConfig to ensure correctness, structural integrity, and job dependency rules.
 */
public class PipelineValidator {

  /**
   * Validates the structure and dependencies of a pipeline.
   *
   * @param pipeline The pipeline configuration to validate.
   * @param filename The filename of the YAML being validated.
   * @throws ValidationException If the validation fails.
   */
  public static void validate(Pipeline pipeline, String filename) throws ValidationException {
    PipelineLogger.info("Validating pipeline structure: " + filename);
    List<String> errors = new ArrayList<>();

    // Ensure pipeline and stages are not null before accessing them
    if (pipeline == null) {
      ErrorHandler.Location location = new ErrorHandler.Location(filename, 1, 1, "pipeline");
      throw new ValidationException(location, "Pipeline configuration is missing.");
    }

    if (pipeline.getName() == null || pipeline.getName().isEmpty()) {
      ErrorHandler.Location location = new ErrorHandler.Location(filename, YamlParser.getFieldLineNumber(filename, "name"), 1, "name");
      errors.add(ErrorHandler.formatValidationError(location, "Pipeline name is required."));
    }

    if (pipeline.getStages() == null || pipeline.getStages().isEmpty()) {
      ErrorHandler.Location location = new ErrorHandler.Location(filename, YamlParser.getFieldLineNumber(filename, "stages"), 1, "stages");
      errors.add(ErrorHandler.formatValidationError(location, "Pipeline must contain at least one stage."));
    } else {
      // Store job names for dependency validation
      Set<String> jobNames = new HashSet<>();
      for (Stage stage : pipeline.getStages()) {
        if (stage.getJobs() == null || stage.getJobs().isEmpty()) {
          ErrorHandler.Location location = new ErrorHandler.Location(filename, YamlParser.getFieldLineNumber(filename, "jobs"), 1, "jobs");
          errors.add(ErrorHandler.formatValidationError(location, "Stage '" + stage.getName() + "' must contain at least one job."));
          continue; // Skip further processing for this stage if it has no jobs
        }

        for (Job job : stage.getJobs()) {
          if (job.getName() == null || job.getName().isEmpty()) {
            ErrorHandler.Location location = new ErrorHandler.Location(filename, YamlParser.getFieldLineNumber(filename, "name"), 1, "name");
            errors.add(ErrorHandler.formatValidationError(location, "Job in stage '" + stage.getName() + "' must have a name."));
          }
          jobNames.add(job.getName()); // Store job names for validation
        }
      }

      // Validate job dependencies exist
      validateDependenciesExist(pipeline, filename, jobNames, errors);
    }

    // Detect cyclic dependencies
    List<List<String>> cycles = detectCycles(pipeline);
    for (List<String> cycle : cycles) {
      ErrorHandler.Location location = new ErrorHandler.Location(filename, YamlParser.getFieldLineNumber(filename, "dependencies"), 1, "dependencies");
      errors.add(ErrorHandler.formatValidationError(location, "Cyclic dependency detected: " + String.join(" -> ", cycle)));
      PipelineLogger.error("Cycle detected: " + String.join(" -> ", cycle));
    }

    // Throw all errors at once if validation fails
    if (!errors.isEmpty()) {
      PipelineLogger.error("Pipeline validation failed with " + errors.size() + " errors.");
      throw new ValidationException(errors);
    }

    PipelineLogger.info("Pipeline validation successful. Structure, job dependencies are correctly configured in: " + filename);
  }

  /**
   * Ensures all job dependencies reference existing jobs (by name).
   */
  private static void validateDependenciesExist(Pipeline pipeline, String filename, Set<String> jobNames, List<String> errors) {
    for (Stage stage : pipeline.getStages()) {
      for (Job job : stage.getJobs()) {
        if (job.getDependencies() == null) continue;

        for (String dependency : job.getDependencies()) {
          if (!jobNames.contains(dependency)) {
            ErrorHandler.Location location = new ErrorHandler.Location(filename, YamlParser.getFieldLineNumber(filename, "dependencies"), 1, "dependencies");
            errors.add(ErrorHandler.formatValidationError(location, "Job '" + job.getName() + "' depends on a non-existent job: '" + dependency + "'."));
            PipelineLogger.warn("Job '" + job.getName() + "' has an invalid dependency: '" + dependency + "'");
          }
        }
      }
    }
  }

  /**
   * Detects cyclic dependencies in job execution.
   */
  public static List<List<String>> detectCycles(Pipeline pipeline) {
    List<List<String>> detectedCycles = new ArrayList<>();
    Set<String> visited = new HashSet<>();
    Set<String> recursionStack = new HashSet<>();

    for (Stage stage : pipeline.getStages()) {
      for (Job job : stage.getJobs()) {
        List<String> currentPath = new ArrayList<>();
        detectCycleDFS(job.getName(), pipeline, visited, recursionStack, currentPath, detectedCycles);
      }
    }
    return detectedCycles;
  }

  /**
   * Detects cycles in job dependencies using DFS.
   */
  private static void detectCycleDFS(String jobName, Pipeline pipeline, Set<String> visited,
                                     Set<String> recursionStack, List<String> currentPath, List<List<String>> detectedCycles) {
    if (recursionStack.contains(jobName)) {
      List<String> cycle = new ArrayList<>(currentPath);
      cycle.add(jobName);
      detectedCycles.add(cycle);
      PipelineLogger.error("Cycle detected: " + formatCycle(cycle));
      return;
    }
    if (visited.contains(jobName)) {
      return;
    }

    visited.add(jobName);
    recursionStack.add(jobName);
    currentPath.add(jobName);

    for (String dependency : findJobDependencies(jobName, pipeline)) {
      detectCycleDFS(dependency, pipeline, visited, recursionStack, currentPath, detectedCycles);
    }

    recursionStack.remove(jobName);
    currentPath.remove(jobName);
  }

  /**
   * Finds dependencies for a given job in the pipeline (by name).
   */
  private static List<String> findJobDependencies(String jobName, Pipeline pipeline) {
    for (Stage stage : pipeline.getStages()) {
      for (Job job : stage.getJobs()) {
        if (job.getName().equals(jobName)) {
          return job.getDependencies() != null ? job.getDependencies() : Collections.emptyList();
        }
      }
    }
    return Collections.emptyList();
  }

  /**
   * Formats cycle details for better error reporting.
   */
  private static String formatCycle(List<String> cycle) {
    return String.join(" → ", cycle) + " → " + cycle.getFirst(); // Close the loop visually
  }
}
