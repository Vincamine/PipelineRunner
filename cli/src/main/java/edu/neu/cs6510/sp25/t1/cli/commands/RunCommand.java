package edu.neu.cs6510.sp25.t1.cli.commands;

import java.io.File;

import edu.neu.cs6510.sp25.t1.cli.api.CliBackendClient;
import edu.neu.cs6510.sp25.t1.common.api.RunPipelineRequest;
import edu.neu.cs6510.sp25.t1.common.config.PipelineConfig;
import edu.neu.cs6510.sp25.t1.common.parser.YamlParser;
import edu.neu.cs6510.sp25.t1.common.validation.PipelineValidator;
import edu.neu.cs6510.sp25.t1.common.validation.ValidationException;
import picocli.CommandLine;

/**
 * Command to run a pipeline based on a YAML file.
 * Parse and validate before sending to backend.
 */
@CommandLine.Command(name = "run", description = "Execute a pipeline")
public class RunCommand extends BaseCommand {

  private final CliBackendClient backendClient;

  /**
   * Default constructor using default BackendClient.
   */
  public RunCommand() {
    this.backendClient = new CliBackendClient("http://localhost:8080");
  }

  /**
   * Constructor for dependency injection (used for unit testing).
   *
   * @param backendClient The mocked backend client instance.
   */
  public RunCommand(CliBackendClient backendClient) {
    this.backendClient = backendClient;
  }

  /**
   * Executes the CLI command to interact with the CI/CD system.
   *
   * @return 0 if successful, 1 if an error occurred, 2 if no file provided, 3 if validation failed; required by picocli.
   */
  @Override
  public Integer call() {
    if (configFile == null || configFile.isEmpty()) {
      System.err.println("Error: No pipeline configuration file provided.");
      return 2;
    }

    try {
      File yamlFile = new File(configFile);
      if (!yamlFile.exists()) {
        System.err.println("Error: YAML file not found at " + configFile);
        return 2;
      }

      // Parse YAML
      PipelineConfig pipelineConfig = YamlParser.parseYaml(yamlFile);

      // Validate pipeline structure
      PipelineValidator.validate(pipelineConfig);

      // Create RunPipelineRequest object
      RunPipelineRequest request = new RunPipelineRequest(pipelineConfig.getName());

      // Send request to backend
      String response = backendClient.runPipeline(request);
      System.out.println("Pipeline Execution Started:");
      System.out.println(response);
      return 0;

    } catch (ValidationException e) {
      System.err.println("Validation Error: " + e.getMessage());
      return 3;
    } catch (Exception e) {
      System.err.println("Failed to process pipeline: " + e.getMessage());
      return 1;
    }
  }
}
