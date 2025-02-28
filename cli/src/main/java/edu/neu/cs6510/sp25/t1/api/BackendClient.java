package edu.neu.cs6510.sp25.t1.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import okhttp3.*;

import java.io.IOException;
import java.util.List;

/**
 * Client for interacting with the backend API.
 */
public class BackendClient {
    private final String baseUrl;
    private final OkHttpClient client;
    private final ObjectMapper objectMapper;
    private final YAMLMapper yamlMapper;

    /**
     * Constructor with parameters.
     * 
     * @param baseUrl The backend server base URL.
     */
    public BackendClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.client = new OkHttpClient();
        this.objectMapper = new ObjectMapper();
        this.yamlMapper = new YAMLMapper();
    }

    /**
     * Handles API responses and ensures proper error reporting.
     */
    private String handleResponse(Response response) throws IOException {
        String responseBody = response.body() != null ? response.body().string() : "";

        if (!response.isSuccessful()) {
            return "Error: " + response.code() + " - " + response.message() + "\n" + responseBody;
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
        String json = objectMapper.writeValueAsString(request);

        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));
        Request req = new Request.Builder()
                .url(baseUrl + "/api/v1/pipelines/run")
                .post(body)
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
        Request req = new Request.Builder()
                .url(baseUrl + "/api/v1/pipelines/" + pipeline + "/executions")
                .get()
                .build();

        try (Response response = client.newCall(req).execute()) {
            String responseBody = handleResponse(response);
            if ("yaml".equalsIgnoreCase(format)) {
                return yamlMapper.writeValueAsString(objectMapper.readTree(responseBody));
            } else if ("json".equalsIgnoreCase(format)) {
                return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(objectMapper.readTree(responseBody));
            }
            return responseBody;
        }
    }
}
