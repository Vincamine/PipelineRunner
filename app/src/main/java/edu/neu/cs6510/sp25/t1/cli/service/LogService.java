package edu.neu.cs6510.sp25.t1.cli.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.neu.cs6510.sp25.t1.cli.model.LogEntry;

import java.util.List;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;

public class LogService {
  private final HttpClient httpClient;
  private final ObjectMapper objectMapper;

  public LogService(HttpClient httpClient) {
    this.httpClient = httpClient != null ? httpClient : HttpClient.newHttpClient();
    this.objectMapper = new ObjectMapper();
  }

  public List<LogEntry> getLogsByPipelineId(String pipelineId) {
    try {
      final URI uri = new URI("https://example.com/api/log/" + pipelineId);

      final HttpRequest request = HttpRequest.newBuilder()
          .uri(uri)
          .header("Accept", "application/json")
          .GET()
          .build();

      final HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

      if (response.statusCode() == 200) {
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
