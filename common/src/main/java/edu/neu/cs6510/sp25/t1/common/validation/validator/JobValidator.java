package edu.neu.cs6510.sp25.t1.common.validation.validator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.neu.cs6510.sp25.t1.common.logging.PipelineLogger;
import edu.neu.cs6510.sp25.t1.common.model.Job;
import edu.neu.cs6510.sp25.t1.common.model.Stage;
import edu.neu.cs6510.sp25.t1.common.validation.error.ErrorHandler;
import edu.neu.cs6510.sp25.t1.common.validation.error.ValidationException;
import edu.neu.cs6510.sp25.t1.common.validation.parser.YamlParser;

/**
 * JobValidator ensures that job definitions within a pipeline meet validation criteria.
 */
public class JobValidator {

  /**
   * Validates all jobs in a pipeline configuration.
   *
   * @param stages   The list of stages in the pipeline.
   * @param filename The YAML filename for error reporting.
   * @throws ValidationException If validation fails.
   */
  public static void validateJobs(List<Stage> stages, String filename) throws ValidationException {
    PipelineLogger.info("🔍 Validating jobs in pipeline file: " + filename);
    List<String> errors = new ArrayList<>();
    Set<String> jobNames = new HashSet<>();

    for (Stage stage : stages) {
      for (Job job : stage.getJobs()) {
        PipelineLogger.debug("Checking job: " + job.getName());

        // Validate required fields
        if (job.getName() == null || job.getName().isEmpty()) {
          ErrorHandler.Location location = new ErrorHandler.Location(filename, YamlParser.getFieldLineNumber(filename, "name"), 1, "name");
          String error = ErrorHandler.formatValidationError(location, "Job must have a name.");
          errors.add(error);
          PipelineLogger.warn(error);
        }
        if (job.getDockerImage() == null || job.getDockerImage().isEmpty()) {
          ErrorHandler.Location location = new ErrorHandler.Location(filename, YamlParser.getFieldLineNumber(filename, "image"), 1, "dockerImage");
          String error = ErrorHandler.formatValidationError(location, "Job '" + job.getName() + "' must specify a Docker image.");
          errors.add(error);
          PipelineLogger.warn(error);
        }
        if (job.getScript() == null || job.getScript().isEmpty()) {
          ErrorHandler.Location location = new ErrorHandler.Location(filename, YamlParser.getFieldLineNumber(filename, "script"), 1, "script");
          String error = ErrorHandler.formatValidationError(location, "Job '" + job.getName() + "' must have at least one script command.");
          errors.add(error);
          PipelineLogger.warn(error);
        }

        // Validate unique job names
        if (jobNames.contains(job.getName())) {
          ErrorHandler.Location location = new ErrorHandler.Location(filename, YamlParser.getFieldLineNumber(filename, "name"), 1, "name");
          String error = ErrorHandler.formatValidationError(location, "Duplicate job name found: " + job.getName());
          errors.add(error);
          PipelineLogger.warn(error);
        }
        jobNames.add(job.getName());
      }
    }

    // Log errors before throwing exception
    if (!errors.isEmpty()) {
      PipelineLogger.error("Job validation failed with " + errors.size() + " errors in: " + filename);
      throw new ValidationException(errors);
    }

    PipelineLogger.info("Job validation successful. All jobs are correctly defined in: " + filename);
  }
}
