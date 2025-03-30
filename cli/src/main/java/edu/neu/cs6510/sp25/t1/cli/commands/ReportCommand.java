package edu.neu.cs6510.sp25.t1.cli.commands;

import java.io.IOException;
import java.util.concurrent.Callable;

import edu.neu.cs6510.sp25.t1.cli.api.CliBackendClient;
import edu.neu.cs6510.sp25.t1.common.logging.PipelineLogger;
import edu.neu.cs6510.sp25.t1.common.validation.utils.GitUtils;
import picocli.CommandLine;

/**
 * Implements the `report` command to fetch past pipeline execution reports using the backend API.
 */
@CommandLine.Command(
        name = "report",
        description = "Retrieves pipeline execution reports from the CI/CD backend."
)
public class ReportCommand implements Callable<Integer> {

  @CommandLine.Option(names = {"--pipeline", "-p"}, description = "Specify the pipeline name.", required = true)
  private String pipelineName;

  @CommandLine.Option(names = {"--run"}, description = "Specify the run number for a detailed report.")
  private Integer runNumber;

  @CommandLine.Option(names = {"--stage"}, description = "Specify the stage name for a detailed report.")
  private String stageName;

  @CommandLine.Option(names = {"--job"}, description = "Specify the job name for a detailed report.")
  private String jobName;

  private final CliBackendClient backendClient = new CliBackendClient("http://localhost:8080");

  /**
   * Main entry point for the `report` command.
   *
   * @return 0 if successful, 1 if API request failed
   */
  @Override
  public Integer call() {
    // Verify we're in a Git repository root directory
    GitUtils.isGitRootDirectory();

    try {
      // If no pipeline name is provided, return a list of all pipeline names
      if (pipelineName == null) {
        return fetchAvailablePipelines();
      }

      // If run number is not specified, show all runs for this pipeline
      if (runNumber == null) {
        return fetchPipelineHistory();
      }
      // If stage name is not specified, show summary for this run
      else if (stageName == null) {
        return fetchPipelineRunSummary();
      }
      // If job name is not specified, show stage summary
      else if (jobName == null) {
        return fetchStageSummary();
      }
      // Otherwise, show job summary
      else {
        return fetchJobSummary();
      }
    } catch (IOException e) {
      PipelineLogger.error("API request failed: " + e.getMessage());
      return 1;
    }
  }

  /**
   * Fetch available pipelines.
   * Report content: List of all pipeline names
   *
   * @return 0 if successful, 1 if API request failed
   */
  private Integer fetchAvailablePipelines() throws IOException {
    PipelineLogger.info("Fetching available pipelines");
    String response = backendClient.fetchAvailablePipelines();
    PipelineLogger.info(response);
    return 0;
  }


  /**
   * Fetch past runs of a specific pipeline.
   * Report content:
   * - Pipeline name
   * - Run number (for each run)
   * - Git commit hash
   * - Status (success, failed, canceled)
   * - Start time
   * - Completion time
   *
   * @return 0 if successful, 1 if API request failed
   */
  private Integer fetchPipelineHistory() throws IOException {
    PipelineLogger.info("Fetching past runs for pipeline: " + pipelineName);
    String response = backendClient.fetchPipelineReport(pipelineName, -1, null, null);
    PipelineLogger.info(response);
    return 0;
  }



  /**
   * Fetch summary of a specific pipeline run.
   * Report content:
   * - Pipeline name
   * - Run number
   * - Git commit hash
   * - Pipeline status
   * - For each stage:
   *   - Stage name
   *   - Stage status
   *   - Start time
   *   - Completion time
   *
   * @return 0 if successful, 1 if API request failed
   */
  private Integer fetchPipelineRunSummary() throws IOException {
    PipelineLogger.info("Fetching run summary for pipeline: " + pipelineName + ", Run: " + runNumber);
    String response = backendClient.fetchPipelineReport(pipelineName, runNumber, null, null);
    PipelineLogger.info(response);
    return 0;
  }

  /**
   * Fetch summary of a specific stage in a pipeline run.
   * Report content:
   * - Pipeline name
   * - Run number
   * - Git commit hash
   * - Stage name
   * - Stage status
   * - For each job in the stage:
   *   - Job name
   *   - Job status
   *   - Allows failure flag
   *   - Start time
   *   - Completion time
   *
   * @return 0 if successful, 1 if API request failed
   */
  private Integer fetchStageSummary() throws IOException {
    PipelineLogger.info("Fetching stage summary for pipeline: " + pipelineName + ", Run: " + runNumber + ", Stage: " + stageName);
    String response = backendClient.fetchPipelineReport(pipelineName, runNumber, stageName, null);
    PipelineLogger.info(response);
    return 0;
  }

  /**
   * Fetch summary of a specific job in a pipeline stage.
   * Report content:
   * - Pipeline name
   * - Run number
   * - Git commit hash
   * - Stage name
   * - Job name
   * - Job status
   * - Allows failure flag
   * - Start time
   * - Completion time
   *
   * @return 0 if successful, 1 if API request failed
   */
  private Integer fetchJobSummary() throws IOException {
    PipelineLogger.info("Fetching job summary for pipeline: " + pipelineName + ", Run: " + runNumber + ", Stage: " + stageName + ", Job: " + jobName);
    String response = backendClient.fetchPipelineReport(pipelineName, runNumber, stageName, jobName);
    PipelineLogger.info(response);
    return 0;
  }
}
