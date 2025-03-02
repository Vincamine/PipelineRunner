package edu.neu.cs6510.sp25.t1.backend.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import edu.neu.cs6510.sp25.t1.backend.dto.PipelineExecutionSummary;
import edu.neu.cs6510.sp25.t1.backend.service.PipelineExecutionService;
import edu.neu.cs6510.sp25.t1.common.api.RunPipelineRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

class PipelineControllerTest {

  @Mock
  private PipelineExecutionService pipelineService;

  @InjectMocks
  private PipelineController pipelineController;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void shouldReturnPipelineExecutionSummaryWhenPipelineRunsSuccessfully() {
    // Given
    RunPipelineRequest request = new RunPipelineRequest("testPipeline");
    PipelineExecutionSummary summary = new PipelineExecutionSummary("testPipeline", "RUNNING");
    when(pipelineService.startPipeline("testPipeline")).thenReturn(Optional.of(summary));

    // When
    ResponseEntity<PipelineExecutionSummary> response = pipelineController.runPipeline(request);

    // Then
    assertEquals(200, response.getStatusCode().value());
    assertNotNull(response.getBody());
    assertEquals("testPipeline", response.getBody().getPipelineName());
    assertEquals("RUNNING", response.getBody().getStatus());
  }

  @Test
  void shouldReturnNotFoundWhenPipelineDoesNotExist() {
    // Given
    RunPipelineRequest request = new RunPipelineRequest("nonExistentPipeline");
    when(pipelineService.startPipeline("nonExistentPipeline")).thenReturn(Optional.empty());

    // When
    ResponseEntity<PipelineExecutionSummary> response = pipelineController.runPipeline(request);

    // Then
    assertEquals(404, response.getStatusCode().value());
    assertNull(response.getBody());
  }

  @Test
  void shouldReturnPipelineExecutionSummaryWhenStatusIsRetrieved() {
    // Given
    PipelineExecutionSummary summary = new PipelineExecutionSummary("testPipeline", "SUCCESS");
    when(pipelineService.getPipelineExecution("testPipeline")).thenReturn(Optional.of(summary));

    // When
    ResponseEntity<PipelineExecutionSummary> response = pipelineController.getStatus("testPipeline");

    // Then
    assertEquals(200, response.getStatusCode().value());
    assertNotNull(response.getBody());
    assertEquals("testPipeline", response.getBody().getPipelineName());
    assertEquals("SUCCESS", response.getBody().getStatus());
  }

  @Test
  void shouldReturnNotFoundWhenPipelineExecutionStatusNotAvailable() {
    // Given
    when(pipelineService.getPipelineExecution("nonExistentPipeline")).thenReturn(Optional.empty());

    // When
    ResponseEntity<PipelineExecutionSummary> response = pipelineController.getStatus("nonExistentPipeline");

    // Then
    assertEquals(404, response.getStatusCode().value());
    assertNull(response.getBody());
  }
}
