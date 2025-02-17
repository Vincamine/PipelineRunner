package edu.neu.cs6510.sp25.t1.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import edu.neu.cs6510.sp25.t1.model.LogEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;


import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class LogServiceTest {

    private HttpClient mockHttpClient;
    private HttpResponse<Object> mockResponse;
    private LogService logService;
    @SuppressWarnings("unchecked")
    @BeforeEach
    void setUp() {
        mockHttpClient = mock(HttpClient.class);
        mockResponse = mock(HttpResponse.class);
        logService = new LogService(mockHttpClient);
        new ObjectMapper();
    }

    @Test
    void testGetLogsByPipelineId_Success() throws Exception {
        // Arrange
        final String pipelineId = "12345";
        final String jsonResponse = "[{\"timestamp\": 1678901234, \"level\": \"INFO\", \"message\": \"Pipeline started.\"}]";

        when(mockHttpClient.send(any(HttpRequest.class), any())).thenReturn(mockResponse);
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn(jsonResponse);

        // Act
        final List<LogEntry> logs = logService.getLogsByPipelineId(pipelineId);

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
        List<LogEntry> logs = logService.getLogsByPipelineId(null);
        
                // Assert
                assertTrue(logs.isEmpty(), "Logs list should be empty for a null pipeline ID");
        
                logs = logService.getLogsByPipelineId("  ");  // Empty string
        assertTrue(logs.isEmpty(), "Logs list should be empty for an empty pipeline ID");
    }

    @Test
    void testGetLogsByPipelineId_ApiError() throws Exception {
        // Arrange
        when(mockHttpClient.send(any(HttpRequest.class), any())).thenReturn(mockResponse);
        when(mockResponse.statusCode()).thenReturn(500);
        when(mockResponse.body()).thenReturn("Internal Server Error");

        // Act
        final List<LogEntry> logs = logService.getLogsByPipelineId("12345");

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
        final List<LogEntry> logs = logService.getLogsByPipelineId("12345");

        // Assert
        assertTrue(logs.isEmpty(), "Logs should be empty if JSON parsing fails");
    }

    @Test
    void testGetLogsByPipelineId_HttpRequestException() throws Exception {
        // Arrange
        when(mockHttpClient.send(any(HttpRequest.class), any())).thenThrow(new RuntimeException("Connection error"));

        // Act
        final List<LogEntry> logs = logService.getLogsByPipelineId("12345");

        // Assert
        assertTrue(logs.isEmpty(), "Logs should be empty when HTTP request fails");
    }
}
