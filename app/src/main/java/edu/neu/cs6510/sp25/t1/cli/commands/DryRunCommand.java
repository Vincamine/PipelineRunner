package edu.neu.cs6510.sp25.t1.cli.commands;

import org.yaml.snakeyaml.Yaml;
import edu.neu.cs6510.sp25.t1.service.PipelineExecutionOrderGenerator;
import edu.neu.cs6510.sp25.t1.util.PipelineValidator;
import edu.neu.cs6510.sp25.t1.validation.YamlPipelineValidator;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Command to perform a dry run of a pipeline YAML file.
 * 
 * This command:
 * - Validates the pipeline YAML structure
 * - Checks for dependency issues
 * - Determines and prints the execution order
 */
@Command(name = "dry-run", description = "Dry run a pipeline file", mixinStandardHelpOptions = true)
public class DryRunCommand implements Callable<Boolean> {

  /**
   * Path to the pipeline YAML file.
   */
  @Option(names = { "-f", "--file" }, description = "Path to the pipeline YAML file", required = true)
  String yamlFilePath;

  /**
   * Executes the dry-run command to validate and print execution order.
   *
   * @return true if validation succeeds and execution order is printed, false
   *         otherwise.
   */
  @Override
  public Boolean call() {
    final YamlPipelineValidator yamlPipelineValidator = new YamlPipelineValidator();
    final PipelineValidator pipelineValidator = new PipelineValidator(yamlPipelineValidator);

    // Validate pipeline structure
    if (!pipelineValidator.validatePipelineFile(yamlFilePath)) {
      System.err.println("Validation failed. Exiting.");
      return false;
    }

    try {
      // Generate execution order
      final PipelineExecutionOrderGenerator executionOrderGenerator = new PipelineExecutionOrderGenerator();
      final Map<String, Map<String, Object>> executionOrder = executionOrderGenerator
          .generateExecutionOrder(yamlFilePath);

      if (executionOrder.isEmpty()) {
        System.err.println("No valid execution order. Check for dependency issues.");
        return false;
      }

      // Convert to YAML format for better readability
      final Yaml yaml = new Yaml();
      final String yamlOutput = yaml.dump(executionOrder);

      System.out.println(yamlOutput);

      return true;
    } catch (Exception e) {
      System.err.println("Error generating execution order: " + e.getMessage());
      return false;
    }
  }
}
