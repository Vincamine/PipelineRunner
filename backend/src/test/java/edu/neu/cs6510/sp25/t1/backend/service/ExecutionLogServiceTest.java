package edu.neu.cs6510.sp25.t1.backend.service;

import edu.neu.cs6510.sp25.t1.backend.database.entity.ExecutionLogEntity;
import edu.neu.cs6510.sp25.t1.backend.database.repository.ExecutionLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

/**
 * Unit test for ExecutionLogService.
 */
class ExecutionLogServiceTest {

  @Mock
  private ExecutionLogRepository executionLogRepository;

  @InjectMocks
  private ExecutionLogService executionLogService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void logExecution_SavesExecutionLogEntity() {
    // Given
    String logText = "Pipeline execution started.";
    UUID pipelineId = UUID.randomUUID();
    UUID stageId = UUID.randomUUID();
    UUID jobId = UUID.randomUUID();

    // When
    executionLogService.logExecution(logText, pipelineId, stageId, jobId);

    // Then
    ArgumentCaptor<ExecutionLogEntity> logCaptor = ArgumentCaptor.forClass(ExecutionLogEntity.class);
    verify(executionLogRepository).save(logCaptor.capture());

    ExecutionLogEntity capturedLog = logCaptor.getValue();
    assertEquals(logText, capturedLog.getLogText());
    assertEquals(pipelineId, capturedLog.getPipelineExecutionId());
    assertEquals(stageId, capturedLog.getStageExecutionId());
    assertEquals(jobId, capturedLog.getJobExecutionId());
  }

  @Test
  void logExecution_SavesExecutionLogEntity_WithNullReferences() {
    // Given
    String logText = "Generic log entry.";

    // When
    executionLogService.logExecution(logText, null, null, null);

    // Then
    ArgumentCaptor<ExecutionLogEntity> logCaptor = ArgumentCaptor.forClass(ExecutionLogEntity.class);
    verify(executionLogRepository).save(logCaptor.capture());

    ExecutionLogEntity capturedLog = logCaptor.getValue();
    assertEquals(logText, capturedLog.getLogText());
    assertEquals(null, capturedLog.getPipelineExecutionId());
    assertEquals(null, capturedLog.getStageExecutionId());
    assertEquals(null, capturedLog.getJobExecutionId());
  }
}
