package edu.neu.cs6510.sp25.t1.cli.commands;

import edu.neu.cs6510.sp25.t1.cli.util.ErrorHandler;
import edu.neu.cs6510.sp25.t1.cli.util.PipelineExecutionOrderGenerator;
import edu.neu.cs6510.sp25.t1.cli.util.PipelineValidator;
import edu.neu.cs6510.sp25.t1.cli.validation.YamlPipelineValidator;
import org.yaml.snakeyaml.Yaml;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Command to Dry run a pipeline YAML file.
 * Checks structure, dependencies, jobs, and overall pipeline configuration.
 * print the executions order in Yaml format
 */
@Command(
    name = "dry-run",
    description = "Dry run a pipeline file",
    mixinStandardHelpOptions = true
)
public class DryRunCommand implements Callable<Boolean> {
  @Option(
      names = {"-f", "--file"},
      description = "Path to the pipeline YAML file",
      required = true
  )

  private String yamlFilePath;

  /**
   * Executes the dry-run command to validate and print execution order.
   *
   * @return true if validation succeeds and execution order is printed, false otherwise
   */
  @Override
  public Boolean call() {
    YamlPipelineValidator yamlPipelineValidator = new YamlPipelineValidator();
    PipelineValidator pipelineValidator = new PipelineValidator(yamlPipelineValidator);
    if (!pipelineValidator.validatePipelineFile(yamlFilePath)) {
      return false;
    }
    try {
      // Generate execution order
      PipelineExecutionOrderGenerator executionOrderGenerator = new PipelineExecutionOrderGenerator();
      Map<String, Map<String, Object>> executionOrder = executionOrderGenerator.generateExecutionOrder(yamlFilePath);

      // Print execution order as YAML
      Yaml yaml = new Yaml();
      System.out.println(yaml.dump(executionOrder));

      return true;
    } catch (Exception e) {
      System.err.println("Error generating execution order: " + e.getMessage());
      return false;
    }
  }
}
