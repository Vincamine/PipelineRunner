package edu.neu.cs6510.sp25.t1.cli.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * CliBackendClient handles API interactions with the CI/CD backend.
 * Supports:
 * - Triggering pipeline executions
 * - Fetching execution reports
 * - Validating pipeline configurations
 * - Handling API requests with structured error handling
 */
public class CliBackendClient {
  private static final Logger LOGGER = LoggerFactory.getLogger(CliBackendClient.class);
  private static final OkHttpClient HTTP_CLIENT = new OkHttpClient();
  private final String backendUrl;

  /**
   * Constructs a CliBackendClient with the backend API URL.
   *
   * @param backendUrl The base URL of the CI/CD backend.
   */
  public CliBackendClient(String backendUrl) {
    this.backendUrl = backendUrl;
  }

  /**
   * Triggers a pipeline execution on the backend.
   *
   * @param repo     The repository URL or local path.
   * @param branch   The Git branch to use.
   * @param commit   The commit hash.
   * @param pipeline The name of the pipeline to execute.
   * @return The execution ID if successful.
   * @throws IOException If an API error occurs.
   */
  public String triggerPipelineExecution(String repo, String branch, String commit, String pipeline) throws IOException {
    String url = String.format("%s/api/pipeline/run", backendUrl);
    String jsonPayload = String.format(
            "{\"repo\": \"%s\", \"branch\": \"%s\", \"commit\": \"%s\", \"pipeline\": \"%s\"}",
            repo, branch, commit, pipeline
    );

    Request request = createPostRequest(url, jsonPayload);
    Response response = executeRequest(request);

    return response.body() != null ? response.body().string() : "Error: Empty response";
  }

  /**
   * Fetches execution reports for a **pipeline, stage, or job**.
   *
   * @param pipelineName The pipeline name.
   * @param runNumber    The specific run number.
   * @param stageName    Optional: The stage name.
   * @param jobName      Optional: The job name.
   * @return JSON response containing execution details.
   * @throws IOException If an API error occurs.
   */
  public String fetchPipelineReport(String pipelineName, int runNumber, String stageName, String jobName) throws IOException {
    String url;

    // 🌟 Check if user wants a **pipeline, stage, or job** report
    if (jobName != null && stageName != null) {
      url = String.format("%s/api/report/pipeline/%s/run/%d/stage/%s/job/%s", backendUrl, pipelineName, runNumber, stageName, jobName);
    } else if (stageName != null) {
      url = String.format("%s/api/report/pipeline/%s/run/%d/stage/%s", backendUrl, pipelineName, runNumber, stageName);
    } else {
      url = String.format("%s/api/report/pipeline/%s/run/%d", backendUrl, pipelineName, runNumber);
    }

    Request request = createGetRequest(url);
    Response response = executeRequest(request);

    return response.body() != null ? response.body().string() : "Error: Empty response";
  }

  /**
   * Validates a pipeline configuration file on the backend.
   *
   * @param repo The repository URL.
   * @param file The configuration file path.
   * @return JSON response indicating validation results.
   * @throws IOException If an API error occurs.
   */
  public String validatePipelineConfig(String repo, String file) throws IOException {
    String url = String.format("%s/api/pipeline/validate", backendUrl);
    String jsonPayload = String.format("{\"repo\": \"%s\", \"file\": \"%s\"}", repo, file);

    Request request = createPostRequest(url, jsonPayload);
    Response response = executeRequest(request);

    return response.body() != null ? response.body().string() : "Error: Empty response";
  }

  /**
   * Creates an HTTP GET request.
   *
   * @param url The API endpoint URL.
   * @return The OkHttp Request object.
   */
  private Request createGetRequest(String url) {
    return new Request.Builder()
            .url(url)
            .get()
            .addHeader("Accept", "application/json")
            .build();
  }

  /**
   * Creates an HTTP POST request.
   *
   * @param url     The API endpoint URL.
   * @param payload The JSON payload.
   * @return The OkHttp Request object.
   */
  private Request createPostRequest(String url, String payload) {
    RequestBody body = RequestBody.create(payload, MediaType.get("application/json; charset=utf-8"));
    return new Request.Builder()
            .url(url)
            .post(body)
            .addHeader("Content-Type", "application/json")
            .addHeader("Accept", "application/json")
            .build();
  }

  /**
   * Executes an HTTP request and handles errors.
   *
   * @param request The OkHttp Request object.
   * @return The Response object.
   * @throws IOException If an API error occurs.
   */
  private Response executeRequest(Request request) throws IOException {
    Response response = HTTP_CLIENT.newCall(request).execute();

    if (!response.isSuccessful()) {
      String errorMessage = response.body() != null ? response.body().string() : "Unknown error";
      LOGGER.error("API Error: {} - {}", response.code(), errorMessage);
      throw new IOException("API Error: " + response.code() + " - " + errorMessage);
    }

    return response;
  }
}
