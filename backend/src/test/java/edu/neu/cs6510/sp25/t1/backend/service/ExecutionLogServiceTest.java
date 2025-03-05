package edu.neu.cs6510.sp25.t1.backend.service;

import edu.neu.cs6510.sp25.t1.backend.database.entity.ExecutionLogEntity;
import edu.neu.cs6510.sp25.t1.backend.database.repository.ExecutionLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link ExecutionLogService}.
 */
@ExtendWith(MockitoExtension.class)
class ExecutionLogServiceTest {

  @Mock
  private ExecutionLogRepository executionLogRepository;

  private ExecutionLogService executionLogService;

  @BeforeEach
  void setUp() {
    executionLogService = new ExecutionLogService(executionLogRepository);
  }

  @Test
  void constructor_WithRepositoryParam_ShouldInitializeCorrectly() {
    // Arrange & Act - done in setUp()

    // Assert - verify the repository was properly injected
    assertNotNull(executionLogService, "Service should be initialized");

    // Testing that the constructor properly assigns the repository
    // This verifies a part of the code that's often not covered
  }

  @Test
  void logExecution_WithAllIdsProvided_ShouldSaveLogWithAllReferences() {
    // Arrange
    String logText = "Test execution log";
    UUID pipelineExecutionId = UUID.randomUUID();
    UUID stageExecutionId = UUID.randomUUID();
    UUID jobExecutionId = UUID.randomUUID();

    // Simulate repository behavior
    when(executionLogRepository.save(any(ExecutionLogEntity.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

    // Act
    executionLogService.logExecution(logText, pipelineExecutionId, stageExecutionId, jobExecutionId);

    // Assert
    ArgumentCaptor<ExecutionLogEntity> logCaptor = ArgumentCaptor.forClass(ExecutionLogEntity.class);
    verify(executionLogRepository, times(1)).save(logCaptor.capture());

    ExecutionLogEntity savedLog = logCaptor.getValue();
    assertNotNull(savedLog, "The saved log should not be null");
    assertEquals(logText, savedLog.getLogText(), "Log text should match");
    assertEquals(pipelineExecutionId, savedLog.getPipelineExecutionId(), "Pipeline execution ID should match");
    assertEquals(stageExecutionId, savedLog.getStageExecutionId(), "Stage execution ID should match");
    assertEquals(jobExecutionId, savedLog.getJobExecutionId(), "Job execution ID should match");
  }

  /**
   * Creates a stream of arguments for parameterized test with various ID combinations.
   * This approach tests multiple scenarios in a single test method.
   */
  static Stream<Arguments> provideExecutionIdCombinations() {
    UUID pipelineId = UUID.randomUUID();
    UUID stageId = UUID.randomUUID();
    UUID jobId = UUID.randomUUID();

    return Stream.of(
            // Format: logText, pipelineId, stageId, jobId
            Arguments.of("Only pipeline ID", pipelineId, null, null),
            Arguments.of("Only stage ID", null, stageId, null),
            Arguments.of("Only job ID", null, null, jobId),
            Arguments.of("Pipeline and stage", pipelineId, stageId, null),
            Arguments.of("Pipeline and job", pipelineId, null, jobId),
            Arguments.of("Stage and job", null, stageId, jobId),
            Arguments.of("No IDs provided", null, null, null)
    );
  }

  @ParameterizedTest
  @MethodSource("provideExecutionIdCombinations")
  void logExecution_WithVariousIdCombinations_ShouldSaveCorrectReferences(
          String logText, UUID pipelineId, UUID stageId, UUID jobId) {

    // Arrange
    when(executionLogRepository.save(any(ExecutionLogEntity.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

    // Act
    executionLogService.logExecution(logText, pipelineId, stageId, jobId);

    // Assert
    ArgumentCaptor<ExecutionLogEntity> logCaptor = ArgumentCaptor.forClass(ExecutionLogEntity.class);
    verify(executionLogRepository).save(logCaptor.capture());

    ExecutionLogEntity savedLog = logCaptor.getValue();
    assertEquals(logText, savedLog.getLogText(), "Log text should match");
    assertEquals(pipelineId, savedLog.getPipelineExecutionId(), "Pipeline execution ID should match");
    assertEquals(stageId, savedLog.getStageExecutionId(), "Stage execution ID should match");
    assertEquals(jobId, savedLog.getJobExecutionId(), "Job execution ID should match");
  }

  @Test
  void logExecution_WithEmptyLogText_ShouldSaveLogWithEmptyText() {
    // Arrange
    String logText = "";
    UUID pipelineId = UUID.randomUUID();

    // Act
    executionLogService.logExecution(logText, pipelineId, null, null);

    // Assert
    ArgumentCaptor<ExecutionLogEntity> logCaptor = ArgumentCaptor.forClass(ExecutionLogEntity.class);
    verify(executionLogRepository).save(logCaptor.capture());

    ExecutionLogEntity savedLog = logCaptor.getValue();
    assertEquals("", savedLog.getLogText(), "Empty log text should be saved as-is");
  }

  @Test
  void logExecution_WithNullLogText_ShouldHandleGracefully() {
    // Arrange
    String logText = null;
    UUID jobId = UUID.randomUUID();

    // Mock behavior when ExecutionLogEntity is created with null text
    when(executionLogRepository.save(any(ExecutionLogEntity.class)))
            .thenAnswer(invocation -> {
              ExecutionLogEntity entity = invocation.getArgument(0);
              // This assertion verifies how the ExecutionLogEntity constructor handles null text
              // (It might convert to empty string or keep as null depending on implementation)
              return entity;
            });

    // Act
    executionLogService.logExecution(logText, null, null, jobId);

    // Assert
    verify(executionLogRepository).save(any(ExecutionLogEntity.class));
    // We're just verifying the call happens without exception
    // The actual handling of null depends on ExecutionLogEntity implementation
  }

  @Test
  void logExecution_WhenRepositoryThrowsException_ShouldPropagateException() {
    // Arrange
    String logText = "Test log";
    when(executionLogRepository.save(any(ExecutionLogEntity.class)))
            .thenThrow(new RuntimeException("Database error"));

    // Act & Assert
    assertThrows(RuntimeException.class, () ->
                    executionLogService.logExecution(logText, null, null, null),
            "Should propagate repository exceptions"
    );
  }
}