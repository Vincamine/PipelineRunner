package edu.neu.cs6510.sp25.t1.commands;

import edu.neu.cs6510.sp25.t1.api.RunPipelineRequest;
import edu.neu.cs6510.sp25.t1.api.CliBackendClient;
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
            RunPipelineRequest request = new RunPipelineRequest(repo, branch, commit, pipeline, local);
            var response = backendClient.runPipeline(request);

            System.out.println("Pipeline Execution Started:");
            System.out.println(formatOutput(response));

            return 0; // Success
        } catch (Exception e) {
            logger.error("Failed to execute pipeline", e);
            return 1; // General failure
        }
    }
}


// package edu.neu.cs6510.sp25.t1.commands;

// import edu.neu.cs6510.sp25.t1.util.ErrorHandler;
// import edu.neu.cs6510.sp25.t1.util.GitValidator;
// import edu.neu.cs6510.sp25.t1.validation.YamlPipelineValidator;
// import picocli.CommandLine.Command;
// import picocli.CommandLine.Option;

// import java.io.IOException;
// // import java.nio.file.Paths;
// // import java.util.List;

// /**
// * CLI command to trigger a CI/CD pipeline execution.
// * This command executes a pipeline either locally or remotely.
// */
// @Command(name = "run", description = "Trigger CI/CD pipeline execution")
// /**
// * RunCommand class to handle the execution of a CI/CD pipeline.
// */
// public class RunCommand implements Runnable {

// /**
// * Flag to indicate whether to run the pipeline locally.
// */
// @Option(names = "--local", description = "Run the pipeline on the local
// machine")
// private boolean isLocalRun;

// /**
// * Path to the repository (local or remote).
// */
// @Option(names = "--repo", description = "Repository location (local path or
// remote HTTPS URL)")
// private String repo;

// /**
// * Path to the pipeline configuration file.
// */
// @Option(names = "--file", description = "Path to the pipeline configuration
// file", required = true)
// private String pipelineFile;

// @SuppressWarnings("unused")
// private final YamlPipelineValidator validator;

// /**
// * Default constructor.
// */
// public RunCommand() {
// this(new YamlPipelineValidator());
// }

// /**
// * Constructor with a validator for testing.
// *
// * @param validator
// */
// public RunCommand(YamlPipelineValidator validator) {
// if (validator == null) {
// throw new IllegalArgumentException("Validator cannot be null.");
// }
// this.validator = validator;
// }

// @Override
// public void run() {
// System.out.println("CI/CD pipeline execution started.");

// try {
// System.out.println("isLocalRun: " + isLocalRun);
// if (isLocalRun) {
// executeLocalRun();
// } else {
// System.err.println("Error: Remote execution is not supported without a
// backend.");
// throw new UnsupportedOperationException("Remote execution is not
// supported.");
// }
// } catch (Exception e) {
// ErrorHandler.reportError(e.getMessage());
// throw new RuntimeException("Pipeline execution failed.", e);
// }
// }

// /**
// * Executes the pipeline locally.
// */
// private void executeLocalRun() throws IOException {
// System.out.println("Validating local repository...");

// if (!GitValidator.isGitRepository()) {
// throw new IllegalStateException("Error: Not a valid Git repository.");
// }

// GitValidator.validateGitRepo();

// System.out.println("validate result:"+!validatePipelineFile(pipelineFile));
// if (!validatePipelineFile(pipelineFile)) {
// throw new IllegalArgumentException("Pipeline file validation failed.");
// }

// System.out.println("Pipeline validation successful.");
// System.out.println("Executing pipeline...");

// // executeLocalJobs();

// System.out.println("Pipeline execution complete.");
// }

// /**
// * Validates the pipeline YAML file.
// *
// * @param filePath The pipeline file path.
// * @return true if valid, false otherwise.
// */
// private boolean validatePipelineFile(String filePath) {
// final YamlPipelineValidator validator = new YamlPipelineValidator();
// if (!validator.validatePipeline(filePath)) {
// System.err.println("Pipeline validation failed.");
// return false;
// }
// return true;
// }

// // /**
// // * Simulates local execution of jobs.
// // *
// // * @throws RuntimeException if job execution is interrupted.
// // */
// // private void executeLocalJobs() throws IOException {

// // // Extract the YAML file name (without extension)
// // String yamlFileName =
// Paths.get(pipelineFile).getFileName().toString().replace(".yml",
// "").replace(".yaml", "");
// // System.out.println("Pipeline validation successful.");

// // // Start the Docker container for the pipeline execution
// // DockerRunner dockerRunner = new DockerRunner(yamlFileName);

// // // Extract pipeline details from the YAML file
// // PipelineParser parser = new PipelineParser(pipelineFile,
// dockerRunner.getDockerClient());
// // String pipelineName = parser.getPipelineName();
// // List<StageExecutor> stages = parser.getStages();

// // PipelineExecutor pipelineExecutor = new PipelineExecutor(pipelineName,
// stages, dockerRunner);

// // System.out.println("Executing pipeline: " + pipelineName);
// // pipelineExecutor.execute();
// // }

// }
