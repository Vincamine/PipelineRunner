package edu.neu.cs6510.sp25.t1.common.validation;

import java.util.HashSet;
import java.util.Set;

import edu.neu.cs6510.sp25.t1.common.config.JobConfig;
import edu.neu.cs6510.sp25.t1.common.config.PipelineConfig;
import edu.neu.cs6510.sp25.t1.common.config.StageConfig;

/**
 * Validates a parsed PipelineDefinition to ensure correctness, YAML structure, and dependencies.
 * Checks for required fields, duplicate job names, and cyclic dependencies.
 * Throws a ValidationException if any validation fails.
 * Static validation logic.
 */
public class PipelineValidator {

  /**
   * Validates the structure and dependencies of a pipeline.
   *
   * @param pipelineConfig The pipeline configuration to validate.
   * @throws ValidationException If the validation fails.
   */
  public static void validate(PipelineConfig pipelineConfig) throws ValidationException {
    if (pipelineConfig == null || pipelineConfig.getName() == null || pipelineConfig.getName().isEmpty()) {
      throw new ValidationException("Pipeline name is required.");
    }

    if (pipelineConfig.getStages() == null || pipelineConfig.getStages().isEmpty()) {
      throw new ValidationException("At least one stage is required.");
    }

    Set<String> jobNames = new HashSet<>();
    for (StageConfig stage : pipelineConfig.getStages()) {
      if (stage.getJobs() == null || stage.getJobs().isEmpty()) {
        throw new ValidationException("Each stage must have at least one job.");
      }

      for (JobConfig job : stage.getJobs()) {
        if (jobNames.contains(job.getName())) {
          throw new ValidationException("Duplicate job name found: " + job.getName());
        }
        jobNames.add(job.getName());
      }
    }

    if (hasCyclicDependencies(pipelineConfig)) {
      throw new ValidationException("Cyclic dependencies detected in job dependencies.");
    }
  }

  /**
   * Checks for cyclic dependencies in job definitions.
   *
   * @param pipelineConfig The pipeline configuration.
   * @return True if cyclic dependencies exist, false otherwise.
   */
  private static boolean hasCyclicDependencies(PipelineConfig pipelineConfig) {
    Set<String> visited = new HashSet<>();
    for (StageConfig stage : pipelineConfig.getStages()) {
      for (JobConfig job : stage.getJobs()) {
        if (!dfs(job, pipelineConfig, new HashSet<>(), visited)) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Performs depth-first search (DFS) to detect cycles.
   *
   * @param job            The current job.
   * @param pipelineConfig The pipeline configuration.
   * @param stack          The stack to track the current job path.
   * @param visited        The set of visited jobs.
   * @return False if a cycle is detected, true otherwise.
   */
  private static boolean dfs(JobConfig job, PipelineConfig pipelineConfig, Set<String> stack, Set<String> visited) {
    if (stack.contains(job.getName())) {
      return false;
    } // Cycle detected
    if (visited.contains(job.getName())) {
      return true;
    } // Already processed

    stack.add(job.getName());
    for (String dependency : job.getNeeds()) {
      JobConfig depJob = findJobByName(dependency, pipelineConfig);
      if (depJob != null && !dfs(depJob, pipelineConfig, stack, visited)) {
        return false;
      }
    }
    stack.remove(job.getName());
    visited.add(job.getName());
    return true;
  }

  /**
   * Finds a job by name within a pipeline.
   *
   * @param jobName        The job name to search for.
   * @param pipelineConfig The pipeline configuration.
   * @return The JobDefinition if found, null otherwise.
   */
  private static JobConfig findJobByName(String jobName, PipelineConfig pipelineConfig) {
    for (StageConfig stage : pipelineConfig.getStages()) {
      for (JobConfig job : stage.getJobs()) {
        if (job.getName().equals(jobName)) {
          return job;
        }
      }
    }
    return null;
  }
}

// package edu.neu.cs6510.sp25.t1.validation;

// import java.nio.file.Files;
// import java.nio.file.Path;
// import java.nio.file.Paths;

// /**
//  * Utility class for validating pipeline YAML files.
//  * Ensures the file exists, is in the correct `.pipelines/` directory,
//  * and follows the correct structure.
//  */
// public class PipelineValidator {
//     private final YamlPipelineValidator yamlPipelineValidator;
//     private static final String PIPELINE_DIRECTORY = ".pipelines";

//     /**
//      * Constructs a new PipelineValidator with a YAML validator.
//      *
//      * @param yamlPipelineValidator The validator instance used for checking YAML
//      *                              structure.
//      */
//     public PipelineValidator(YamlPipelineValidator yamlPipelineValidator) {
//         this.yamlPipelineValidator = yamlPipelineValidator;
//     }

//     /**
//      * Validates the existence and correctness of a pipeline YAML file.
//      * The file is checked for existence, if it resides in the correct directory,
//      * and if it adheres to the expected pipeline structure.
//      *
//      * @param yamlFilePath The path to the YAML file.
//      * @return {@code true} if the file is valid, {@code false} otherwise.
//      */
//     public boolean validatePipelineFile(String yamlFilePath) {
//         try {
//             final Path yamlPath = Paths.get(yamlFilePath).toAbsolutePath().normalize();

//             // Check if the file exists
//             if (!Files.exists(yamlPath)) {
//                 System.err.println("YAML file not found: " + yamlFilePath);
//                 return false;
//             }

//             // Check if the file is inside the correct '.pipelines/' directory
//             final Path parentDir = yamlPath.getParent();
//             if (parentDir == null || !Files.isDirectory(parentDir)
//                     || !PIPELINE_DIRECTORY.equals(parentDir.getFileName().toString())) {
//                 System.err.println("YAML file must be inside the '.pipelines/' folder");
//                 return false;
//             }

//             // Validate the YAML file structure
//             final boolean isValid = yamlPipelineValidator.validatePipeline(yamlPath.toString());

// //            if (!isValid) {
// //                System.err.println("Pipeline validation failed.");
// //            }

//             return isValid;

//         } catch (Exception e) {
//             System.err.println("Pipeline validation error: " + e.getMessage());
//             return false;
//         }
//     }
// }
