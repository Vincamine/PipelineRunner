package edu.neu.cs6510.sp25.t1.api;

import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BackendClientTest {
    private static MockWebServer mockWebServer;
    private BackendClient backendClient;
    @SuppressWarnings("unused")
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    static void startMockServer() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void stopMockServer() throws IOException {
        mockWebServer.shutdown();
    }

    @BeforeEach
    void setUp() {
        String baseUrl = mockWebServer.url("/").toString(); // Use mock server URL
        backendClient = new BackendClient(baseUrl);
    }

    @Test
    void testRunPipelineSuccess() throws IOException {
        // Mock response JSON
        String mockResponseJson = "{\"status\":\"success\",\"message\":\"Pipeline executed\"}";

        // Enqueue the response
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(mockResponseJson)
                .addHeader("Content-Type", "application/json"));

        // Create request
        RunPipelineRequest request = new RunPipelineRequest("repo", "branch", "commit", "pipeline", true);
        String response = backendClient.runPipeline(request);

        assertNotNull(response);
        assertTrue(response.contains("success"));
    }
}
