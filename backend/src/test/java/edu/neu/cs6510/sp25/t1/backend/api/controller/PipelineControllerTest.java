package edu.neu.cs6510.sp25.t1.backend.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.neu.cs6510.sp25.t1.backend.service.PipelineExecutionService;
import edu.neu.cs6510.sp25.t1.common.api.request.PipelineExecutionRequest;
import edu.neu.cs6510.sp25.t1.common.api.response.PipelineExecutionResponse;

@ExtendWith(MockitoExtension.class)
public class PipelineControllerTest {

  @Mock
  private PipelineExecutionService pipelineExecutionService;

  @InjectMocks
  private PipelineController pipelineController;

  private PipelineExecutionRequest request;
  private UUID executionId;

  @BeforeEach
  public void setup() {
    executionId = UUID.randomUUID();
    request = mock(PipelineExecutionRequest.class);
  }

  @Test
  public void testExecutePipeline() {
    // Arrange
    PipelineExecutionResponse mockResponse = mock(PipelineExecutionResponse.class);
    when(pipelineExecutionService.startPipelineExecution(request)).thenReturn(mockResponse);

    // Act
    PipelineExecutionResponse result = pipelineController.executePipeline(request);

    // Assert
    assertEquals(mockResponse, result);
    verify(pipelineExecutionService, times(1)).startPipelineExecution(request);
  }

  @Test
  public void testGetPipelineStatus() {
    // Arrange
    PipelineExecutionResponse mockResponse = mock(PipelineExecutionResponse.class);
    when(pipelineExecutionService.getPipelineExecution(executionId)).thenReturn(mockResponse);

    // Act
    PipelineExecutionResponse result = pipelineController.getPipelineStatus(executionId);

    // Assert
    assertEquals(mockResponse, result);
    verify(pipelineExecutionService, times(1)).getPipelineExecution(executionId);
  }

  @Test
  public void testCheckDuplicateExecution_WhenDuplicateExists() {
    // Arrange
    when(pipelineExecutionService.isDuplicateExecution(request)).thenReturn(true);

    // Act
    boolean isDuplicate = pipelineController.checkDuplicateExecution(request);

    // Assert
    assertTrue(isDuplicate);
    verify(pipelineExecutionService, times(1)).isDuplicateExecution(request);
  }

  @Test
  public void testCheckDuplicateExecution_WhenNoDuplicateExists() {
    // Arrange
    when(pipelineExecutionService.isDuplicateExecution(request)).thenReturn(false);

    // Act
    boolean isDuplicate = pipelineController.checkDuplicateExecution(request);

    // Assert
    assertFalse(isDuplicate);
    verify(pipelineExecutionService, times(1)).isDuplicateExecution(request);
  }

  @Test
  public void testExecutePipeline_WithNullResult() {
    // Arrange
    when(pipelineExecutionService.startPipelineExecution(request)).thenReturn(null);

    // Act
    PipelineExecutionResponse result = pipelineController.executePipeline(request);

    // Assert
    assertNull(result);
    verify(pipelineExecutionService, times(1)).startPipelineExecution(request);
  }

  @Test
  public void testGetPipelineStatus_WithDifferentId() {
    // Arrange
    UUID differentId = UUID.randomUUID();
    PipelineExecutionResponse mockResponse = mock(PipelineExecutionResponse.class);
    when(pipelineExecutionService.getPipelineExecution(differentId)).thenReturn(mockResponse);

    // Act
    PipelineExecutionResponse result = pipelineController.getPipelineStatus(differentId);

    // Assert
    assertEquals(mockResponse, result);
    verify(pipelineExecutionService, times(1)).getPipelineExecution(differentId);
  }
}