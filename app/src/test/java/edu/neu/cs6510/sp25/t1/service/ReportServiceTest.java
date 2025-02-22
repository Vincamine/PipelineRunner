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

class ReportServiceTest {
  private HttpClient mockHttpClient;
  private HttpResponse<String> mockResponse;
  private ReportService reportService;
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    mockHttpClient = mock(HttpClient.class);
    mockResponse = mock(HttpResponse.class);
    reportService = new ReportService(mockHttpClient);
    objectMapper = new ObjectMapper();
  }

  @Test
  void testGetRepositoryReports_Success() throws Exception {
    final String repoUrl = "https://github.com/test/repo";
    final String jsonResponse = "[{\"pipelineId\": \"12345\", \"level\": \"SUCCESS\", \"message\": \"Pipeline completed.\"}]";

    when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
        .thenReturn(mockResponse);
    when(mockResponse.statusCode()).thenReturn(200);
    when(mockResponse.body()).thenReturn(jsonResponse);

    final List<ReportEntry> reports = reportService.getRepositoryReports(repoUrl);

    assertFalse(reports.isEmpty(), "Reports list should not be empty");
    assertEquals(1, reports.size(), "Only one report entry should be present");
  }

  @Test
  void testGetPipelineRunSummary_ApiError() throws Exception {
    when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
        .thenReturn(mockResponse);
    when(mockResponse.statusCode()).thenReturn(500);
    when(mockResponse.body()).thenReturn("Internal Server Error");

    final List<ReportEntry> reports = reportService.getPipelineRunSummary("repo-url", "pipeline-name", 1);

    assertTrue(reports.isEmpty(), "Reports should be empty on API error");
  }

  @Test
  void testGetPipelineRunSummary_JsonProcessingError() throws Exception {
    when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
        .thenReturn(mockResponse);
    when(mockResponse.statusCode()).thenReturn(200);
    when(mockResponse.body()).thenReturn("Invalid JSON Response");

    final List<ReportEntry> reports = reportService.getPipelineRunSummary("repo-url", "pipeline-name", 1);

    assertTrue(reports.isEmpty(), "Reports should be empty if JSON parsing fails");
  }

  @Test
  void testGetPipelineRunSummary_HttpRequestException() throws Exception {
    when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
        .thenThrow(new IOException("Simulated connection error"));

    final List<ReportEntry> reports = reportService.getPipelineRunSummary("repo-url", "pipeline-name", 1);

    assertNotNull(reports, "Reports list should not be null");
    assertTrue(reports.isEmpty(), "Reports should be empty when HTTP request fails");
  }
}
