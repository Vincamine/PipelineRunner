package edu.neu.cs6510.sp25.t1.cli.commands;

import java.io.IOException;
import java.util.concurrent.Callable;

import edu.neu.cs6510.sp25.t1.cli.CliApp;
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

  @CommandLine.ParentCommand
  private CliApp parent; // Inherit global CLI options

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
    String pipelineName = parent.pipeline;
    if (pipelineName == null) {
      System.err.println("[Error] Please specify a pipeline using --pipeline.");
      return 1;
    }

    try {
      if (runNumber == null) {
        return fetchPipelineHistory(pipelineName);
      } else if (stageName == null) {
        return fetchPipelineRunSummary(pipelineName, runNumber);
      } else if (jobName == null) {
        return fetchStageSummary(pipelineName, runNumber, stageName);
      } else {
        return fetchJobSummary(pipelineName, runNumber, stageName, jobName);
      }
    } catch (IOException e) {
      System.err.println("[Error] API request failed: " + e.getMessage());
      return 1;
    }
  }

  /**
   * Fetch past runs of a specific pipeline.
   *
   * @param pipelineName Name of the pipeline
   * @return 0 if successful, 1 if API request failed
   */
  private Integer fetchPipelineHistory(String pipelineName) throws IOException {
    System.out.println("Fetching past runs for pipeline: " + pipelineName);
    String response = backendClient.fetchPipelineReport(pipelineName, -1, null, null); // -1 means "fetch all runs"
    System.out.println(response);
    return 0;
  }

  /**
   * Fetch summary of a specific pipeline run.
   * @param pipelineName Name of the pipeline
   * @param run Run number
   * @return 0 if successful, 1 if API request failed
   * @throws IOException if API request fails
   */
  private Integer fetchPipelineRunSummary(String pipelineName, int run) throws IOException {
    System.out.println("Fetching run summary for pipeline: " + pipelineName + ", Run: " + run);
    String response = backendClient.fetchPipelineReport(pipelineName, run, null, null);
    System.out.println(response);
    return 0;
  }

  /**
   * Fetch summary of a specific stage in a pipeline run.
   * @param pipelineName Name of the pipeline
   * @param run Run number
   * @param stage Stage name
   * @return 0 if successful, 1 if API request failed
   * @throws IOException if API request fails
   */
  private Integer fetchStageSummary(String pipelineName, int run, String stage) throws IOException {
    System.out.println("Fetching stage summary for pipeline: " + pipelineName + ", Run: " + run + ", Stage: " + stage);
    String response = backendClient.fetchPipelineReport(pipelineName, run, stage, null);
    System.out.println(response);
    return 0;
  }

  /**
   * Fetch summary of a specific job in a pipeline stage.
   * @param pipelineName Name of the pipeline
   * @param run Run number
   * @param stage Stage name
   * @param job Job name
   * @return 0 if successful, 1 if API request failed
   * @throws IOException if API request fails
   */
  private Integer fetchJobSummary(String pipelineName, int run, String stage, String job) throws IOException {
    System.out.println("Fetching job summary for pipeline: " + pipelineName + ", Run: " + run + ", Stage: " + stage + ", Job: " + job);
    String response = backendClient.fetchPipelineReport(pipelineName, run, stage, job);
    System.out.println(response);
    return 0;
  }
}
