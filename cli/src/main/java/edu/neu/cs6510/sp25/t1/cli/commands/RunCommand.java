package edu.neu.cs6510.sp25.t1.cli.commands;

import edu.neu.cs6510.sp25.t1.cli.api.CliBackendClient;
import edu.neu.cs6510.sp25.t1.common.api.request.RunPipelineRequest;
import edu.neu.cs6510.sp25.t1.common.api.response.UpdateExecutionStateRequest;
import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;
import picocli.CommandLine;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * CLI command to execute a pipeline, either locally or remotely.
 */
@CommandLine.Command(name = "run", description = "Execute a pipeline (locally or via backend)")
public class RunCommand extends BaseCommand {

  private final CliBackendClient backendClient;
  private static final String DEFAULT_PIPELINE_PATH = ".pipelines/pipeline.yaml";
  private static final String PIPELINES_DIRECTORY = ".pipelines/";
  private static final String YAML_EXTENSION = ".yaml";

  @CommandLine.Option(names = "--local", description = "Run pipeline on local machine (logs execution state)")
  private boolean runLocally;

  @CommandLine.Option(names = "--pipeline", description = "Pipeline name to run (will look for .pipelines/<name>.yaml)")
  private String pipelineName;

  /**
   * Default constructor for Picocli.
   * Initializes with default backend client.
   */
  public RunCommand() {
    this.backendClient = new CliBackendClient("http://localhost:8080");
  }

  /**
   * Constructor for dependency injection.
   *
   * @param backendClient Backend client for API communication.
   */
  public RunCommand(CliBackendClient backendClient) {
    this.backendClient = backendClient;
  }

  @Override
  public Integer call() {
    System.out.println("Command Started: RunCommand");

    if (configFile == null || configFile.isEmpty()) {
      configFile = DEFAULT_PIPELINE_PATH;
      System.out.println("Using default pipeline configuration: " + DEFAULT_PIPELINE_PATH);
    }

    if (!validateInputs()) {
      return 2;
    }

    String runId = UUID.randomUUID().toString();
    System.out.println((runLocally ? "Running locally" : "Running remotely") + " | Run ID: " + runId);

    if (runLocally) {
      return executePipelineLocally(runId);
    } else {
      return executePipelineRemotely(runId);
    }
  }

  /**
   * Executes the pipeline **remotely** by sending a request to the backend.
   *
   * @param runId Unique execution ID.
   * @return Exit code (0 for success, 3 for failure).
   */
  private int executePipelineRemotely(String runId) {
    try {
      RunPipelineRequest request = new RunPipelineRequest("repoPath", branch, commit, configFile, false, Map.of(), configFile);
      backendClient.runPipeline(request);
      backendClient.updateExecutionState(new UpdateExecutionStateRequest(configFile, ExecutionStatus.SUCCESS));

      System.out.println("✅ Pipeline execution request sent to backend.");
      return 0;
    } catch (Exception e) {
      backendClient.updateExecutionState(new UpdateExecutionStateRequest(configFile, ExecutionStatus.FAILED));
      System.err.println("❌ Pipeline execution failed: " + e.getMessage());
      return 3;
    }
  }

  /**
   * Executes the pipeline **locally** and logs the results to a file.
   *
   * @param runId Unique execution ID.
   * @return Exit code (0 for success, 3 for failure).
   */
  private int executePipelineLocally(String runId) {
    try {
      backendClient.updateExecutionState(new UpdateExecutionStateRequest(configFile, ExecutionStatus.RUNNING));

      // Simulate local execution (backend should not handle this case)
      ExecutionStatus finalStatus = ExecutionStatus.SUCCESS;
      saveLocalExecutionReport(runId, finalStatus);

      backendClient.updateExecutionState(new UpdateExecutionStateRequest(configFile, finalStatus));
      System.out.println("✅ Local pipeline execution completed.");
      return 0;
    } catch (Exception e) {
      saveLocalExecutionReport(runId, ExecutionStatus.FAILED);
      backendClient.updateExecutionState(new UpdateExecutionStateRequest(configFile, ExecutionStatus.FAILED));
      System.err.println("❌ Local pipeline execution failed: " + e.getMessage());
      return 3;
    }
  }

  /**
   * Saves execution report locally for `--local` runs.
   *
   * @param runId  Unique execution ID.
   * @param status Execution status.
   */
  private void saveLocalExecutionReport(String runId, ExecutionStatus status) {
    try {
      File logFile = new File("logs/pipeline-run.log");
      if (!logFile.exists()) {
        logFile.getParentFile().mkdirs();
        logFile.createNewFile();
      }

      FileWriter writer = new FileWriter(logFile, true);
      writer.write(String.format("Run ID: %s | Status: %s | Pipeline: %s%n", runId, status, configFile));
      writer.close();
    } catch (IOException e) {
      System.err.println("⚠️ Failed to write local execution log: " + e.getMessage());
    }
  }

  /**
   * Process pipeline option
   */
  private void processPipelineOptions() {
    // Track if we're using a user-specified option to provide better error messages
    AtomicBoolean userSpecifiedOption = new AtomicBoolean(false);

    // Check if pipeline name is provided with --pipeline
    if (pipelineName != null && !pipelineName.isEmpty()) {
      // Only override configFile if it wasn't explicitly set
      if (configFile == null || configFile.isEmpty()) {
        configFile = PIPELINES_DIRECTORY + pipelineName + YAML_EXTENSION;
        System.out.println("Using pipeline configuration: " + configFile);
      } else {
        System.out.println("Both --file and --pipeline provided. Using --file: " + configFile);
      }
      userSpecifiedOption.set(true);
    }

    // If no options were provided, use the default
    if (configFile == null || configFile.isEmpty()) {
      configFile = DEFAULT_PIPELINE_PATH;
      System.out.println("Using default pipeline configuration: " + DEFAULT_PIPELINE_PATH);
    }
  }


  /**
   * Validates required inputs.
   *
   * @return true if inputs are valid, false otherwise.
   */
  protected boolean validateInputs() {
    if (configFile == null || configFile.isEmpty()) {
      logError("Missing required parameter: --file <pipeline.yaml>");
      return false;
    }

    if (!Files.isReadable(Paths.get(configFile))) {
      logError("Cannot read file: " + configFile);
      return false;
    }

    return true;
  }
}
