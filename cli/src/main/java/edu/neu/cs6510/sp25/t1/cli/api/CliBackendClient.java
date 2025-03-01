package edu.neu.cs6510.sp25.t1.cli.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

import java.io.IOException;

import edu.neu.cs6510.sp25.t1.common.api.PipelineCheckResponse;
import edu.neu.cs6510.sp25.t1.common.api.RunPipelineRequest;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Client for interacting with the backend API.
 */
public class CliBackendClient {
  private final String baseUrl;
  private final OkHttpClient client;
  private final ObjectMapper objectMapper;
  private final YAMLMapper yamlMapper;

  /**
   * Constructor with parameters.
   *
   * @param baseUrl The backend server base URL.
   */
  public CliBackendClient(String baseUrl) {
    this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    this.client = new OkHttpClient();
    this.objectMapper = new ObjectMapper();
    this.yamlMapper = new YAMLMapper();
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
    // Convert RunPipelineRequest to JSON
    String jsonRequest = objectMapper.writeValueAsString(request);

    // Create the HTTP POST request
    Request req = new Request.Builder()
            .url(baseUrl + "/api/v1/pipelines/run")
            .post(RequestBody.create(jsonRequest, MediaType.parse("application/json")))
            .build();

    // Execute the request
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
}
