package edu.neu.cs6510.sp25.t1.cli.commands;

import edu.neu.cs6510.sp25.t1.util.PipelineValidator;
import edu.neu.cs6510.sp25.t1.validation.YamlPipelineValidator;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.concurrent.Callable;

/**
 * Command to validate a pipeline YAML file.
 * <p>
 * This command ensures that the pipeline YAML file:
 * <ul>
 *     <li>Has a valid structure</li>
 *     <li>Contains no cyclic dependencies</li>
 *     <li>Defines valid job configurations</li>
 * </ul>
 * </p>
 */
@Command(
    name = "check",
    description = "Validate a pipeline YAML file",
    mixinStandardHelpOptions = true
)
public class CheckCommand implements Callable<Boolean> {

  @Option(
      names = {"-f", "--file"},
      description = "Path to the pipeline YAML file",
      required = false,
      defaultValue = ".pipelines/pipeline.yaml"
  )
  private String yamlFilePath;

  /**
   * Executes the check command to validate a pipeline YAML file.
   *
   * @return {@code true} if validation succeeds, {@code false} if validation fails.
   */
  @Override
  public Boolean call() {
    final YamlPipelineValidator yamlPipelineValidator = new YamlPipelineValidator();
    final PipelineValidator pipelineValidator = new PipelineValidator(yamlPipelineValidator);

    // Validate pipeline file
    final boolean isValid = pipelineValidator.validatePipelineFile(yamlFilePath);

    if (!isValid) {
      System.err.println("❌ Pipeline validation failed. Please check your YAML file.");
    }

    return isValid;
  }
}
