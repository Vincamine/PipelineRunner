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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

class ReportServiceTest {
  private HttpClient mockHttpClient;
  private HttpResponse<Object> mockResponse;
  private ReportService reportService;

  @SuppressWarnings("unchecked")
  @BeforeEach
  void setUp() {
    mockHttpClient = mock(HttpClient.class);
    mockResponse = mock(HttpResponse.class);
    reportService = new ReportService(mockHttpClient);
    new ObjectMapper();
  }

  @Test
  void testGetReportsByPipelineId_Success() throws Exception {
    // Arrange
    final String pipelineId = "12345";
    final String jsonResponse = "[{\"timestamp\": 1678901234, \"level\": \"SUCCESS\", \"message\": \"Pipeline started.\"}]";

    when(mockHttpClient.send(any(HttpRequest.class), any())).thenReturn(mockResponse);
    when(mockResponse.statusCode()).thenReturn(200);
    when(mockResponse.body()).thenReturn(jsonResponse);

    // Act
    final List<ReportEntry> reports = reportService.getReportsByPipelineId(pipelineId);
    System.out.println(reports);

    // Assert
    assertFalse(reports.isEmpty(), "Reports list should not be empty");
    assertEquals(1, reports.size(), "Only one report entry should be present");
    assertEquals("Pipeline started.", reports.get(0).getMessage(), "Message should match the JSON response");

    // Verify HTTP request
    final ArgumentCaptor<HttpRequest> requestCaptor = ArgumentCaptor.forClass(HttpRequest.class);
    verify(mockHttpClient).send(requestCaptor.capture(), any());

    final HttpRequest capturedRequest = requestCaptor.getValue();
    assertEquals(URI.create("https://example.com/api/report/" + pipelineId), capturedRequest.uri());
    assertEquals("GET", capturedRequest.method());
  }

  @Test
  void testGetReportsByPipelineId_InvalidPipelineId() {
    // Act
    List<ReportEntry> reports = reportService.getReportsByPipelineId(null);

    // Assert
    assertTrue(reports.isEmpty(), "Reports list should be empty for a null pipeline ID");

    reports = reportService.getReportsByPipelineId("  "); // Empty string
    assertTrue(reports.isEmpty(), "Reports list should be empty for an empty pipeline ID");
  }

  @Test
  void testGetReportsByPipelineId_ApiError() throws Exception {
    // Arrange
    when(mockHttpClient.send(any(HttpRequest.class), any())).thenReturn(mockResponse);
    when(mockResponse.statusCode()).thenReturn(500);
    when(mockResponse.body()).thenReturn("Internal Server Error");

    // Act
    final List<ReportEntry> reports = reportService.getReportsByPipelineId("12345");

    // Assert
    assertTrue(reports.isEmpty(), "Reports should be empty on API error");
  }

  @Test
  void testGetReportsByPipelineId_JsonProcessingError() throws Exception {
    // Arrange
    when(mockHttpClient.send(any(HttpRequest.class), any())).thenReturn(mockResponse);
    when(mockResponse.statusCode()).thenReturn(200);
    when(mockResponse.body()).thenReturn("Invalid JSON Response"); // Corrupt JSON

    // Act
    final List<ReportEntry> reports = reportService.getReportsByPipelineId("12345");

    // Assert
    assertTrue(reports.isEmpty(), "reports should be empty if JSON parsing fails");
  }

  @Test
  void testGetReportsByPipelineId_HttpRequestException() throws Exception {
    // Arrange: Make the mock HttpClient throw an IOException
    when(mockHttpClient.send(any(HttpRequest.class), any()))
        .thenThrow(new IOException("Simulated connection error"));

    // Act
    final List<ReportEntry> reports = reportService.getReportsByPipelineId("12345");

    // Assert
    assertNotNull(reports, "Reports list should not be null");
    assertTrue(reports.isEmpty(), "Reports should be empty when HTTP request fails");
  }

}