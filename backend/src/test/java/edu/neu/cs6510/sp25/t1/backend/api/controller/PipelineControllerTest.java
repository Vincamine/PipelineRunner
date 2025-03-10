package edu.neu.cs6510.sp25.t1.backend.api.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import edu.neu.cs6510.sp25.t1.backend.service.PipelineExecutionService;
import edu.neu.cs6510.sp25.t1.common.api.response.PipelineExecutionResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PipelineControllerTest {

  @Mock
  private PipelineExecutionService pipelineExecutionService;

  private PipelineController pipelineController;

  private UUID testExecutionId;

  @BeforeEach
  void setUp() {
    testExecutionId = UUID.randomUUID();
    pipelineController = new PipelineController(pipelineExecutionService);
  }

  @Test
  void testGetPipelineStatus_Success() {
    // Arrange
    PipelineExecutionResponse expectedResponse = mock(PipelineExecutionResponse.class);
    doReturn(expectedResponse).when(pipelineExecutionService).getPipelineExecution(any(UUID.class));

    // Act
    ResponseEntity<?> response = pipelineController.getPipelineStatus(testExecutionId);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(expectedResponse, response.getBody());
    verify(pipelineExecutionService, times(1)).getPipelineExecution(testExecutionId);
  }

  @Test
  void testGetPipelineStatus_NotFound() {
    // Arrange
    doThrow(new IllegalArgumentException("Pipeline execution not found"))
            .when(pipelineExecutionService).getPipelineExecution(any(UUID.class));

    // Act
    ResponseEntity<?> response = pipelineController.getPipelineStatus(testExecutionId);

    // Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertTrue(response.getBody().toString().contains("Pipeline execution not found"));
    verify(pipelineExecutionService, times(1)).getPipelineExecution(testExecutionId);
  }

  @Test
  void testConstructor() {
    // Arrange & Act
    PipelineController controller = new PipelineController(pipelineExecutionService);

    // Assert
    assertNotNull(controller);
  }

  @Test
  void testGetPipelineStatus_WithNullId() {
    // Arrange
    UUID nullId = null;
    doThrow(new IllegalArgumentException("Pipeline execution ID cannot be null"))
            .when(pipelineExecutionService).getPipelineExecution(nullId);

    // Act
    ResponseEntity<?> response = pipelineController.getPipelineStatus(nullId);

    // Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertTrue(response.getBody().toString().contains("Pipeline execution not found"));
  }
}