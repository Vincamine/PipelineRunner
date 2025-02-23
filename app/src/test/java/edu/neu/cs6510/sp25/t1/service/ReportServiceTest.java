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
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ReportService class.
 * Tests both remote API and local file system operations.
 */
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

  /**
   * Tests successful retrieval of repository reports.
   */
  @Test
  void testGetRepositoryReports_Success() throws Exception {
    final String repoUrl = "https://github.com/test/repo";
    final String jsonResponse = "[{\"pipelineId\": \"12345\", \"level\": \"SUCCESS\", \"message\": \"Pipeline completed.\", \"timestamp\": " + Instant.now().toEpochMilli() + "}]";

    when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
            .thenReturn(mockResponse);
    when(mockResponse.statusCode()).thenReturn(200);
    when(mockResponse.body()).thenReturn(jsonResponse);

    final List<ReportEntry> reports = reportService.getRepositoryReports(repoUrl);

    assertFalse(reports.isEmpty(), "Reports list should not be empty");
    assertEquals(1, reports.size(), "Only one report entry should be present");
    assertEquals("12345", reports.get(0).getPipelineId(), "Pipeline ID should match");

    ArgumentCaptor<HttpRequest> requestCaptor = ArgumentCaptor.forClass(HttpRequest.class);
    verify(mockHttpClient).send(requestCaptor.capture(), any());
    assertTrue(requestCaptor.getValue().uri().toString().contains(repoUrl),
            "Request URL should contain repository URL");
  }

  /**
   * Tests retrieval of pipeline names.
   */
  @Test
  void testGetAllPipelineNames_Success() throws Exception {
    final String repoUrl = "https://github.com/test/repo";
    final String jsonResponse = "[\"pipeline1\", \"pipeline2\"]";

    when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
            .thenReturn(mockResponse);
    when(mockResponse.statusCode()).thenReturn(200);
    when(mockResponse.body()).thenReturn(jsonResponse);

    List<String> pipelineNames = reportService.getAllPipelineNames(repoUrl);

    assertEquals(2, pipelineNames.size(), "Should return correct number of pipeline names");
    assertTrue(pipelineNames.containsAll(Arrays.asList("pipeline1", "pipeline2")),
            "Should contain all pipeline names");
  }

  /**
   * Tests pipeline run summary retrieval with API error.
   */
  @Test
  void testGetPipelineRunSummary_ApiError() throws Exception {
    when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
            .thenReturn(mockResponse);
    when(mockResponse.statusCode()).thenReturn(500);
    when(mockResponse.body()).thenReturn("Internal Server Error");

    final List<ReportEntry> reports = reportService.getPipelineRunSummary("repo-url", "pipeline-name", 1);

    assertTrue(reports.isEmpty(), "Reports should be empty on API error");
  }

  /**
   * Tests pipeline run summary retrieval with JSON processing error.
   */
  @Test
  void testGetPipelineRunSummary_JsonProcessingError() throws Exception {
    when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
            .thenReturn(mockResponse);
    when(mockResponse.statusCode()).thenReturn(200);
    when(mockResponse.body()).thenReturn("Invalid JSON Response");

    final List<ReportEntry> reports = reportService.getPipelineRunSummary("repo-url", "pipeline-name", 1);

    assertTrue(reports.isEmpty(), "Reports should be empty if JSON parsing fails");
  }

  /**
   * Tests pipeline run summary retrieval with HTTP request exception.
   */
  @Test
  void testGetPipelineRunSummary_HttpRequestException() throws Exception {
    when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
            .thenThrow(new IOException("Simulated connection error"));

    final List<ReportEntry> reports = reportService.getPipelineRunSummary("repo-url", "pipeline-name", 1);

    assertNotNull(reports, "Reports list should not be null");
    assertTrue(reports.isEmpty(), "Reports should be empty when HTTP request fails");
  }

  /**
   * Tests successful stage report retrieval.
   */
  @Test
  void testGetStageReport_Success() throws Exception {
    final String jsonResponse = "[{\"pipelineId\":\"stage1\",\"level\":\"SUCCESS\",\"message\":\"Stage completed.\",\"timestamp\":" + Instant.now().toEpochMilli() + "}]";

    when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
            .thenReturn(mockResponse);
    when(mockResponse.statusCode()).thenReturn(200);
    when(mockResponse.body()).thenReturn(jsonResponse);

    List<ReportEntry> reports = reportService.getStageReport("repo-url", "pipeline1", 1, "stage1");

    assertFalse(reports.isEmpty(), "Should return stage reports");
    assertEquals("stage1", reports.get(0).getPipelineId(), "Stage ID should match");
  }

  /**
   * Tests successful job report retrieval.
   */
  @Test
  void testGetJobReport_Success() throws Exception {
    final String jsonResponse = "[{\"pipelineId\":\"job1\",\"level\":\"SUCCESS\",\"message\":\"Job completed.\",\"timestamp\":" + Instant.now().toEpochMilli() + "}]";

    when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
            .thenReturn(mockResponse);
    when(mockResponse.statusCode()).thenReturn(200);
    when(mockResponse.body()).thenReturn(jsonResponse);

    List<ReportEntry> reports = reportService.getJobReport("repo-url", "pipeline1", 1, "stage1", "job1");

    assertFalse(reports.isEmpty(), "Should return job reports");
    assertEquals("job1", reports.get(0).getPipelineId(), "Job ID should match");

    ArgumentCaptor<HttpRequest> requestCaptor = ArgumentCaptor.forClass(HttpRequest.class);
    verify(mockHttpClient).send(requestCaptor.capture(), any());
    String uri = requestCaptor.getValue().uri().toString();
    assertTrue(uri.contains("job=job1"), "Request URL should contain job name");
    assertTrue(uri.contains("stage=stage1"), "Request URL should contain stage name");
  }

  /**
   * Tests successful retrieval of pipeline run summary.
   */
  @Test
  void testGetPipelineRunSummary_Success() throws Exception {
    final String jsonResponse = "[{\"pipelineId\":\"run1\",\"level\":\"SUCCESS\",\"message\":\"Run completed.\",\"timestamp\":" + Instant.now().toEpochMilli() + "}]";

    when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
            .thenReturn(mockResponse);
    when(mockResponse.statusCode()).thenReturn(200);
    when(mockResponse.body()).thenReturn(jsonResponse);

    List<ReportEntry> reports = reportService.getPipelineRunSummary("repo-url", "pipeline1", 1);

    assertFalse(reports.isEmpty(), "Should return run summary");
    assertEquals("run1", reports.get(0).getPipelineId(), "Run ID should match");

    ArgumentCaptor<HttpRequest> requestCaptor = ArgumentCaptor.forClass(HttpRequest.class);
    verify(mockHttpClient).send(requestCaptor.capture(), any());
    String uri = requestCaptor.getValue().uri().toString();
    assertTrue(uri.contains("run=1"), "Request URL should contain run number");
  }

  /**
   * Tests successful retrieval of pipeline runs.
   */
  @Test
  void testGetPipelineRuns_Success() throws Exception {
    final String jsonResponse = "[{\"pipelineId\":\"pipeline1\",\"level\":\"SUCCESS\",\"message\":\"Pipeline runs.\",\"timestamp\":" + Instant.now().toEpochMilli() + "}]";

    when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
            .thenReturn(mockResponse);
    when(mockResponse.statusCode()).thenReturn(200);
    when(mockResponse.body()).thenReturn(jsonResponse);

    List<ReportEntry> reports = reportService.getPipelineRuns("repo-url", "pipeline1");

    assertFalse(reports.isEmpty(), "Should return pipeline runs");
    assertEquals("pipeline1", reports.get(0).getPipelineId(), "Pipeline ID should match");
  }
}