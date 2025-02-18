package edu.neu.cs6510.sp25.t1.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.neu.cs6510.sp25.t1.model.LogEntry;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;

/**
 * Service class for retrieving logs from an external API.
 * Handles HTTP requests, JSON deserialization, and error handling.
 */
public class LogService {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private static final String BASE_URL = "https://example.com/api/log/"; // Placeholder API

    /**
     * Constructs a LogService with a given HTTP client.
     * If {@code httpClient} is null, a default one is created.
     *
     * @param httpClient An instance of {@link HttpClient} for sending HTTP
     *                   requests.
     */
    public LogService(HttpClient httpClient) {
        this.httpClient = httpClient != null ? httpClient : HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper(); // Ensure it's properly initialized and used
    }

    /**
     * Fetches logs for a given pipeline ID from the external API.
     *
     * @param pipelineId The pipeline ID to retrieve logs for.
     * @return A list of {@link LogEntry} objects if successful; otherwise, an empty
     *         list.
     */
    public List<LogEntry> getLogsByPipelineId(String pipelineId) {
        if (pipelineId == null || pipelineId.trim().isEmpty()) {
            System.err.println("Error: Pipeline ID cannot be null or empty.");
            return Collections.emptyList();
        }

        final String requestUrl = BASE_URL + pipelineId;
        final HttpRequest request = buildHttpRequest(requestUrl);

        try {
            final HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return processResponse(response);
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Failed to fetch logs: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Constructs an HTTP GET request.
     *
     * @param url The request URL.
     * @return A configured {@link HttpRequest} object.
     */
    private HttpRequest buildHttpRequest(String url) {
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/json")
                .GET()
                .build();
    }

    /**
     * Processes the HTTP response and converts it into a list of log entries.
     *
     * @param response The HTTP response.
     * @return A list of {@link LogEntry} objects or an empty list if an error
     *         occurs.
     */
    private List<LogEntry> processResponse(HttpResponse<String> response) {
        if (response.statusCode() == 200) {
            return parseJsonResponse(response.body());
        } else {
            System.err.println("Error fetching logs. HTTP Status: " + response.statusCode());
            return Collections.emptyList();
        }
    }

    /**
     * Parses a JSON response into a list of {@link LogEntry} objects.
     *
     * @param jsonResponse The JSON response body.
     * @return A list of log entries or an empty list if parsing fails.
     */
    private List<LogEntry> parseJsonResponse(String jsonResponse) {
        try {
            return objectMapper.readValue(jsonResponse, new TypeReference<List<LogEntry>>() {
            });
        } catch (JsonProcessingException e) {
            System.err.println("Error parsing JSON response: " + e.getMessage());
            return Collections.emptyList();
        }
    }
}
