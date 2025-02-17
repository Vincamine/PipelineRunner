package edu.neu.cs6510.sp25.t1.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.neu.cs6510.sp25.t1.model.LogEntry;
import edu.neu.cs6510.sp25.t1.model.LogLevel;
import edu.neu.cs6510.sp25.t1.service.LogService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

class LogServiceTest {
  @Mock
  private HttpClient mockHttpClient;

  @Mock
  private HttpResponse<Object> mockHttpResponse;

  private LogService logService;
  private final ObjectMapper objectMapper = new ObjectMapper();

  @BeforeEach
  void setUp() throws Exception {
    MockitoAnnotations.openMocks(this);
    logService = new LogService(mockHttpClient);
  }

  @Test
  void testGetLogsOnlineByPipelineId_Success() throws Exception {
    final List<LogEntry> mockLogs = List.of(
        new LogEntry("123", LogLevel.INFO, "Pipeline execution started", System.currentTimeMillis())
    );

    final String jsonResponse = objectMapper.writeValueAsString(mockLogs);

    when(mockHttpResponse.statusCode()).thenReturn(200);
    when(mockHttpResponse.body()).thenReturn(jsonResponse);
    when(mockHttpClient.send(any(HttpRequest.class), any())).thenReturn(mockHttpResponse);

    final List<LogEntry> result = logService.getLogsByPipelineId("123");

    assertEquals(1, result.size());
    assertEquals("Pipeline execution started", result.get(0).getMessage());
  }
}