package edu.neu.cs6510.sp25.t1.commands;

import edu.neu.cs6510.sp25.t1.api.CliBackendClient;
import picocli.CommandLine;

/**
 * CLI command for fetching pipeline execution history.
 *
 * This command allows users to retrieve past pipeline executions from the CI/CD
 * system.
 * It supports:
 * - Listing all available pipelines
 * - Fetching execution history of a specific pipeline
 * - Fetching details of a specific pipeline run
 *
 * The output format can be specified as plaintext, JSON, or YAML.
 *
 * Future Enhancements:
 * - Support querying execution history for a specific stage
 * - Support querying execution history for a specific job
 * - Include execution logs in the report
 */
@CommandLine.Command(name = "report", description = "Retrieve past pipeline executions")
public class ReportCommand extends BaseCommand {

    private final CliBackendClient backendClient;

    /**
     * Lists all pipelines available in the system.
     * Required by Picocli.
     */
    @CommandLine.Option(names = "--list-pipelines", description = "List all pipelines")
    private boolean listPipelines;

    /**
     * Specifies the pipeline name whose execution history is being queried.
     * Required by Picocli.
     */
    @CommandLine.Option(names = "--pipeline", description = "Specify pipeline name")
    private String pipeline;

    /**
     * Specifies a particular run ID of the pipeline.
     * Required by Picocli.
     */
    @CommandLine.Option(names = "--run", description = "Specify run ID")
    private String runId;

    /**
     * Constructs a new ReportCommand with a reference to the backend client.
     *
     * @param backendClient The backend client used to fetch pipeline execution
     *                      data.
     */
    public ReportCommand(CliBackendClient backendClient) {
        this.backendClient = backendClient;
    }

    /**
     * Executes the report command by retrieving pipeline execution data based on
     * the given parameters.
     *
     * Behavior:
     * - If `--list-pipelines` is provided, it returns a list of all pipelines.
     * - If `--pipeline` is provided without `--run`, it returns execution history
     * for that pipeline.
     * - If both `--pipeline` and `--run` are provided, it returns details of that
     * specific run.
     * - If none of the above are provided, an error message is displayed.
     *
     * The response is formatted based on the `--output` option (plaintext, JSON,
     * YAML).
     *
     * @return Exit code: 0 if successful, 1 if an error occurs.
     */
    @Override
    public Integer call() {
        try {
            String response;

            if (listPipelines) {
                response = backendClient.getAllPipelines();
            } else if (pipeline != null) {
                if (runId == null) {
                    response = backendClient.getPipelineExecutions(pipeline, outputFormat);
                } else {
                    response = backendClient.getPipelineExecutions(pipeline + "/" + runId, outputFormat);
                }
            } else {
                // Picocli now automatically handles missing required arguments
                System.err.println("Invalid command. Use --help for options.");
                return 1;
            }

            // Uses BaseCommand’s formatting (plaintext, JSON, YAML)
            System.out.println(formatOutput(response));
            return 0;
        } catch (Exception e) {
            logger.error("Failed to retrieve report", e);
            System.err.println("Error fetching report: " + e.getMessage());
            return 1;
        }
    }

}
