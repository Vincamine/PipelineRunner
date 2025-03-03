package edu.neu.cs6510.sp25.t1.cli.commands;

import java.io.File;
import java.util.UUID;

import edu.neu.cs6510.sp25.t1.cli.api.CliBackendClient;
import edu.neu.cs6510.sp25.t1.common.api.response.UpdateExecutionStateRequest;
import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;
import edu.neu.cs6510.sp25.t1.worker.api.client.WorkerBackendClient;
import edu.neu.cs6510.sp25.t1.worker.executor.PipelineExecutor;
import edu.neu.cs6510.sp25.t1.worker.manager.DockerManager;
import picocli.CommandLine;

/**
 * CLI command to execute a pipeline, either locally or via backend.
 */
@CommandLine.Command(name = "run", description = "Execute a pipeline (locally or via backend)")
public class RunCommand extends BaseCommand {

  private final CliBackendClient backendClient;
  private final DockerManager dockerManager;
  private final WorkerBackendClient workerBackendClient;

  @CommandLine.Option(names = "--local", description = "Run pipeline on local machine (logs execution state)")
  private boolean runLocally;

  @CommandLine.Option(names = "--pipeline", description = "Pipeline name to run")
  private String pipeline;

  @CommandLine.Option(names = "--file", description = "Pipeline configuration file")
  private String configFile;

  public RunCommand() {
    this.backendClient = new CliBackendClient("http://localhost:8080");
    this.dockerManager = new DockerManager();
    this.workerBackendClient = new WorkerBackendClient();
  }

  public RunCommand(CliBackendClient backendClient, DockerManager dockerManager, WorkerBackendClient workerBackendClient) {
    this.backendClient = backendClient;
    this.dockerManager = dockerManager;
    this.workerBackendClient = workerBackendClient;
  }

  @Override
  public Integer call() {
    if (!validateInputs()) {
      return 2;
    }

    try {
      File pipelineConfigFile = new File(configFile);
      return executePipeline(pipelineConfigFile);
    } catch (Exception e) {
      logError("Error processing pipeline: " + e.getMessage());
      return 1;
    }
  }

  /**
   * Executes the pipeline using `PipelineExecutor`, ensuring centralized execution logic.
   *
   * @param pipelineConfigFile The YAML pipeline configuration file.
   * @return Exit code (0 for success, 3 for failure).
   */
  private int executePipeline(File pipelineConfigFile) {
    String runId = UUID.randomUUID().toString();
    logInfo("Executing pipeline | Run ID: " + runId);

    backendClient.updateExecutionState(new UpdateExecutionStateRequest(configFile, ExecutionStatus.RUNNING));

    try {
      PipelineExecutor pipelineExecutor = new PipelineExecutor(dockerManager, workerBackendClient);
      pipelineExecutor.execute(pipelineConfigFile);

      backendClient.updateExecutionState(new UpdateExecutionStateRequest(configFile, ExecutionStatus.SUCCESS));
      logInfo("Pipeline execution completed successfully.");
      return 0;
    } catch (Exception e) {
      backendClient.updateExecutionState(new UpdateExecutionStateRequest(configFile, ExecutionStatus.FAILED));
      logError("Pipeline execution failed: " + e.getMessage());
      return 3;
    }
  }
}

