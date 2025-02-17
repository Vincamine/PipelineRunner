package edu.neu.cs6510.sp25.t1.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.neu.cs6510.sp25.t1.model.LogEntry;
import java.util.List;
import java.net.http.HttpClient;

/**
 * Service class responsible for retrieving pipeline execution logs.
 * <p>
 * 🚀 **Mock Implementation** for demo purposes.
 * - This service **simulates** retrieving logs from an API.
 * - Once the backend API is available, **replace the mock logic with an actual API call**.
 * </p>
 */
public class LogService {
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    /**
     * Constructs a LogService with a given HTTP client.
     * If {@code httpClient} is null, a default one is created.
     *
     * @param httpClient An instance of {@link HttpClient} for sending HTTP requests.
     */
    public LogService(HttpClient httpClient) {
        this.httpClient = httpClient != null ? httpClient : HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Retrieves mock logs for a pipeline.
     *
     * @param pipelineId The unique identifier of the pipeline.
     * @return A list of {@link LogEntry} objects containing mock log data.
     */
    public List<LogEntry> getLogsByPipelineId(String pipelineId) {
        if (pipelineId == null || pipelineId.trim().isEmpty()) {
            throw new IllegalArgumentException("Pipeline ID cannot be null or empty.");
        }

        // 🚀 TODO: Replace this with an actual API call when backend is ready
        return List.of(
            new LogEntry(pipelineId, "INFO", "Pipeline execution started", System.currentTimeMillis()),
            new LogEntry(pipelineId, "INFO", "Job 1 completed", System.currentTimeMillis()),
            new LogEntry(pipelineId, "WARN", "Job 2 encountered a delay", System.currentTimeMillis()),
            new LogEntry(pipelineId, "INFO", "Pipeline execution finished", System.currentTimeMillis())
        );
    }
}
