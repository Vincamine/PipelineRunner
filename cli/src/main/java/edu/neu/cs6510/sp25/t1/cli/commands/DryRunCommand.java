package edu.neu.cs6510.sp25.t1.cli.commands;

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

import edu.neu.cs6510.sp25.t1.cli.api.CliBackendClient;
import picocli.CommandLine;

@CommandLine.Command(name = "dry-run", description = "Simulate pipeline execution")
public class DryRunCommand extends BaseCommand {

    private final CliBackendClient backendClient;

    /**
     * Default constructor using default BackendClient - for unit testing.
     */
    public DryRunCommand() {
        this.backendClient = new CliBackendClient("http://localhost:8080");
    }

    /**
     * Constructor for dependency injection (used for unit testing).
     *
     * @param backendClient The mocked backend client instance.
     */
    public DryRunCommand(CliBackendClient backendClient) {
        this.backendClient = backendClient;
    }

    /**
     * Executes the CLI command to interact with the CI/CD system.
     *
     * Picocli requires an integer return code to indicate success or failure:
     * - `0` -> Success: The command executed successfully.
     * - `1` -> General failure: An unexpected error occurred.
     * - `2` -> Invalid arguments: Handled automatically by Picocli.
     * - `3+` -> Custom error codes (e.g., `3` for validation errors, `4` for
     * network issues).
     *
     * This method communicates with the backend service to perform the requested
     * operation.
     * - `DryRunCommand`: Simulates execution and displays the execution order.
     *
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
            var response = backendClient.dryRunPipeline(configFile);

            if ("yaml".equalsIgnoreCase(outputFormat)) {
                YAMLMapper yamlMapper = new YAMLMapper();
                response = yamlMapper.writeValueAsString(response);
            }

            System.out.println("Pipeline Execution Plan:");
            System.out.println(response);

            return response.startsWith("Error") ? 3 : 0;
        } catch (Exception e) {
            logger.error("Failed to perform dry-run", e);
            return 1;
        }
    }
}