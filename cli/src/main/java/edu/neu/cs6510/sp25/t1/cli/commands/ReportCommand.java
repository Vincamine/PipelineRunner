package edu.neu.cs6510.sp25.t1.cli.commands;

import edu.neu.cs6510.sp25.t1.cli.api.CliBackendClient;
import picocli.CommandLine;

/**
 * CLI command for fetching pipeline execution history.
 * <p>
 * This command allows users to retrieve past pipeline executions from the CI/CD system.
 * It supports:
 * - Listing all available pipelines
 * - Fetching execution history of a specific pipeline
 * - Fetching details of a specific pipeline run
 * <p>
 * The output format can be specified as plaintext, JSON, or YAML.
 * <p>
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
   */
  @CommandLine.Option(names = "--list-pipelines", description = "List all pipelines")
  private boolean listPipelines;

  /**
   * Specifies the pipeline name whose execution history is being queried.
   */
  @CommandLine.Option(names = "--pipeline", description = "Specify pipeline name")
  private String pipeline;

  /**
   * Specifies a particular run ID of the pipeline.
   */
  @CommandLine.Option(names = "--run", description = "Specify run ID")
  private String runId;

  /**
   * Default constructor using the default BackendClient.
   */
  public ReportCommand() {
    this.backendClient = new CliBackendClient("http://localhost:8080");
  }

  /**
   * Constructor for dependency injection (used for unit testing).
   *
   * @param backendClient The backend client used to fetch pipeline execution data.
   */
  public ReportCommand(CliBackendClient backendClient) {
    this.backendClient = backendClient;
  }

  /**
   * Executes the report command by retrieving pipeline execution data based on the given parameters.
   * <p>
   * Behavior:
   * - If `--list-pipelines` is provided, it returns a list of all pipelines.
   * - If `--pipeline` is provided without `--run`, it returns execution history for that pipeline.
   * - If both `--pipeline` and `--run` are provided, it returns details of that specific run.
   * - If neither `--list-pipelines` nor `--pipeline` is provided, an error message is displayed.
   * <p>
   * The response is formatted based on the `--output` option (plaintext, JSON, YAML).
   *
   * @return Exit code:
   *         - 0: Success
   *         - 1: General error
   *         - 2: Missing required argument
   */
  @Override
  public Integer call() {
    if (validateInputs()) {// the git repo check is done here
      return 2; // Exit code for missing file or wrong directory
    }
    if (!listPipelines && (pipeline == null || pipeline.isEmpty())) {
      logError("Error: Missing required option '--pipeline=<pipeline>' or '--list-pipelines'");
      return 2; // Picocli's standard exit code for missing arguments
    }

    try {
      String response;

      if (listPipelines) {
        response = backendClient.getAllPipelines();
      } else {
        response = (runId == null)
                ? backendClient.getPipelineExecutions(pipeline, outputFormat)
                : backendClient.getPipelineExecutions(pipeline + "/" + runId, outputFormat);
      }

      logInfo("Report successfully retrieved.");
      System.out.println(formatOutput(response));
      return 0;

    } catch (Exception e) {
      logError("Failed to retrieve report: " + e.getMessage());
      return 1; // General failure exit code
    }
  }
}
