package edu.neu.cs6510.sp25.t1.cli.commands;

import java.io.IOException;
import java.util.concurrent.Callable;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

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
  private final ObjectMapper objectMapper = new ObjectMapper()
          .configure(SerializationFeature.INDENT_OUTPUT, true);

  /**
   * Main entry point for the `report` command.
   *
   * @return 0 if successful, 1 if API request failed
   */
  @Override
  public Integer call() {
    try {
      // Based on options, determine report type
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

    // Print the response
    displayResponse(response);
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

    // Print the response
    displayResponse(response);
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

    // Print the response
    displayResponse(response);
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

    // Print the response
    displayResponse(response);
    return 0;
  }

  /**
   * Display the response based on the requested format.
   *
   * @param response the response from the backend
   * @throws IOException if formatting fails
   */
  private void displayResponse(String response) throws IOException {
    if ("json".equals(format.toLowerCase())) {
      // For JSON format, try to pretty-print the JSON
      try {
        Object json = objectMapper.readValue(response, Object.class);
        System.out.println(objectMapper.writeValueAsString(json));
      } catch (Exception e) {
        // If parsing fails, just print the raw response
        System.out.println(response);
      }
    } else {
      // For text format, print the response as is
      System.out.println(response);
    }
  }
}