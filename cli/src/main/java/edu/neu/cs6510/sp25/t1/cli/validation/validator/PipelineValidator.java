package edu.neu.cs6510.sp25.t1.cli.validation.validator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import edu.neu.cs6510.sp25.t1.cli.validation.error.ValidationException;
import edu.neu.cs6510.sp25.t1.cli.validation.manager.PipelineNameManager;
import edu.neu.cs6510.sp25.t1.cli.validation.parser.YamlParser;
import edu.neu.cs6510.sp25.t1.common.logging.PipelineLogger;
import edu.neu.cs6510.sp25.t1.common.model.Job;
import edu.neu.cs6510.sp25.t1.common.model.Pipeline;
import edu.neu.cs6510.sp25.t1.common.model.Stage;

/**
 * Validates a parsed PipelineConfig to ensure correctness, structural integrity, and job dependency rules.
 * <p>
 * The validation process includes:
 * - Ensuring required fields (`pipeline`, `stages`, `jobs`) are present.
 * - Verifying that each pipeline contains at least one stage, and each stage has at least one job.
 * - Detecting duplicate job names within the pipeline.
 * - Ensuring that all job dependencies (`needs`) reference existing jobs.
 * - Detecting cyclic dependencies between jobs to prevent infinite execution loops.
 * - Providing structured error reporting in the format: "<filename>:<line>:<error-message>".
 * <p>
 * Validation Logic:
 * 1. Basic Structural Validation: Check if `pipeline`, `stages`, and `jobs` exist.
 * 2. Job Uniqueness: Ensure that job names within the pipeline are unique.
 * 3. Dependency Validation: Verify that every job listed in the `needs` field exists.
 * 4. Cycle Detection: Use Depth-First Search (DFS) to detect and report cycles in job dependencies.
 * <p>
 * Usage:
 * - Call `PipelineValidator.validate(pipelineConfig, filename)` to validate a pipeline.
 * - Throws `ValidationException` if any validation errors occur.
 * <p>
 * Error Reporting:
 * Errors are returned in the format:
 * <p>
 * filename.yaml:12: Cyclic dependency detected: jobA -> jobB -> jobC -> jobA
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
    final PipelineNameManager pipelineNameManager = new PipelineNameManager();

    // Ensure pipeline name exists
    if (pipeline == null || pipeline.getName() == null || pipeline.getName().isEmpty()) {
      errors.add(formatError(filename, YamlParser.getFieldLineNumber(filename, "name"), "Pipeline name is required."));
    } else {
      // Check pipeline name uniqueness
      if (!pipelineNameManager.isPipelineNameUnique(pipeline.getName())) {
        String suggestedName = pipelineNameManager.suggestUniquePipelineName(pipeline.getName());
        errors.add(formatError(filename, YamlParser.getFieldLineNumber(filename, "name"),
                "Pipeline name '" + pipeline.getName() + "' is already in use. Suggested alternative: '" + suggestedName + "'."));
        PipelineLogger.warn("Duplicate pipeline name detected: " + pipeline.getName() + ". Suggested: " + suggestedName);
      }
    }

    // Ensure at least one stage exists
    if (pipeline.getStages() == null || pipeline.getStages().isEmpty()) {
      errors.add(formatError(filename, YamlParser.getFieldLineNumber(filename, "stages"), "At least one stage is required."));
    } else {
      // Populate job names first
      Set<String> jobNames = new HashSet<>();
      for (Stage stage : pipeline.getStages()) {
        for (Job job : stage.getJobs()) {
          jobNames.add(job.getName());
        }
      }

      // Validate job dependencies exist
      validateDependenciesExist(pipeline, filename, jobNames, errors);
    }

    // Detect cyclic dependencies
    List<List<String>> cycles = detectCycles(pipeline);
    for (List<String> cycle : cycles) {
      errors.add(formatError(filename, YamlParser.getFieldLineNumber(filename, "dependencies"), "Cyclic dependency detected: " + String.join(" -> ", cycle)));
      PipelineLogger.error("Cyclic dependency detected: " + String.join(" -> ", cycle));
    }

    // Throw all errors at once if validation fails
    if (!errors.isEmpty()) {
      PipelineLogger.error("Pipeline validation failed with " + errors.size() + " errors.");
      throw new ValidationException(String.join("\n", errors));
    }

    PipelineLogger.info("Pipeline validation passed: " + filename);
  }


  /**
   * Ensures all job dependencies reference existing jobs.
   *
   * @param pipeline The pipeline configuration.
   * @param filename The YAML filename.
   * @param jobNames The set of all defined job names.
   * @param errors   The list of validation errors to populate.
   */
  private static void validateDependenciesExist(Pipeline pipeline, String filename, Set<String> jobNames, List<String> errors) {
    for (Stage stage : pipeline.getStages()) {
      for (Job job : stage.getJobs()) {
        if (job.getDependencies() != null && !job.getDependencies().isEmpty()) { // Only check when dependencies exist
          for (UUID dependency : job.getDependencies()) {
            String dependencyStr = dependency.toString();
            if (!jobNames.contains(dependencyStr)) { // Ensure dependency exists in the pipeline
              errors.add(formatError(filename, YamlParser.getFieldLineNumber(filename, dependencyStr),
                      "Job '" + job.getName() + "' depends on a non-existent job '" + dependencyStr + "'."));
              PipelineLogger.warn("Job '" + job.getName() + "' has an invalid dependency: '" + dependencyStr + "'");
            }
          }
        }
      }
    }
  }

  /**
   * Detects cyclic dependencies in job execution.
   *
   * @param pipeline The pipeline configuration.
   * @return A list of detected cycles.
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
   *
   * @param jobName        The current job name.
   * @param pipeline       The pipeline configuration.
   * @param visited        A set of visited jobs.
   * @param recursionStack Tracks the current job path for cycle detection.
   * @param currentPath    The current job path being explored.
   * @param detectedCycles The list of detected cycles.
   */
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
   * Formats cycle details for better error reporting.
   *
   * @param cycle The detected cycle list.
   * @return Formatted cycle string.
   */
  private static String formatCycle(List<String> cycle) {
    return String.join(" → ", cycle) + " → " + cycle.getFirst(); // Close the loop visually
  }

  /**
   * Finds dependencies for a given job in the pipeline.
   *
   * @param jobName  The name of the job.
   * @param pipeline The pipeline configuration.
   * @return A list of job names (as Strings) that this job depends on.
   */
  private static List<String> findJobDependencies(String jobName, Pipeline pipeline) {
    for (Stage stage : pipeline.getStages()) {
      for (Job job : stage.getJobs()) {
        if (job.getName().equals(jobName)) {
          // Convert List<UUID> to List<String>
          return job.getDependencies().stream().map(UUID::toString).toList();
        }
      }
    }
    return Collections.emptyList();
  }

  /**
   * Generates a unique signature for a detected cycle.
   *
   * @param cycle The detected cyclic dependency path.
   * @return A string representing the unique cycle signature.
   */
  private static String createCycleSignature(List<String> cycle) {
    String minJob = cycle.getFirst();
    int minIndex = 0;
    for (int i = 1; i < cycle.size(); i++) {
      if (cycle.get(i).compareTo(minJob) < 0) {
        minJob = cycle.get(i);
        minIndex = i;
      }
    }

    StringBuilder signature = new StringBuilder();
    for (int i = 0; i < cycle.size(); i++) {
      int index = (minIndex + i) % cycle.size();
      signature.append(cycle.get(index)).append("->");
    }
    signature.append(minJob);
    return signature.toString();
  }

  /**
   * Formats error messages as <filename>:<line>:<error-message>.
   *
   * @param filename The filename of the YAML being validated.
   * @param line     The line number (defaulted to 1 since YAML parser does not provide it).
   * @param message  The error message.
   * @return Formatted error message.
   */
  private static String formatError(String filename, int line, String message) {
    return filename + ":" + line + ":" + message;
  }
}
