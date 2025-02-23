package edu.neu.cs6510.sp25.t1.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.neu.cs6510.sp25.t1.model.ReportEntry;

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
 * Service class for retrieving pipeline execution reports.
 * Supports both remote API and local file system operations.
 */
public class ReportService {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private static final String BASE_URL = "https://example.com/api/report/";

    /**
     * Constructs a ReportService with a given HTTP client.
     *
     * @param httpClient An instance of HttpClient for sending HTTP requests
     */
    public ReportService(HttpClient httpClient) {
        this.httpClient = httpClient != null ? httpClient : HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Retrieves all available pipeline names.
     *
     * @param repoUrl The repository URL. If null, retrieves from local repository
     * @return List of pipeline names
     */
    public List<String> getAllPipelineNames(String repoUrl) {
        if (repoUrl == null) {
            return getAllLocalPipelineNames();
        }
        return fetchPipelineNames(BASE_URL + "pipelines?repo=" + repoUrl);
    }

    /**
     * Retrieves reports for all pipelines in a repository.
     *
     * @param repoUrl The repository URL
     * @return List of ReportEntry objects
     */
    public List<ReportEntry> getRepositoryReports(String repoUrl) {
        return fetchReports(BASE_URL + "repo?url=" + repoUrl);
    }

    /**
     * Retrieves reports from local repository.
     *
     * @return List of ReportEntry objects
     */
    public List<ReportEntry> getLocalRepositoryReports() {
        return fetchLocalReports(".ci-cd-history/reports.json");
    }

    /**
     * Retrieves all runs for a specific pipeline in local repository.
     *
     * @param pipelineName Pipeline identifier
     * @return List of ReportEntry objects
     */
    public List<ReportEntry> getLocalPipelineRuns(String pipelineName) {
        return fetchLocalReports(".ci-cd-history/" + pipelineName + "/runs.json");
    }

    /**
     * Retrieves a specific run summary from local repository.
     *
     * @param pipelineName Pipeline identifier
     * @param runNumber    Run identifier
     * @return List of ReportEntry objects
     */
    public List<ReportEntry> getLocalPipelineRunSummary(String pipelineName, int runNumber) {
        return fetchLocalReports(".ci-cd-history/" + pipelineName + "/run-" + runNumber + ".json");
    }

    /**
     * Retrieves all runs for a specific pipeline.
     *
     * @param repoUrl      The repository URL
     * @param pipelineName Pipeline identifier
     * @return List of ReportEntry objects
     */
    public List<ReportEntry> getPipelineRuns(String repoUrl, String pipelineName) {
        return fetchReports(BASE_URL + "pipeline?repo=" + repoUrl + "&name=" + pipelineName);
    }

    /**
     * Retrieves a specific pipeline run summary.
     *
     * @param repoUrl      The repository URL
     * @param pipelineName Pipeline identifier
     * @param runNumber    Run identifier
     * @return List of ReportEntry objects
     */
    public List<ReportEntry> getPipelineRunSummary(String repoUrl, String pipelineName, int runNumber) {
        return fetchReports(BASE_URL + "run?repo=" + repoUrl + "&pipeline=" + pipelineName + "&run=" + runNumber);
    }

    /**
     * Retrieves a report for a specific stage.
     *
     * @param repoUrl      The repository URL
     * @param pipelineName Pipeline identifier
     * @param runNumber    Run identifier
     * @param stageName    Stage identifier
     * @return List of ReportEntry objects
     */
    public List<ReportEntry> getStageReport(String repoUrl, String pipelineName, int runNumber, String stageName) {
        if (repoUrl == null) {
            return getLocalStageReport(pipelineName, runNumber, stageName);
        }
        return fetchReports(BASE_URL + "stage?repo=" + repoUrl +
                "&pipeline=" + pipelineName +
                "&run=" + runNumber +
                "&stage=" + stageName);
    }

    /**
     * Retrieves a report for a specific job.
     *
     * @param repoUrl      The repository URL
     * @param pipelineName Pipeline identifier
     * @param runNumber    Run identifier
     * @param stageName    Stage identifier
     * @param jobName      Job identifier
     * @return List of ReportEntry objects
     */
    public List<ReportEntry> getJobReport(String repoUrl, String pipelineName,
            int runNumber, String stageName, String jobName) {
        if (repoUrl == null) {
            return getLocalJobReport(pipelineName, runNumber, stageName, jobName);
        }
        return fetchReports(BASE_URL + "job?repo=" + repoUrl +
                "&pipeline=" + pipelineName +
                "&run=" + runNumber +
                "&stage=" + stageName +
                "&job=" + jobName);
    }

    /**
     * Retrieves pipeline names from local directory.
     *
     * @return List of pipeline names
     */
    private List<String> getAllLocalPipelineNames() {
        try {
            return Files.list(Paths.get(".ci-cd-history"))
                    .filter(Files::isDirectory)
                    .map(path -> path.getFileName().toString())
                    .toList();
        } catch (IOException e) {
            System.err.println("Error reading local pipeline names: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Retrieves a stage report from local repository.
     */
    private List<ReportEntry> getLocalStageReport(String pipelineName, int runNumber, String stageName) {
        return fetchLocalReports(".ci-cd-history/" + pipelineName +
                "/run-" + runNumber +
                "/stages/" + stageName + ".json");
    }

    /**
     * Retrieves a job report from local repository.
     */
    private List<ReportEntry> getLocalJobReport(String pipelineName, int runNumber,
            String stageName, String jobName) {
        return fetchLocalReports(".ci-cd-history/" + pipelineName +
                "/run-" + runNumber +
                "/stages/" + stageName +
                "/jobs/" + jobName + ".json");
    }

    /**
     * Fetches reports from a remote URL.
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
     * Fetches pipeline names from a remote URL.
     */
    private List<String> fetchPipelineNames(String requestUrl) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(requestUrl))
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return objectMapper.readValue(response.body(), new TypeReference<List<String>>() {
                });
            }
            System.err.println("Error fetching pipeline names. HTTP Status: " + response.statusCode());
            return Collections.emptyList();
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Failed to fetch pipeline names: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Reads and parses local JSON report files.
     */
    private List<ReportEntry> fetchLocalReports(String filePath) {
        try {
            if (!Files.exists(Paths.get(filePath))) {
                throw new IOException("Report file not found: " + filePath);
            }
            String jsonContent = new String(Files.readAllBytes(Paths.get(filePath)));
            return objectMapper.readValue(jsonContent, new TypeReference<List<ReportEntry>>() {
            });
        } catch (IOException e) {
            System.err.println("Error reading local reports: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Processes HTTP response and converts to ReportEntry list.
     */
    private List<ReportEntry> processResponse(HttpResponse<String> response) {
        if (response.statusCode() == 200) {
            return parseJsonResponse(response.body());
        } else {
            System.err.println("Error fetching reports. HTTP Status: " + response.statusCode());
            return Collections.emptyList();
        }
    }

    /**
     * Parses JSON response into ReportEntry objects.
     */
    private List<ReportEntry> parseJsonResponse(String jsonResponse) {
        try {
            return objectMapper.readValue(jsonResponse, new TypeReference<List<ReportEntry>>() {
            });
        } catch (JsonProcessingException e) {
            System.err.println("Error parsing JSON response: " + e.getMessage());
            return Collections.emptyList();
        }
    }
}