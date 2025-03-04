package edu.neu.cs6510.sp25.t1.common.validation.validator;

import java.io.File;

import edu.neu.cs6510.sp25.t1.common.validation.manager.PipelineNameManager;
import edu.neu.cs6510.sp25.t1.common.model.Pipeline;
import edu.neu.cs6510.sp25.t1.common.logging.PipelineLogger;

import edu.neu.cs6510.sp25.t1.common.validation.error.ValidationException;
import edu.neu.cs6510.sp25.t1.common.validation.parser.YamlParser;

/**
 * YamlPipelineValidator is the top-level validator that ensures the entire pipeline configuration is valid.
 * <p>
 * It validates:
 * - YAML Parsing**: Ensures the YAML file is correctly formatted.
 * - Pipeline Structure**: Uses `PipelineValidator` to check pipeline-level validation.
 * - Job Validation**: Uses `JobValidator` to validate individual job definitions.
 * - Error Handling**: Throws `ValidationException` if any validation issues are found.
 * <p>
 * Usage:
 * - Call `YamlPipelineValidator.validatePipeline(filePath)` to validate a pipeline YAML file.
 */
public class YamlPipelineValidator {
  private static final PipelineNameManager pipelineNameManager = new PipelineNameManager();


  /**
   * Validates a pipeline configuration file.
   *
   * @param filePath The path to the YAML configuration file.
   * @throws ValidationException If validation fails.
   */
  public static void validatePipeline(String filePath) throws ValidationException {
    File yamlFile = new File(filePath);
    if (!yamlFile.exists()) {
      PipelineLogger.error(filePath + " does not exist.");
      throw new ValidationException(filePath + ": File not found.");
    }

    // Parse YAML into PipelineConfig
    Pipeline pipeline = YamlParser.parseYaml(yamlFile);

    // Validate pipeline name uniqueness
    if (!pipelineNameManager.isPipelineNameUnique(pipeline.getName())) {
      String suggestedName = pipelineNameManager.suggestUniquePipelineName(pipeline.getName());
      PipelineLogger.warn("Duplicate pipeline name detected: " + pipeline.getName() + ". Suggested: " + suggestedName);
      throw new ValidationException(filePath, 1, "Pipeline name '" + pipeline.getName() + "' is already in use. Suggested: '" + suggestedName + "'.");
    }

    // Validate pipeline structure
    PipelineValidator.validate(pipeline, filePath);

    // Validate job configurations
    JobValidator.validateJobs(pipeline.getStages(), filePath);

    PipelineLogger.info("Pipeline validation successful: " + filePath);
  }
}
