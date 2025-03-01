package edu.neu.cs6510.sp25.t1.cli.commands;

import edu.neu.cs6510.sp25.t1.cli.api.CliBackendClient;
import edu.neu.cs6510.sp25.t1.common.api.RunPipelineRequest;
import picocli.CommandLine;

@CommandLine.Command(name = "run", description = "Execute a pipeline")
public class RunCommand extends BaseCommand {

  private final CliBackendClient backendClient;

  @CommandLine.Option(names = "--local", description = "Run pipeline locally")
  private boolean local;

  @CommandLine.Option(names = "--pipeline", description = "Pipeline name to run")
  private String pipeline;

  /**
   * Default constructor (used in production).
   * Creates a new `BackendClient` to interact with the API.
   */
  public RunCommand() {
    this.backendClient = new CliBackendClient("http://localhost:8080"); // Default backend client
  }

  /**
   * Constructor for dependency injection (used for unit testing).
   *
   * @param backendClient The mocked backend client instance.
   */
  public RunCommand(CliBackendClient backendClient) {
    this.backendClient = backendClient; // Injected backend client for testing
  }

  @Override
  public Integer call() {
    // Validate required parameters
    if (pipeline == null || pipeline.isEmpty()) {
      System.err.println("Error: No pipeline configuration file provided.");
      return 2; // Invalid arguments
    }

    try {
      System.out.println("Executing pipeline with config: " + pipeline);

      // 🔹 Fix: Create a request object before calling `runPipeline()`
      RunPipelineRequest request = new RunPipelineRequest(pipeline);

      System.out.println("Sending request to backend...");
      String response = backendClient.runPipeline(request);
      System.out.println("Pipeline Execution Started:");
      System.out.println(formatOutput(response));

      return 0; // Success
    } catch (Exception e) {
      System.err.println("Failed to execute pipeline: " + e.getMessage());
      logger.error("Failed to execute pipeline", e);
      return 1; // General failure
    }
  }
}