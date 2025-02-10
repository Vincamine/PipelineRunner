package edu.neu.cs6510.sp25.t1.cli.commands;

import edu.neu.cs6510.sp25.t1.cli.util.ErrorHandler;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Callable;

import edu.neu.cs6510.sp25.t1.cli.validation.YamlPipelineValidator;

/**
 * Command to validate a pipeline YAML file.
 * Checks structure, dependencies, jobs, and overall pipeline configuration.
 */
@Command(
    name = "check",
    description = "Validate a pipeline YAML file",
    mixinStandardHelpOptions = true
)
public class CheckCommand implements Callable<Integer> {
  @Parameters(index = "0", description = "Path to the pipeline YAML file")
  private String yamlFilePath;

  /**
   * Executes the check command to validate a pipeline YAML file.
   *
   * @return 0 if validation succeeds, 1 if validation fails
   */
  @Override
  public Integer call() {
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
        return 1;
      }

      // Verify parent directory is 'pipelines'
      final Path parentDir = yamlPath.getParent();
      if (parentDir == null || !parentDir.getFileName().toString().equals("pipelines")) {
        System.err.println(ErrorHandler.formatMissingFieldError(
            location,
            "YAML file must be inside the 'pipelines/' folder"
        ));
        return 1;
      }

      // Validate pipeline configuration
      final YamlPipelineValidator pipelineValidator = new YamlPipelineValidator();
      final boolean isValid = pipelineValidator.validatePipeline(yamlPath.toString());

      if (isValid) {
        System.out.println("Pipeline validation successful: " + yamlPath);
        return 0;
      } else {
        // Note: specific error messages are already printed by the validator
        return 1;
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
      return 1;
    }
  }
}