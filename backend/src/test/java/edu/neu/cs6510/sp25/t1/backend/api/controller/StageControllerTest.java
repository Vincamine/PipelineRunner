package edu.neu.cs6510.sp25.t1.backend.api.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import edu.neu.cs6510.sp25.t1.backend.service.StageExecutionService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StageControllerTest {

  @Mock
  private StageExecutionService stageExecutionService;

  private StageController stageController;

  private UUID testPipelineExecutionId;
  private UUID testStageId;

  @BeforeEach
  void setUp() {
    testPipelineExecutionId = UUID.randomUUID();
    testStageId = UUID.randomUUID();
    stageController = new StageController(stageExecutionService);
  }

  @Test
  void testGetStageStatus_Success() {
    // Arrange
    String expectedStatus = "RUNNING";
    when(stageExecutionService.getStageStatus(testPipelineExecutionId, testStageId))
            .thenReturn(expectedStatus);

    // Act
    ResponseEntity<?> response = stageController.getStageStatus(testPipelineExecutionId, testStageId);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertTrue(response.getBody().toString().contains("\"status\": \"RUNNING\""));
    verify(stageExecutionService, times(1)).getStageStatus(testPipelineExecutionId, testStageId);
  }

  @Test
  void testGetStageStatus_NullStatus() {
    // Arrange
    when(stageExecutionService.getStageStatus(testPipelineExecutionId, testStageId))
            .thenReturn(null);

    // Act
    ResponseEntity<?> response = stageController.getStageStatus(testPipelineExecutionId, testStageId);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertTrue(response.getBody().toString().contains("\"status\": \"null\""));
    verify(stageExecutionService, times(1)).getStageStatus(testPipelineExecutionId, testStageId);
  }

  @Test
  void testGetStageStatus_EmptyStatus() {
    // Arrange
    String emptyStatus = "";
    when(stageExecutionService.getStageStatus(testPipelineExecutionId, testStageId))
            .thenReturn(emptyStatus);

    // Act
    ResponseEntity<?> response = stageController.getStageStatus(testPipelineExecutionId, testStageId);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertTrue(response.getBody().toString().contains("\"status\": \"\""));
    verify(stageExecutionService, times(1)).getStageStatus(testPipelineExecutionId, testStageId);
  }

  @Test
  void testGetStageStatus_WithServiceException() {
    // Arrange
    when(stageExecutionService.getStageStatus(testPipelineExecutionId, testStageId))
            .thenThrow(new RuntimeException("Service error"));

    // Act & Assert
    Exception exception = assertThrows(RuntimeException.class, () ->
            stageController.getStageStatus(testPipelineExecutionId, testStageId));

    assertEquals("Service error", exception.getMessage());
    verify(stageExecutionService, times(1)).getStageStatus(testPipelineExecutionId, testStageId);
  }

  @Test
  void testConstructor() {
    // Act
    StageController controller = new StageController(stageExecutionService);

    // Assert
    assertNotNull(controller);
  }
}