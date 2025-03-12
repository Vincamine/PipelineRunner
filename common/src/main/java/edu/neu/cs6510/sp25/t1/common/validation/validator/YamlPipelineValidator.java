package edu.neu.cs6510.sp25.t1.common.validation.validator;

import java.io.File;

import edu.neu.cs6510.sp25.t1.common.logging.PipelineLogger;
import edu.neu.cs6510.sp25.t1.common.model.Pipeline;
import edu.neu.cs6510.sp25.t1.common.validation.error.ErrorHandler;
import edu.neu.cs6510.sp25.t1.common.validation.error.ValidationException;
import edu.neu.cs6510.sp25.t1.common.validation.manager.PipelineNameManager;
import edu.neu.cs6510.sp25.t1.common.validation.parser.YamlParser;

/**
 * YamlPipelineValidator is the top-level validator that ensures the entire pipeline configuration is valid.
 */
public class YamlPipelineValidator {

  /**
   * Validates a pipeline configuration file.
   *
   * @param filePath The path to the YAML configuration file.
   * @throws ValidationException If validation fails.
   */
  public static void validatePipeline(String filePath) throws ValidationException {
    PipelineLogger.info("Starting validation for file: " + filePath);

    File yamlFile = new File(filePath);
    if (!yamlFile.exists()) {
      ErrorHandler.Location location = new ErrorHandler.Location(filePath, 1, 1, "file");
      String errorMessage = "File not found.";

      PipelineLogger.error(ErrorHandler.formatValidationError(location, errorMessage));
      throw new ValidationException(location, errorMessage);
    }

    Pipeline pipeline;
    try {
      PipelineLogger.info("Parsing YAML file: " + filePath);
      pipeline = YamlParser.parseYaml(yamlFile);
      PipelineLogger.info("YAML parsing successful: " + filePath);
    } catch (ValidationException e) {
      PipelineLogger.error("YAML parsing failed: " + e.getMessage());
      throw e; // Rethrow exception after logging
    }

    // check unique pipeline name
    PipelineNameManager nameManager = new PipelineNameManager();
    if (!nameManager.isPipelineNameUnique(pipeline.getName())) {
      throw new ValidationException(filePath, 1, 1, "Pipeline name '" + pipeline.getName() + "' is not unique within the repository.");
    }

    try {
      PipelineLogger.info("Validating pipeline structure...");
      PipelineValidator.validate(pipeline, filePath);
      PipelineLogger.info("Pipeline structure validated.");
    } catch (ValidationException e) {
      PipelineLogger.error("Pipeline structure validation failed: " + e.getMessage());
      throw e;
    }

    try {
      PipelineLogger.info("Validating jobs...");
      JobValidator.validateJobs(pipeline.getStages(), filePath);
      PipelineLogger.info("Job validation successful.");
    } catch (ValidationException e) {
      PipelineLogger.error("Job validation failed: " + e.getMessage());
      throw e;
    }

    PipelineLogger.info("Pipeline validation successful for: " + filePath);
  }
}
