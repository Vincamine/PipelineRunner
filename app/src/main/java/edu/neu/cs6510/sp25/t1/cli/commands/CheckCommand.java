package edu.neu.cs6510.sp25.t1.cli.commands;

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
    final Path yamlPath = Paths.get(yamlFilePath);

    // Check if the file actually exists
    if (!Files.exists(yamlPath)) {
      System.err.println("Error: YAML file not found: " + yamlFilePath);
      return 1;
    }

    // Check if the path contains "pipelines/"
    if (!isInsidePipelinesFolder(yamlPath)) {
      System.err.println("Error: YAML file must be inside the 'pipelines/' folder.");
      return 1;
    }

    try {
      final YamlPipelineValidator pipelineValidator = new YamlPipelineValidator();

      final boolean isValid = pipelineValidator.validatePipeline(yamlPath.toString());

      if (isValid) {
        System.out.println("Pipeline validation successful: " + yamlPath);
        return 0;
      } else {
        System.err.println("Pipeline validation failed: " + yamlPath);
        return 1;
      }

    } catch (Exception e) {
      System.err.println("Error validating pipeline: " + e.getMessage());
      return 1;
    }
  }

  /**
   * Checks whether the given file is inside the 'pipelines/' directory.
   *
   * @param path The absolute or relative path to the YAML file.
   * @return {@code true} if the file is inside 'pipelines/', {@code false} otherwise.
   */
  private boolean isInsidePipelinesFolder(Path path) {
    // Normalize path for consistent directory structure
    final Path normalizedPath = path.toAbsolutePath().normalize();

    // Extract parent directory name
    final Path parentDir = normalizedPath.getParent();

    // Ensure parent directory is not null before checking
    if (parentDir != null && parentDir.getFileName() != null) {
      return parentDir.getFileName().toString().equals("pipelines");
    }

    return false;
  }
}