package edu.neu.cs6510.sp25.t1.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.neu.cs6510.sp25.t1.model.ReportEntry;
import edu.neu.cs6510.sp25.t1.model.ReportLevel;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

/**
 * Service class for retrieving reports from an external API or local repository.
 * Handles HTTP requests, JSON deserialization, and error handling.
 */
public class ReportService {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private static final String BASE_URL = "https://example.com/api/report/"; // Placeholder API

    /**
     * Constructs a ReportService with a given HTTP client.
     * If {@code httpClient} is null, a default one is created.
     *
     * @param httpClient An instance of {@link HttpClient} for sending HTTP requests.
     */
    public ReportService(HttpClient httpClient) {
        this.httpClient = httpClient != null ? httpClient : HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper(); // Ensure it's properly initialized and used
    }

    /**
     * Fetches reports for all pipelines in a given repository.
     *
     * @param repoUrl The repository URL.
     * @return A list of {@link ReportEntry} objects.
     */
    public List<ReportEntry> getRepositoryReports(String repoUrl) {
        return fetchReports(BASE_URL + "repo?url=" + repoUrl);
    }

    /**
     * Fetches reports for all pipelines in local.
     *
     * @return A list of {@link ReportEntry} objects.
     */
    public List<ReportEntry> getLocalRepositoryReports() {
        return fetchLocalReports(".ci-cd-history/reports.json");
    }

    /**
     * Fetches all reports for a pipelineName/pipelineId in local.
     *
     * @param pipelineName given pipelineName/pipelineId
     * @return A list of {@link ReportEntry} objects.
     */
    public List<ReportEntry> getLocalPipelineRuns(String pipelineName) {
        return fetchLocalReports(".ci-cd-history/" + pipelineName + "/runs.json");
    }

    /**
     * Fetches all reports for a given pipelineName/pipelineId and a given runs in local.
     *
     * @param pipelineName given pipelineName/pipelineId
     * @param runNumber given runNumber
     * @return A list of {@link ReportEntry} objects.
     */
    public List<ReportEntry> getLocalPipelineRunSummary(String pipelineName, int runNumber) {
        return fetchLocalReports(".ci-cd-history/" + pipelineName + "/run-" + runNumber + ".json");
    }

    /**
     * Fetches all runs for a specific pipeline in a repository.
     *
     * @param repoUrl The repository URL.
     * @param pipelineName The pipeline name.
     * @return A list of {@link ReportEntry} objects.
     */
    public List<ReportEntry> getPipelineRuns(String repoUrl, String pipelineName) {
        return fetchReports(BASE_URL + "pipeline?repo=" + repoUrl + "&name=" + pipelineName);
    }

    /**
     * Fetches a specific pipeline run summary.
     *
     * @param repoUrl The repository URL.
     * @param pipelineName The pipeline name.
     * @param runNumber The specific run number.
     * @return A list of {@link ReportEntry} objects.
     */
    public List<ReportEntry> getPipelineRunSummary(String repoUrl, String pipelineName, int runNumber) {
        return fetchReports(BASE_URL + "run?repo=" + repoUrl + "&pipeline=" + pipelineName + "&run=" + runNumber);
    }

    /**
     * Makes an HTTP GET request to fetch reports.
     *
     * @param requestUrl The request URL.
     * @return A list of {@link ReportEntry} objects or an empty list if an error occurs.
     */
    private List<ReportEntry> fetchReports(String requestUrl) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(requestUrl))
                .header("Accept", "application/json")
                .GET()
                .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return processResponse(response);
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Failed to fetch reports: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Reads local JSON report files.
     *
     * @param filePath The file path.
     * @return A list of {@link ReportEntry} objects.
     */
    private List<ReportEntry> fetchLocalReports(String filePath) {
        try {
            String jsonContent = new String(Files.readAllBytes(Paths.get(filePath)));
            return objectMapper.readValue(jsonContent, new TypeReference<List<ReportEntry>>() {});
        } catch (IOException e) {
            System.err.println("Error reading local reports: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Processes the HTTP response and converts it into a list of Report entries.
     *
     * @param response The HTTP response.
     * @return A list of {@link ReportEntry} objects or an empty list if an error occurs.
     */
    private List<ReportEntry> processResponse(HttpResponse<String> response) {
        if (response.statusCode() == 200) {
            return parseJsonResponse(response.body());
        } else {
            System.err.println("Error fetching reports. HTTP Status: " + response.statusCode());
            return Collections.emptyList();
        }
    }

    private List<ReportEntry> parseJsonResponse(String jsonResponse) {
        try {
            return objectMapper.readValue(jsonResponse, new TypeReference<List<ReportEntry>>() {});
        } catch (JsonProcessingException e) {
            System.err.println("Error parsing JSON response: " + e.getMessage());
            return Collections.emptyList();
        }
    }



}