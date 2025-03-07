package edu.neu.cs6510.sp25.t1.backend.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.neu.cs6510.sp25.t1.backend.service.StageExecutionService;

@ExtendWith(MockitoExtension.class)
public class StageControllerTest {

  @Mock
  private StageExecutionService stageExecutionService;

  @InjectMocks
  private StageController stageController;

  private UUID pipelineExecutionId;
  private UUID stageId;

  @BeforeEach
  public void setup() {
    pipelineExecutionId = UUID.randomUUID();
    stageId = UUID.randomUUID();
  }

  @Test
  public void testGetStageStatus_Success() {
    // Arrange
    String expectedStatus = "COMPLETED";
    when(stageExecutionService.getStageStatus(pipelineExecutionId, stageId)).thenReturn(expectedStatus);

    // Act
    String actualStatus = stageController.getStageStatus(pipelineExecutionId, stageId);

    // Assert
    assertEquals(expectedStatus, actualStatus);
    verify(stageExecutionService, times(1)).getStageStatus(pipelineExecutionId, stageId);
  }

  @Test
  public void testGetStageStatus_EmptyResponse() {
    // Arrange
    String expectedStatus = "";
    when(stageExecutionService.getStageStatus(pipelineExecutionId, stageId)).thenReturn(expectedStatus);

    // Act
    String actualStatus = stageController.getStageStatus(pipelineExecutionId, stageId);

    // Assert
    assertEquals(expectedStatus, actualStatus);
    verify(stageExecutionService, times(1)).getStageStatus(pipelineExecutionId, stageId);
  }

  @Test
  public void testGetStageStatus_NullResponse() {
    // Arrange
    when(stageExecutionService.getStageStatus(pipelineExecutionId, stageId)).thenReturn(null);

    // Act
    String actualStatus = stageController.getStageStatus(pipelineExecutionId, stageId);

    // Assert
    assertEquals(null, actualStatus);
    verify(stageExecutionService, times(1)).getStageStatus(pipelineExecutionId, stageId);
  }

  @Test
  public void testGetStageStatus_WithNullPipelineId() {
    // Arrange
    UUID nullPipelineId = null;
    IllegalArgumentException expectedException = new IllegalArgumentException("Pipeline execution ID cannot be null");
    when(stageExecutionService.getStageStatus(nullPipelineId, stageId)).thenThrow(expectedException);

    // Act & Assert
    IllegalArgumentException thrownException = assertThrows(IllegalArgumentException.class, () -> {
      stageController.getStageStatus(nullPipelineId, stageId);
    });

    assertEquals(expectedException.getMessage(), thrownException.getMessage());
    verify(stageExecutionService, times(1)).getStageStatus(nullPipelineId, stageId);
  }

  @Test
  public void testGetStageStatus_WithNullStageId() {
    // Arrange
    UUID nullStageId = null;
    IllegalArgumentException expectedException = new IllegalArgumentException("Stage ID cannot be null");
    when(stageExecutionService.getStageStatus(pipelineExecutionId, nullStageId)).thenThrow(expectedException);

    // Act & Assert
    IllegalArgumentException thrownException = assertThrows(IllegalArgumentException.class, () -> {
      stageController.getStageStatus(pipelineExecutionId, nullStageId);
    });

    assertEquals(expectedException.getMessage(), thrownException.getMessage());
    verify(stageExecutionService, times(1)).getStageStatus(pipelineExecutionId, nullStageId);
  }

  @Test
  public void testGetStageStatus_WithSpecialIds() {
    // Arrange - Using zero UUIDs for edge case testing
    UUID zeroUuid = UUID.fromString("00000000-0000-0000-0000-000000000000");
    String expectedStatus = "PENDING";
    when(stageExecutionService.getStageStatus(zeroUuid, zeroUuid)).thenReturn(expectedStatus);

    // Act
    String actualStatus = stageController.getStageStatus(zeroUuid, zeroUuid);

    // Assert
    assertEquals(expectedStatus, actualStatus);
    verify(stageExecutionService, times(1)).getStageStatus(zeroUuid, zeroUuid);
  }
}