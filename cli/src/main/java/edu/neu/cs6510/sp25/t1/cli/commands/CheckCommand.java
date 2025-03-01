package edu.neu.cs6510.sp25.t1.cli.commands;

import edu.neu.cs6510.sp25.t1.cli.api.CliBackendClient;
import picocli.CommandLine;

@CommandLine.Command(name = "check", description = "Validate the pipeline configuration file")
public class CheckCommand extends BaseCommand {
  @CommandLine.Parameters(index = "0", description = "Path to the pipeline configuration file")
  private String configFile;
  private final CliBackendClient backendClient;

  public CheckCommand(CliBackendClient backendClient) {
    this.backendClient = backendClient;
  }

  /**
   * Executes the CLI command to interact with the CI/CD system.
   * <p>
   * Picocli requires an integer return code to indicate success or failure:
   * - `0` -> Success: The command executed successfully.
   * - `1` -> General failure: An unexpected error occurred.
   * - `2` -> Invalid arguments: Handled automatically by Picocli.
   * - `3+` -> Custom error codes (e.g., `3` for validation errors, `4` for
   * network issues).
   * <p>
   * This method communicates with the backend service to perform the requested
   * operation.
   * - `CheckCommand`: Validates the pipeline configuration file.
   * <p>
   * If successful, it prints a confirmation message. Otherwise, it displays
   * errors.
   */
  @Override
  public Integer call() {
    if (configFile == null || configFile.isEmpty()) {
      System.err.println("Error: No pipeline configuration file provided.");
      return 2;
    }
    try {
      var response = backendClient.checkPipelineConfig(configFile);

      if (response.isValid()) {
        System.out.println("Pipeline configuration is valid.");
        return 0;
      } else {
        System.out.println("Invalid pipeline configuration: " + response.getErrors());
        return 3;
      }
    } catch (Exception e) {
      logger.error("Failed to validate pipeline configuration", e);
      return 1;
    }
  }
}