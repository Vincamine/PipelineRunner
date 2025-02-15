package edu.neu.cs6510.sp25.t1.cli.util;

import edu.neu.cs6510.sp25.t1.cli.validation.YamlPipelineValidator;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Utility class to validate pipeline YAML files and ensure they are correctly formatted.
 */
public class PipelineValidator {
  private final YamlPipelineValidator yamlPipelineValidator;

  /**
   * Constructor for injecting a YamlPipelineValidator instance.
   *
   * @param yamlPipelineValidator The YAML validator instance
   */
  public PipelineValidator(YamlPipelineValidator yamlPipelineValidator) {
    this.yamlPipelineValidator = yamlPipelineValidator;
  }

  /**
   * Validates a pipeline YAML file.
   *
   * @param yamlFilePath The path to the YAML file
   * @return true if the file is valid, false otherwise
   */
  public boolean validatePipelineFile(String yamlFilePath) {
    try {
      // Normalize path for consistent processing
      final Path yamlPath = Paths.get(yamlFilePath).toAbsolutePath().normalize();

      // Create location for error reporting
      final ErrorHandler.Location location = new ErrorHandler.Location(
          yamlPath.getFileName().toString(),
          1,
          1,
          "root"
      );

      // Check if file exists
      if (!Files.exists(yamlPath)) {
        System.err.println(ErrorHandler.formatMissingFieldError(
            location,
            "YAML file not found: " + yamlFilePath
        ));
        return false;
      }

      // Verify parent directory is '.pipelines'
      final Path parentDir = yamlPath.getParent();
      if (parentDir == null || !".pipelines".equals(parentDir.getFileName().toString())) {
        System.err.println(ErrorHandler.formatMissingFieldError(
            location,
            "YAML file must be inside the '.pipelines/' folder"
        ));
        return false;
      }

      // Validate pipeline configuration
      final boolean isValid = yamlPipelineValidator.validatePipeline(yamlPath.toString());

      if (isValid) {
        System.out.println("Pipeline validation successful: " + yamlPath);
        return true;
      } else {
        // Note: specific error messages are already printed by the validator
        return false;
      }

    } catch (Exception e) {
      // Create error location for the exception
      final ErrorHandler.Location errorLocation = new ErrorHandler.Location(
          yamlFilePath,
          1,
          1,
          "error"
      );
      System.err.println(ErrorHandler.formatMissingFieldError(
          errorLocation,
          e.getMessage()
      ));
      return false;
    }
  }
}
