package edu.neu.cs6510.sp25.t1.cli.validation.validator;

import java.io.File;

import edu.neu.cs6510.sp25.t1.cli.validation.error.ValidationException;
import edu.neu.cs6510.sp25.t1.cli.validation.parser.YamlParser;
import edu.neu.cs6510.sp25.t1.common.logging.PipelineLogger;
import edu.neu.cs6510.sp25.t1.common.model.Pipeline;

/**
 * YamlPipelineValidator is the top-level validator that ensures the entire pipeline configuration is valid.
 * <p>
 * It validates:
 * - **YAML Parsing**: Ensures the YAML file is correctly formatted.
 * - **Pipeline Structure**: Uses `PipelineValidator` to check pipeline-level validation.
 * - **Job Validation**: Uses `JobValidator` to validate individual job definitions.
 * <p>
 * Usage:
 * - Call `YamlPipelineValidator.validatePipeline(filePath)` to validate a pipeline YAML file.
 */
public class YamlPipelineValidator {

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
      throw new ValidationException(filePath, 1, 1, "File not found.");
    }

    // Parse YAML into PipelineConfig
    Pipeline pipeline = YamlParser.parseYaml(yamlFile);

    // Validate pipeline structure using PipelineValidator
    PipelineValidator.validate(pipeline, filePath);

    // Validate job configurations
    JobValidator.validateJobs(pipeline.getStages(), filePath);

    PipelineLogger.info("Pipeline validation successful: " + filePath);
  }
}
