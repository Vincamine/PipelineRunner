package edu.neu.cs6510.sp25.t1.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import edu.neu.cs6510.sp25.t1.backend.database.entity.ExecutionLogEntity;
import edu.neu.cs6510.sp25.t1.backend.database.repository.ExecutionLogRepository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ExecutionLogServiceTest {

  @Mock
  private ExecutionLogRepository executionLogRepository;

  private ExecutionLogService executionLogService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    executionLogService = new ExecutionLogService(executionLogRepository);
  }

  @Test
  void testLogExecution_WithAllParameters() {
    // Arrange
    String logText = "Test log message";
    UUID pipelineExecutionId = UUID.randomUUID();
    UUID stageExecutionId = UUID.randomUUID();
    UUID jobExecutionId = UUID.randomUUID();

    when(executionLogRepository.save(any(ExecutionLogEntity.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

    // Act
    executionLogService.logExecution(logText, pipelineExecutionId, stageExecutionId, jobExecutionId);

    // Assert
    verify(executionLogRepository, times(1)).save(any(ExecutionLogEntity.class));
  }

  @Test
  void testLogExecution_WithNullStageAndJobIds() {
    // Arrange
    String logText = "Pipeline-level log";
    UUID pipelineExecutionId = UUID.randomUUID();
    UUID stageExecutionId = null;
    UUID jobExecutionId = null;

    when(executionLogRepository.save(any(ExecutionLogEntity.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

    // Act
    executionLogService.logExecution(logText, pipelineExecutionId, stageExecutionId, jobExecutionId);

    // Assert
    verify(executionLogRepository, times(1)).save(any(ExecutionLogEntity.class));
  }

  @Test
  void testLogExecution_WithNullPipelineAndJobIds() {
    // Arrange
    String logText = "Stage-level log";
    UUID pipelineExecutionId = null;
    UUID stageExecutionId = UUID.randomUUID();
    UUID jobExecutionId = null;

    when(executionLogRepository.save(any(ExecutionLogEntity.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

    // Act
    executionLogService.logExecution(logText, pipelineExecutionId, stageExecutionId, jobExecutionId);

    // Assert
    verify(executionLogRepository, times(1)).save(any(ExecutionLogEntity.class));
  }

  @Test
  void testLogExecution_WithNullPipelineAndStageIds() {
    // Arrange
    String logText = "Job-level log";
    UUID pipelineExecutionId = null;
    UUID stageExecutionId = null;
    UUID jobExecutionId = UUID.randomUUID();

    when(executionLogRepository.save(any(ExecutionLogEntity.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

    // Act
    executionLogService.logExecution(logText, pipelineExecutionId, stageExecutionId, jobExecutionId);

    // Assert
    verify(executionLogRepository, times(1)).save(any(ExecutionLogEntity.class));
  }

  @Test
  void testLogExecution_WithAllNullIds() {
    // Arrange
    String logText = "System-level log";
    UUID pipelineExecutionId = null;
    UUID stageExecutionId = null;
    UUID jobExecutionId = null;

    when(executionLogRepository.save(any(ExecutionLogEntity.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

    // Act
    executionLogService.logExecution(logText, pipelineExecutionId, stageExecutionId, jobExecutionId);

    // Assert
    verify(executionLogRepository, times(1)).save(any(ExecutionLogEntity.class));
  }
}