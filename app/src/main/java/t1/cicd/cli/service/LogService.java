package t1.cicd.cli.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import t1.cicd.cli.model.LogEntry;
import t1.cicd.cli.model.LogLevel;

import java.util.List;
import java.util.stream.Collectors;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;

/**
 * Service to manage log retrieval for pipelines.
 */
public class LogService {

  private static final String LOG_API_URL = "https://example.com/api/log";
  private final HttpClient httpClient;
  private final ObjectMapper objectMapper;

  public LogService() {
    this.httpClient = HttpClient.newHttpClient();
    this.objectMapper = new ObjectMapper();
  }

  /**
   * Retrieves logs for a given pipeline ID.
   *
   * @param pipelineId The ID of the pipeline.
   * @return A list of log entries associated with the pipeline.
   */
  public List<LogEntry> getLogsByPipelineId(String pipelineId) {
    List<LogEntry> allLogs = List.of(
        new LogEntry("123", LogLevel.INFO, "Pipeline execution started", System.currentTimeMillis()),
        new LogEntry("123", LogLevel.WARN, "Potential issue detected", System.currentTimeMillis() + 1000),
        new LogEntry("124", LogLevel.ERROR, "Pipeline 124 failed", System.currentTimeMillis() + 2000)
    );

    return allLogs.stream()
        .filter(log -> log.getPipelineId().equals(pipelineId))
        .collect(Collectors.toList());
  }

  /**
   * Retrieves logs for a given pipeline ID from an online log server.
   *
   * @param pipelineId The ID of the pipeline.
   * @return A list of log entries associated with the pipeline.
   */
  public List<LogEntry> getLogsOnlineByPipelineId(String pipelineId) {
    try {
      // Construct the API URL
      URI uri = new URI(LOG_API_URL + pipelineId);

      // Create HTTP request
      HttpRequest request = HttpRequest.newBuilder()
          .uri(uri)
          .header("Accept", "application/json")
          .GET()
          .build();

      // Send the request
      HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

      // Handle response
      if (response.statusCode() == HttpURLConnection.HTTP_OK) {
        return objectMapper.readValue(response.body(), new TypeReference<List<LogEntry>>() {});
      } else {
        System.err.println("Error fetching logs. HTTP Status: " + response.statusCode());
        return Collections.emptyList();
      }

    } catch (Exception e) {
      System.err.println("Failed to fetch logs: " + e.getMessage());
      return Collections.emptyList();
    }
  }
}
