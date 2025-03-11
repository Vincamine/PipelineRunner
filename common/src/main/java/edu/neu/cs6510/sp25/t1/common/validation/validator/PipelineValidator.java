package edu.neu.cs6510.sp25.t1.common.validation.validator;

import java.util.*;

import edu.neu.cs6510.sp25.t1.common.validation.error.ValidationException;
import edu.neu.cs6510.sp25.t1.common.validation.parser.YamlParser;
import edu.neu.cs6510.sp25.t1.common.logging.PipelineLogger;
import edu.neu.cs6510.sp25.t1.common.model.Job;
import edu.neu.cs6510.sp25.t1.common.model.Pipeline;
import edu.neu.cs6510.sp25.t1.common.model.Stage;

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
    List<String> errors = new ArrayList<>();

    // Ensure pipeline name exists
    if (pipeline == null || pipeline.getName() == null || pipeline.getName().isEmpty()) {
      errors.add(formatError(filename, YamlParser.getFieldLineNumber(filename, "name"), "Pipeline name is required."));
    }

    // Ensure at least one stage exists
    if (pipeline.getStages() == null || pipeline.getStages().isEmpty()) {
      errors.add(formatError(filename, YamlParser.getFieldLineNumber(filename, "stages"), "At least one stage is required."));
    } else {
      // Store job names for dependency validation
      Set<String> jobNames = new HashSet<>();
      for (Stage stage : pipeline.getStages()) {
        for (Job job : stage.getJobs()) {
          jobNames.add(job.getName()); // Store job names instead of UUIDs
        }
      }

      // Validate job dependencies exist (Fix UUID issue)
      validateDependenciesExist(pipeline, filename, jobNames, errors);
    }

    // Detect cyclic dependencies
    List<List<String>> cycles = detectCycles(pipeline);
    for (List<String> cycle : cycles) {
      errors.add(formatError(filename, YamlParser.getFieldLineNumber(filename, "dependencies"),
              "Cyclic dependency detected: " + String.join(" -> ", cycle)));
      PipelineLogger.error("Cycle detected: " + String.join(" -> ", cycle));
    }

    // Throw all errors if validation fails
    if (!errors.isEmpty()) {
      PipelineLogger.error("Pipeline validation failed with " + errors.size() + " errors.");
      throw new ValidationException(String.join("\n", errors));
    }

    PipelineLogger.info("Pipeline validation successful. Structure, job dependencies are correctly configured in: " + filename);
  }


  private static void validateDependenciesExist(Pipeline pipeline, String filename, Set<String> jobNames, List<String> errors) {
    for (Stage stage : pipeline.getStages()) {
      for (Job job : stage.getJobs()) {
        for (String dependency : job.getDependencies()) {
          if (!jobNames.contains(dependency)) {
            int line = YamlParser.getFieldLineNumber(filename, dependency);  // Get exact line number
            errors.add(formatError(filename, line, "Job '" + job.getName() + "' depends on a non-existent job '" + dependency + "'."));
            PipelineLogger.warn("Job '" + job.getName() + "' has an invalid dependency: '" + dependency + "'");
          }
        }
      }
    }
  }



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


  private static void detectCycleDFS(String jobName, Pipeline pipeline, Set<String> visited,
                                     Set<String> recursionStack, List<String> currentPath, List<List<String>> detectedCycles) {
    if (recursionStack.contains(jobName)) {
      List<String> cycle = new ArrayList<>(currentPath);
      cycle.add(jobName);
      detectedCycles.add(cycle);
      PipelineLogger.error("⚠️ Cycle detected: " + formatCycle(cycle));
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
   * Finds dependencies for a given job in the pipeline.
   */
  private static List<String> findJobDependencies(String jobName, Pipeline pipeline) {
    for (Stage stage : pipeline.getStages()) {
      for (Job job : stage.getJobs()) {
        if (job.getName().equals(jobName)) {
          return job.getDependencies();  // **Returns dependencies as Strings (job names)**
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

  /**
   * Formats error messages as <filename>:<line>:<error-message>.
   */
  private static String formatError(String filename, int line, String message) {
    return filename + ":" + line + ":" + message;
  }
}
