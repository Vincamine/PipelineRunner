package edu.neu.cs6510.sp25.t1.cli.api;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import edu.neu.cs6510.sp25.t1.common.logging.PipelineLogger;

/**
 * Client for communicating with the backend API.
 */
public class CliBackendClient {
  private final String baseUrl;
  private final HttpClient httpClient;

  // API endpoint patterns - adjust these to match your backend
  private static final String PIPELINE_HISTORY_ENDPOINT = "/api/report/pipeline/%s";
  private static final String PIPELINE_RUN_ENDPOINT = "/api/report/pipeline/%s/run/%d";
  private static final String STAGE_REPORT_ENDPOINT = "/api/report/pipeline/%s/run/%d/stage/%s";
  private static final String JOB_REPORT_ENDPOINT = "/api/report/pipeline/%s/run/%d/stage/%s/job/%s";

  /**
   * Constructor for CliBackendClient.
   *
   * @param baseUrl the base URL of the backend API
   */
  public CliBackendClient(String baseUrl) {
    this.baseUrl = baseUrl;
    this.httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .build();
  }

  /**
   * Fetch pipeline reports from the backend.
   *
   * @param pipelineName the pipeline name
   * @param runNumber the run number, or -1 for all runs
   * @param stageName the stage name (optional)
   * @param jobName the job name (optional)
   * @return formatted response from the backend
   * @throws IOException if the API request fails
   */
  public String fetchPipelineReport(String pipelineName, int runNumber,
                                    String stageName, String jobName) throws IOException {
    String url;
    String encodedPipeline = URLEncoder.encode(pipelineName, StandardCharsets.UTF_8);

    if (runNumber < 0) {
      // Pipeline history report
      url = String.format(PIPELINE_HISTORY_ENDPOINT, encodedPipeline);
    } else if (stageName == null) {
      // Pipeline run report
      url = String.format(PIPELINE_RUN_ENDPOINT, encodedPipeline, runNumber);
    } else if (jobName == null) {
      // Stage report
      String encodedStage = URLEncoder.encode(stageName, StandardCharsets.UTF_8);
      url = String.format(STAGE_REPORT_ENDPOINT, encodedPipeline, runNumber, encodedStage);
    } else {
      // Job report
      String encodedStage = URLEncoder.encode(stageName, StandardCharsets.UTF_8);
      String encodedJob = URLEncoder.encode(jobName, StandardCharsets.UTF_8);
      url = String.format(JOB_REPORT_ENDPOINT, encodedPipeline, runNumber, encodedStage, encodedJob);
    }

    return sendGetRequest(baseUrl + url);
  }

  /**
   * Send a GET request to the backend API.
   *
   * @param url the URL to send the request to
   * @return the response body as a string
   * @throws IOException if the API request fails
   */
  private String sendGetRequest(String url) throws IOException {
    try {
      PipelineLogger.info("Sending GET request to: " + url);

      HttpRequest request = HttpRequest.newBuilder()
              .uri(URI.create(url))
              .GET()
              .header("Accept", "application/json")
              .build();

      HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

      if (response.statusCode() >= 400) {
        PipelineLogger.error("API request failed with status code: " + response.statusCode());
        PipelineLogger.error("Response body: " + response.body());
        throw new IOException("API request failed with status code: " + response.statusCode());
      }

      return response.body();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IOException("API request was interrupted", e);
    } catch (Exception e) {
      throw new IOException("API request failed: " + e.getMessage(), e);
    }
  }
}