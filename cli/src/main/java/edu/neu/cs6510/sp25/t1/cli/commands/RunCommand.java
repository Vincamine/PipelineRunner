package edu.neu.cs6510.sp25.t1.cli.commands;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import edu.neu.cs6510.sp25.t1.cli.api.CliBackendClient;
import edu.neu.cs6510.sp25.t1.common.api.JobRequest;
import edu.neu.cs6510.sp25.t1.common.api.RunPipelineRequest;
import edu.neu.cs6510.sp25.t1.common.api.UpdateExecutionStateRequest;
import edu.neu.cs6510.sp25.t1.common.config.PipelineConfig;
import edu.neu.cs6510.sp25.t1.common.runtime.ExecutionState;
import edu.neu.cs6510.sp25.t1.common.runtime.JobRunState;
import edu.neu.cs6510.sp25.t1.common.runtime.PipelineRunState;
import edu.neu.cs6510.sp25.t1.common.runtime.StageRunState;
import edu.neu.cs6510.sp25.t1.common.validation.error.ValidationException;
import picocli.CommandLine;

/**
 * CLI command to execute a pipeline, either locally or through the backend.
 *
 * <p><b>Local Execution:</b> Runs the pipeline locally but still logs execution state
 * to the backend for tracking and reporting.</p>
 *
 * <p><b>Backend Execution:</b> Delegates execution entirely to the backend system.</p>
 */
@CommandLine.Command(name = "run", description = "Execute a pipeline (locally or via backend)")
public class RunCommand extends BaseCommand {

  private final CliBackendClient backendClient;

  /**
   * Flag to force execution on the local machine while still logging execution state to the backend.
   */
  @CommandLine.Option(names = "--local", description = "Run pipeline on local machine (logs execution state)")
  private boolean runLocally;

  /**
   * Default constructor initializes backend client.
   */
  public RunCommand() {
    this.backendClient = new CliBackendClient("http://localhost:8080");
  }

  /**
   * Constructor for dependency injection (unit testing).
   *
   * @param backendClient The backend client instance.
   */
  public RunCommand(CliBackendClient backendClient) {
    this.backendClient = backendClient;
  }

  /**
   * Executes the pipeline based on the provided options.
   *
   * @return Exit code:
   * <ul>
   *   <li>0 - Success</li>
   *   <li>1 - General failure</li>
   *   <li>2 - Missing file or incorrect directory</li>
   *   <li>3 - Validation failure</li>
   * </ul>
   */
  @Override
  public Integer call() {
    if (!validateInputs()) {
      return 2;
    }

    try {
      // Load pipeline configuration
      PipelineConfig pipelineConfig = loadAndValidatePipelineConfig();

      if (runLocally) {
        return executeLocally(pipelineConfig);
      } else {
        return executeRemotely(pipelineConfig);
      }
    } catch (ValidationException e) {
      logError(String.format("%s: Validation Error: %s", configFile, e.getMessage()));
      return 3;
    } catch (Exception e) {
      logError(String.format("%s: Error processing pipeline: %s", configFile, e.getMessage()));
      return 1;
    }
  }

  /**
   * Executes the pipeline on the local machine while still logging execution state to the backend.
   *
   * @param pipelineConfig The pipeline configuration.
   * @return Exit code (0 for success, 3 for failure).
   */
  private int executeLocally(PipelineConfig pipelineConfig) {
    logInfo("Running pipeline locally: " + pipelineConfig.getName());

    // Initialize pipeline execution state
    PipelineRunState pipelineState = new PipelineRunState(pipelineConfig.getName());
    backendClient.updateExecutionState(new UpdateExecutionStateRequest(pipelineConfig.getName(), ExecutionState.RUNNING));

    // Iterate over each stage and execute sequentially
    for (var stageConfig : pipelineConfig.getStages()) {
      StageRunState stageState = new StageRunState(stageConfig,
              stageConfig.getJobs().stream()
                      .map(JobRunState::new)
                      .collect(Collectors.toList()));

      logInfo("Executing Stage: " + stageConfig.getName());

      // Execute each job in the stage
      for (JobRunState jobState : stageState.getJobExecutions()) {
        backendClient.updateExecutionState(new UpdateExecutionStateRequest(jobState.getJobName(), ExecutionState.RUNNING));

        if (!executeJob(jobState)) {
          backendClient.updateExecutionState(new UpdateExecutionStateRequest(jobState.getJobName(), ExecutionState.FAILED));

          if (!jobState.isAllowFailure()) {
            logError("Job failed: " + jobState.getJobName() + " - Stopping pipeline.");
            backendClient.updateExecutionState(new UpdateExecutionStateRequest(pipelineConfig.getName(), ExecutionState.FAILED));
            return 3; // Fail if job is not allowed to fail
          }
        }

        backendClient.updateExecutionState(new UpdateExecutionStateRequest(jobState.getJobName(), ExecutionState.SUCCESS));
      }

      stageState.updateStatus();
      pipelineState.updateState();
    }

    backendClient.updateExecutionState(new UpdateExecutionStateRequest(pipelineConfig.getName(), ExecutionState.SUCCESS));
    logInfo("Pipeline execution completed successfully.");
    return 0;
  }

  /**
   * Executes a job locally using Docker (future implementation).
   *
   * @param jobState The job state to execute.
   * @return true if successful, false otherwise.
   */
  private boolean executeJob(JobRunState jobState) {
    logInfo("Running Job: " + jobState.getJobName());

    // Ensure the job has a valid pipeline association
    String pipelineName = jobState.getJobDefinition().getStageName();

    // Create a job execution request
    JobRequest jobRequest = new JobRequest(
            jobState.getJobName(),
            pipelineName, // Use stage as a reference (Pipeline info not in JobConfig)
            jobState.getJobName(),
            "latest-commit",
            Map.of(),
            List.of(),
            true
    );

    try {
      backendClient.executeJob(jobRequest);
      logInfo("Job execution request sent: " + jobState.getJobName());
      return true;
    } catch (Exception e) {
      logError("Job execution failed: " + jobState.getJobName() + " - Error: " + e.getMessage());
      return false;
    }
  }


  /**
   * Executes the pipeline remotely via the backend.
   *
   * @param pipelineConfig The pipeline configuration.
   * @return Exit code (0 for success, 1 for failure).
   */
  private int executeRemotely(PipelineConfig pipelineConfig) {
    logInfo("Running pipeline remotely via backend...");

    RunPipelineRequest request = new RunPipelineRequest(pipelineConfig.getName());
    try {
      String response = backendClient.runPipeline(request);
      logInfo("Pipeline Execution Started via Backend:");
      System.out.println(response);
      return 0;
    } catch (Exception e) {
      logError("Failed to start pipeline remotely: " + e.getMessage());
      return 1;
    }
  }
}
