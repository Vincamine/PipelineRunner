package edu.neu.cs6510.sp25.t1.cli.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import edu.neu.cs6510.sp25.t1.common.api.PipelineCheckResponse;
import edu.neu.cs6510.sp25.t1.common.api.RunPipelineRequest;
import edu.neu.cs6510.sp25.t1.common.api.UpdateExecutionStateRequest;
import edu.neu.cs6510.sp25.t1.common.runtime.JobRunState;
import edu.neu.cs6510.sp25.t1.common.runtime.PipelineRunState;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Client for interacting with the backend API.
 */
public class CliBackendClient {
  private static final Logger logger = LoggerFactory.getLogger(CliBackendClient.class);
  private final String baseUrl;
  private final OkHttpClient client;
  private final ObjectMapper objectMapper;
  private final YAMLMapper yamlMapper = new YAMLMapper();

  /**
   * Constructor with parameters.
   *
   * @param baseUrl The backend server base URL.
   */
  public CliBackendClient(String baseUrl) {
    this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    this.client = new OkHttpClient.Builder()
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .build();
    this.objectMapper = new ObjectMapper();
  }

  public CliBackendClient() {

  }

  /**
   * Handles API responses and ensures proper error reporting.
   *
   * @param response The HTTP response object.
   * @return The response body as a string.
   */
  private String handleResponse(Response response) throws IOException {
    String responseBody = response.body() != null ? response.body().string() : "";

    if (!response.isSuccessful()) {
      logger.error("API Error: {} - {}", response.code(), response.message());
      throw new IOException("API Error: " + response.code() + " - " + response.message() + "\n" + responseBody);
    }
    return responseBody;
  }

  /**
   * Check the pipeline configuration.
   *
   * @param configFile Path to the configuration file.
   * @return PipelineCheckResponse
   * @throws IOException If there is an error communicating with the backend.
   */
  public PipelineCheckResponse checkPipelineConfig(String configFile) throws IOException {
    Request req = new Request.Builder()
            .url(baseUrl + "/api/v1/pipelines/check?file=" + configFile)
            .get()
            .build();

    try (Response response = client.newCall(req).execute()) {
      String responseBody = handleResponse(response);
      return objectMapper.readValue(responseBody, PipelineCheckResponse.class);
    }
  }

  /**
   * Performs a dry run of the pipeline to simulate execution.
   *
   * @param configFile Path to the pipeline configuration file.
   * @return The simulated execution order.
   * @throws IOException If there is an error communicating with the backend.
   */
  public String dryRunPipeline(String configFile) throws IOException {
    Request req = new Request.Builder()
            .url(baseUrl + "/api/v1/pipelines/dry-run?file=" + configFile)
            .get()
            .build();

    try (Response response = client.newCall(req).execute()) {
      return handleResponse(response);
    }
  }

  /**
   * Runs a pipeline execution via the backend.
   *
   * @param request The pipeline execution request details.
   * @return The backend response as a String.
   * @throws IOException If there is an error communicating with the backend.
   */
  public String runPipeline(RunPipelineRequest request) throws IOException {
    String jsonRequest = objectMapper.writeValueAsString(request);

    Request req = new Request.Builder()
            .url(baseUrl + "/api/v1/pipelines/run")
            .post(RequestBody.create(jsonRequest, MediaType.parse("application/json")))
            .build();

    try (Response response = client.newCall(req).execute()) {
      return handleResponse(response);
    }
  }

  /**
   * Fetch past executions of a pipeline.
   *
   * @param pipeline The pipeline name.
   * @param format   The output format: json, yaml, or plaintext.
   * @return The execution history in the requested format.
   * @throws IOException If there is an error communicating with the backend.
   */
  public String getPipelineExecutions(String pipeline, String format) throws IOException {
    String url = baseUrl + "/api/v1/pipelines";

    if (!"all".equals(pipeline)) {
      url += "/" + pipeline + "/executions";
    }

    Request req = new Request.Builder().url(url).get().build();

    try (Response response = client.newCall(req).execute()) {
      String responseBody = handleResponse(response);
      return formatResponse(responseBody, format);
    }
  }

  /**
   * Formats the API response based on the requested output format.
   *
   * @param responseBody The raw API response.
   * @param format       The requested output format.
   * @return Formatted response string.
   * @throws IOException If formatting fails.
   */
  private String formatResponse(String responseBody, String format) throws IOException {
    if ("yaml".equalsIgnoreCase(format)) {
      return yamlMapper.writeValueAsString(objectMapper.readTree(responseBody));
    } else if ("json".equalsIgnoreCase(format)) {
      return objectMapper.writerWithDefaultPrettyPrinter()
              .writeValueAsString(objectMapper.readTree(responseBody));
    }
    return responseBody;
  }

  /**
   * Fetches a list of all pipelines.
   *
   * @return A JSON string containing all pipelines.
   * @throws IOException If there is an error communicating with the backend.
   */
  public String getAllPipelines() throws IOException {
    Request req = new Request.Builder().url(baseUrl + "/api/v1/pipelines").get().build();

    try (Response response = client.newCall(req).execute()) {
      return handleResponse(response);
    }
  }

  /**
   * Updates the execution state of a pipeline, stage, or job.
   *
   * @param request The execution state update request.
   */
  public void updateExecutionState(UpdateExecutionStateRequest request) {
    try {
      String jsonRequest = objectMapper.writeValueAsString(request);

      Request req = new Request.Builder()
              .url(baseUrl + "/api/v1/execution/state")
              .post(RequestBody.create(jsonRequest, MediaType.parse("application/json")))
              .build();

      try (Response response = client.newCall(req).execute()) {
        handleResponse(response);
        logger.info("Updated execution state: {}", request);
      }
    } catch (IOException e) {
      logger.error("Failed to update execution state: {}", e.getMessage());
    }
  }


  /**
   * Logs the start of a pipeline execution.
   *
   * @param pipelineRunState The pipeline execution state.
   */
  public void logPipelineExecutionStart(PipelineRunState pipelineRunState) {
    logExecution("/api/v1/pipelines/log/start", pipelineRunState);
  }

  /**
   * Logs the success of a pipeline execution.
   *
   * @param pipelineRunState The pipeline execution state.
   */
  public void logPipelineExecutionSuccess(PipelineRunState pipelineRunState) {
    logExecution("/api/v1/pipelines/log/success", pipelineRunState);
  }

  /**
   * Logs the failure of a pipeline execution.
   *
   * @param pipelineRunState The pipeline execution state.
   */
  public void logPipelineExecutionFailure(PipelineRunState pipelineRunState) {
    logExecution("/api/v1/pipelines/log/failure", pipelineRunState);
  }

  /**
   * Logs the start of a job execution.
   *
   * @param jobRunState The job execution state.
   */
  public void logJobExecutionStart(JobRunState jobRunState) {
    logExecution("/api/v1/jobs/log/start", jobRunState);
  }

  /**
   * Logs the success of a job execution.
   *
   * @param jobRunState The job execution state.
   */
  public void logJobExecutionSuccess(JobRunState jobRunState) {
    logExecution("/api/v1/jobs/log/success", jobRunState);
  }

  /**
   * Logs the failure of a job execution.
   *
   * @param jobRunState The job execution state.
   */
  public void logJobExecutionFailure(JobRunState jobRunState) {
    logExecution("/api/v1/jobs/log/failure", jobRunState);
  }

  /**
   * Generic method to log execution states to the backend.
   *
   * @param endpoint The API endpoint for logging execution.
   * @param state    The execution state object (pipeline or job).
   */
  private void logExecution(String endpoint, Object state) {
    try {
      String jsonRequest = objectMapper.writeValueAsString(state);

      Request req = new Request.Builder()
              .url(baseUrl + endpoint)
              .post(RequestBody.create(jsonRequest, MediaType.parse("application/json")))
              .build();

      try (Response response = client.newCall(req).execute()) {
        handleResponse(response);
        logger.info("Logged execution: {} -> {}", endpoint, state);
      }
    } catch (IOException e) {
      logger.error("Failed to log execution to {}: {}", endpoint, e.getMessage());
    }
  }
}

