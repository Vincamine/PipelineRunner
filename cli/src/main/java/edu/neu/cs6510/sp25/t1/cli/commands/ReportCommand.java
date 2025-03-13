package edu.neu.cs6510.sp25.t1.cli.commands;

import java.io.IOException;
import java.util.concurrent.Callable;

import edu.neu.cs6510.sp25.t1.cli.api.CliBackendClient;
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
    try {
      if (runNumber == null) {
        return fetchPipelineHistory();
      } else if (stageName == null) {
        return fetchPipelineRunSummary();
      } else if (jobName == null) {
        return fetchStageSummary();
      } else {
        return fetchJobSummary();
      }
    } catch (IOException e) {
      System.err.println("[Error] API request failed: " + e.getMessage());
      return 1;
    }
  }

  /**
   * Fetch past runs of a specific pipeline.
   *
   * @return 0 if successful, 1 if API request failed
   */
  private Integer fetchPipelineHistory() throws IOException {
    System.out.println("Fetching past runs for pipeline: " + pipelineName);
    String response = backendClient.fetchPipelineReport(pipelineName, -1, null, null); // -1 means "fetch all runs"
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
    System.out.println("Fetching run summary for pipeline: " + pipelineName + ", Run: " + runNumber);
    String response = backendClient.fetchPipelineReport(pipelineName, runNumber, null, null);
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
    System.out.println("Fetching stage summary for pipeline: " + pipelineName + ", Run: " + runNumber + ", Stage: " + stageName);
    String response = backendClient.fetchPipelineReport(pipelineName, runNumber, stageName, null);
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
    System.out.println("Fetching job summary for pipeline: " + pipelineName + ", Run: " + runNumber + ", Stage: " + stageName + ", Job: " + jobName);
    String response = backendClient.fetchPipelineReport(pipelineName, runNumber, stageName, jobName);
    System.out.println(response);
    return 0;
  }
}
