package edu.neu.cs6510.sp25.t1.cli.commands;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.nio.file.Path;
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
  private Path yamlPath;

  /**
   * Executes the check command to validate a pipeline YAML file.
   *
   * @return 0 if validation succeeds, 1 if validation fails
   */
  @Override
  public Integer call() {
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
}