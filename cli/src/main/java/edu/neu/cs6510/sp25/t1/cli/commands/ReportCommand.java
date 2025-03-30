package edu.neu.cs6510.sp25.t1.cli.commands;

import java.io.IOException;
import java.util.concurrent.Callable;

import edu.neu.cs6510.sp25.t1.cli.api.CliBackendClient;
import edu.neu.cs6510.sp25.t1.common.logging.PipelineLogger;
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

  @CommandLine.Option(names = {"--format"}, description = "Output format (text, json).", defaultValue = "text")
  private String format;

  private final CliBackendClient backendClient = new CliBackendClient("http://localhost:8080");

  /**
   * Main entry point for the `report` command.
   *
   * @return 0 if successful, 1 if API request failed
   */
  @Override
  public Integer call() {
    try {
      // If job is specified but stage is not, that's an error
      if (jobName != null && stageName == null) {
        System.err.println("Error: --stage parameter is required when using --job");
        return 1;
      }

      // Now handle various parameter combinations
      if (runNumber != null) {
        // We have a run number specified
        if (stageName != null) {
          if (jobName != null) {
            return fetchJobSummary();
          } else {
            return fetchStageSummary();
          }
        } else {
          return fetchPipelineRunSummary();
        }
      } else {
        // No run number, but we might have stage or job
        if (stageName != null) {
          return fetchStageHistory();  // Need to implement this
        } else {
          return fetchPipelineHistory();
        }
      }
    } catch (IOException e) {
      PipelineLogger.error("API request failed: " + e.getMessage());
      return 1;
    } catch (Exception e) {
      PipelineLogger.error("Error: " + e.getMessage());
      return 1;
    }
  }

  /**
   * Fetch past runs of a specific pipeline.
   *
   * @return 0 if successful, 1 if API request failed
   */
  private Integer fetchPipelineHistory() throws IOException {
    PipelineLogger.info("Fetching past runs for pipeline: " + pipelineName);
    String response = backendClient.fetchPipelineReport(pipelineName, -1, null, null);

    // Simply print the response
    System.out.println(response);
    return 0;
  }

  /**
   * Fetch summary of a specific pipeline run.
   *
   * @return 0 if successful, 1 if API request failed
   * @throws IOException if API request fails
   */
  private Integer fetchPipelineRunSummary() throws IOException {
    PipelineLogger.info("Fetching run summary for pipeline: " + pipelineName + ", Run: " + runNumber);
    String response = backendClient.fetchPipelineReport(pipelineName, runNumber, null, null);

    // Simply print the response
    System.out.println(response);
    return 0;
  }

  /**
   * Fetch summary of a specific stage in a pipeline run.
   *
   * @return 0 if successful, 1 if API request failed
   * @throws IOException if API request fails
   */
  private Integer fetchStageSummary() throws IOException {
    PipelineLogger.info("Fetching stage summary for pipeline: " + pipelineName + ", Run: " + runNumber + ", Stage: " + stageName);
    String response = backendClient.fetchPipelineReport(pipelineName, runNumber, stageName, null);

    // Simply print the response
    System.out.println(response);
    return 0;
  }

  /**
   * Fetch summary of a specific job in a pipeline stage.
   *
   * @return 0 if successful, 1 if API request failed
   * @throws IOException if API request fails
   */
  private Integer fetchJobSummary() throws IOException {
    PipelineLogger.info("Fetching job summary for pipeline: " + pipelineName + ", Run: " + runNumber + ", Stage: " + stageName + ", Job: " + jobName);
    String response = backendClient.fetchPipelineReport(pipelineName, runNumber, stageName, jobName);

    // Simply print the response
    System.out.println(response);
    return 0;
  }

  /**
   * Fetch history of a specific stage across all pipeline runs.
   * This method also handles the case when a job name is specified.
   *
   * @return 0 if successful, 1 if API request failed
   * @throws IOException if API request fails
   */
  private Integer fetchStageHistory() throws IOException {
    if (jobName != null) {
      PipelineLogger.info("Fetching history for job: " + jobName + " in stage: " + stageName + " in pipeline: " + pipelineName);
    } else {
      PipelineLogger.info("Fetching history for stage: " + stageName + " in pipeline: " + pipelineName);
    }

    // Pass both stageName and jobName to backend client
    String response = backendClient.fetchPipelineReport(pipelineName, null, stageName, jobName);

    // Print the response
    System.out.println(response);
    return 0;
  }
}