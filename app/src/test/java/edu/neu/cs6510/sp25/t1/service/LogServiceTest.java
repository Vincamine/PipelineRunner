package edu.neu.cs6510.sp25.t1.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.neu.cs6510.sp25.t1.model.ReportEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LogServiceTest {

    private HttpClient mockHttpClient;
    private HttpResponse<Object> mockResponse;
    private ReportService logService;

    @SuppressWarnings("unchecked")
    @BeforeEach
    void setUp() {
        mockHttpClient = mock(HttpClient.class);
        mockResponse = mock(HttpResponse.class);
        logService = new ReportService(mockHttpClient);
        new ObjectMapper();
    }

    @Test
    void testGetLogsByPipelineId_Success() throws Exception {
        // Arrange
        final String pipelineId = "12345";
        final String jsonResponse = "[{\"timestamp\": 1678901234, \"level\": \"SUCCESSSUCCESS\", \"message\": \"Pipeline started.\"}]";

        when(mockHttpClient.send(any(HttpRequest.class), any())).thenReturn(mockResponse);
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn(jsonResponse);

        // Act
        final List<ReportEntry> logs = logService.getLogsByPipelineId(pipelineId);

        // Assert
        assertFalse(logs.isEmpty(), "Logs list should not be empty");
        assertEquals(1, logs.size(), "Only one log entry should be present");
        assertEquals("Pipeline started.", logs.get(0).getMessage(), "Message should match the JSON response");

        // Verify HTTP request
        final ArgumentCaptor<HttpRequest> requestCaptor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(mockHttpClient).send(requestCaptor.capture(), any());

        final HttpRequest capturedRequest = requestCaptor.getValue();
        assertEquals(URI.create("https://example.com/api/log/" + pipelineId), capturedRequest.uri());
        assertEquals("GET", capturedRequest.method());
    }

    @Test
    void testGetLogsByPipelineId_InvalidPipelineId() {
        // Act
        List<ReportEntry> logs = logService.getLogsByPipelineId(null);

        // Assert
        assertTrue(logs.isEmpty(), "Logs list should be empty for a null pipeline ID");

        logs = logService.getLogsByPipelineId("  "); // Empty string
        assertTrue(logs.isEmpty(), "Logs list should be empty for an empty pipeline ID");
    }

    @Test
    void testGetLogsByPipelineId_ApiError() throws Exception {
        // Arrange
        when(mockHttpClient.send(any(HttpRequest.class), any())).thenReturn(mockResponse);
        when(mockResponse.statusCode()).thenReturn(500);
        when(mockResponse.body()).thenReturn("Internal Server Error");

        // Act
        final List<ReportEntry> logs = logService.getLogsByPipelineId("12345");

        // Assert
        assertTrue(logs.isEmpty(), "Logs should be empty on API error");
    }

    @Test
    void testGetLogsByPipelineId_JsonProcessingError() throws Exception {
        // Arrange
        when(mockHttpClient.send(any(HttpRequest.class), any())).thenReturn(mockResponse);
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn("Invalid JSON Response"); // Corrupt JSON

        // Act
        final List<ReportEntry> logs = logService.getLogsByPipelineId("12345");

        // Assert
        assertTrue(logs.isEmpty(), "Logs should be empty if JSON parsing fails");
    }

    @Test
    void testGetLogsByPipelineId_HttpRequestException() throws Exception {
        // Arrange: Make the mock HttpClient throw an IOException
        when(mockHttpClient.send(any(HttpRequest.class), any()))
                .thenThrow(new IOException("Simulated connection error"));

        // Act
        final List<ReportEntry> logs = logService.getLogsByPipelineId("12345");

        // Assert
        assertNotNull(logs, "Logs list should not be null");
        assertTrue(logs.isEmpty(), "Logs should be empty when HTTP request fails");
    }
}
