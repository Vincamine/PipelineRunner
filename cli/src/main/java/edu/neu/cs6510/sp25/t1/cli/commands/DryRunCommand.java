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

// package edu.neu.cs6510.sp25.t1.commands;

// import org.yaml.snakeyaml.Yaml;

// import edu.neu.cs6510.sp25.t1.service.PipelineExecutionOrderGenerator;
// import edu.neu.cs6510.sp25.t1.util.PipelineValidator;
// import edu.neu.cs6510.sp25.t1.validation.YamlPipelineValidator;
// import picocli.CommandLine.Command;
// import picocli.CommandLine.Option;

// import java.util.Map;
// import java.util.concurrent.Callable;

// /**
// * Command to perform a dry run of a pipeline YAML file.
// *
// * This command:
// * - Validates the pipeline YAML structure
// * - Checks for dependency issues
// * - Determines and prints the execution order
// */
// @Command(name = "dry-run", description = "Dry run a pipeline file",
// mixinStandardHelpOptions = true)
// public class DryRunCommand implements Callable<Boolean> {

// /**
// * Path to the pipeline YAML file.
// */
// @Option(names = { "-f", "--file" }, description = "Path to the pipeline YAML
// file", required = true)
// String yamlFilePath;

// /**
// * Executes the dry-run command to validate and print execution order.
// *
// * @return true if validation succeeds and execution order is printed, false
// * otherwise.
// */
// @Override
// public Boolean call() {
// final YamlPipelineValidator yamlPipelineValidator = new
// YamlPipelineValidator();
// final PipelineValidator pipelineValidator = new
// PipelineValidator(yamlPipelineValidator);

// // Validate pipeline structure
// if (!pipelineValidator.validatePipelineFile(yamlFilePath)) {
// System.err.println("Validation failed. Exiting.");
// return false;
// }

// try {
// // Generate execution order
// final PipelineExecutionOrderGenerator executionOrderGenerator = new
// PipelineExecutionOrderGenerator();
// final Map<String, Map<String, Object>> executionOrder =
// executionOrderGenerator
// .generateExecutionOrder(yamlFilePath);

// if (executionOrder.isEmpty()) {
// System.err.println("No valid execution order. Check for dependency issues.");
// return false;
// }

// // Convert to YAML format for better readability
// final Yaml yaml = new Yaml();
// final String yamlOutput = yaml.dump(executionOrder);

// System.out.println(yamlOutput);

// return true;
// } catch (Exception e) {
// System.err.println("Error generating execution order: " + e.getMessage());
// return false;
// }
// }
// }
