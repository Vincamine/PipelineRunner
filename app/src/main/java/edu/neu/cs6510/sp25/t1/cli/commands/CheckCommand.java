package edu.neu.cs6510.sp25.t1.cli.commands;

import edu.neu.cs6510.sp25.t1.cli.util.PipelineValidator;
import edu.neu.cs6510.sp25.t1.cli.validation.YamlPipelineValidator;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.concurrent.Callable;



/**
 * Command to validate a pipeline YAML file.
 * Checks structure, dependencies, jobs, and overall pipeline configuration.
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
      required = true
  )
  private String yamlFilePath;

  /**
   * Executes the check command to validate a pipeline YAML file.
   *
   * @return true if validation succeeds, false if validation fails
   */
  @Override
  public Boolean call() {
    YamlPipelineValidator yamlPipelineValidator = new YamlPipelineValidator();
    PipelineValidator pipelineValidator = new PipelineValidator(yamlPipelineValidator);

    // Call instance method instead of static
    boolean isValid = pipelineValidator.validatePipelineFile(yamlFilePath);

    if (isValid) {
      System.out.println("Pipeline validation successful: " + yamlFilePath);
    }

    return isValid;
  }
}
