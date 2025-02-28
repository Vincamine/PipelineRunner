package edu.neu.cs6510.sp25.t1.commands;

import edu.neu.cs6510.sp25.t1.api.BackendClient;
import picocli.CommandLine;

@CommandLine.Command(name = "check", description = "Validate the pipeline configuration file")
public class CheckCommand extends BaseCommand {
    @CommandLine.Parameters(index = "0", description = "Path to the pipeline configuration file")
    private String configFile;
    private final BackendClient backendClient;

    public CheckCommand(BackendClient backendClient) {
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
     * - `CheckCommand`: Validates the pipeline configuration file.
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

// package edu.neu.cs6510.sp25.t1.commands;

// import edu.neu.cs6510.sp25.t1.util.PipelineValidator;
// import edu.neu.cs6510.sp25.t1.validation.YamlPipelineValidator;
// import picocli.CommandLine.Command;
// import picocli.CommandLine.Option;

// import java.util.concurrent.Callable;

// /**
// * Command to validate a pipeline YAML file.
// *
// * This command ensures that the pipeline YAML file:
// * - Has a valid structure
// * - Contains no cyclic dependencies
// * - Defines valid job configurations
// */
// @Command(name = "check", description = "Validate a pipeline YAML file",
// mixinStandardHelpOptions = true)
// public class CheckCommand implements Callable<Boolean> {

// @Option(names = { "-f",
// "--file" }, description = "Path to the pipeline YAML file", required = false,
// defaultValue = ".pipelines/pipeline.yaml")
// private String yamlFilePath;

// private final PipelineValidator pipelineValidator;

// /**
// * Default constructor for CheckCommand.
// * Uses the default YamlPipelineValidator.
// *
// * @see YamlPipelineValidator
// * @see PipelineValidator
// */
// public CheckCommand() {
// this(new PipelineValidator(new YamlPipelineValidator()));
// }

// /**
// * Constructor for injecting a mock PipelineValidator \
// *
// * @param pipelineValidator pipeline validator instance
// */
// public CheckCommand(PipelineValidator pipelineValidator) {
// this.pipelineValidator = pipelineValidator;
// }

// /**
// * Validates the pipeline YAML file.
// *
// * @return true if the pipeline is valid, false otherwise
// */
// @Override
// public Boolean call() {
// final boolean isValid = pipelineValidator.validatePipelineFile(yamlFilePath);

// if (!isValid) {
// System.err.println("Pipeline validation failed. Please check your YAML
// file.");
// return false;
// }

// return true;
// }
// }
