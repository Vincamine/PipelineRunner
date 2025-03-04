package edu.neu.cs6510.sp25.t1.cli.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;



/**
 * Client for interacting with the backend API from the CLI.
 */
public class CliBackendClient {
  private static final Logger logger = LoggerFactory.getLogger(CliBackendClient.class);
  private String baseUrl = "http://localhost:8080";
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
    this.client = new OkHttpClient.Builder()
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .build();
    this.objectMapper = new ObjectMapper();
    this.yamlMapper = new YAMLMapper();
  }

  public CliBackendClient() {
    this("http://localhost:8080");
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
   * Run a pipeline execution via the backend.
   *
   * @param request The pipeline execution request details.
   * @return The backend response as a String.
   * @throws IOException If there is an error communicating with the backend.
   */
  public String runPipeline(RunPipelineRequest request) throws IOException {
    String jsonRequest = objectMapper.writeValueAsString(request);

    Request req = new Request.Builder()
            .url(baseUrl + "/api/v1/pipelines/" + request.getPipeline() + "/execute")
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
    String url = baseUrl + "/api/v1/pipelines/" + pipeline + "/executions";

    Request req = new Request.Builder().url(url).get().build();

    try (Response response = client.newCall(req).execute()) {
      String responseBody = handleResponse(response);
      return formatResponse(responseBody, format);
    }
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
   * Fetch a specific pipeline execution.
   *
   * @param pipeline The pipeline name.
   * @param runId    The run ID.
   * @param format   The output format.
   * @return Formatted pipeline execution details.
   * @throws IOException If there is an error communicating with the backend.
   */
  public String getPipelineExecution(String pipeline, String runId, String format) throws IOException {
    String url = baseUrl + "/api/v1/pipelines/" + pipeline + "/executions/" + runId;

    Request req = new Request.Builder().url(url).get().build();

    try (Response response = client.newCall(req).execute()) {
      String responseBody = handleResponse(response);
      return formatResponse(responseBody, format);
    }
  }

  /**
   * Fetch the latest pipeline execution.
   *
   * @param pipeline The pipeline name.
   * @param format   The output format.
   * @return Formatted latest execution details.
   * @throws IOException If there is an error communicating with the backend.
   */
  public String getLatestPipelineExecution(String pipeline, String format) throws IOException {
    String url = baseUrl + "/api/v1/pipelines/" + pipeline + "/executions/latest";

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
}
