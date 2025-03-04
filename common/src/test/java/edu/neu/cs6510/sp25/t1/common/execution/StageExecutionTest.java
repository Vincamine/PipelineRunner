package edu.neu.cs6510.sp25.t1.common.execution;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;
import edu.neu.cs6510.sp25.t1.common.model.Stage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

class StageExecutionTest {

  private StageExecution stageExecution;
  private Stage mockStage;
  private List<JobExecution> jobExecutions;

  @BeforeEach
  void setUp() {
    // Mocking Stage since we can't change its constructor
    mockStage = Mockito.mock(Stage.class);
    when(mockStage.getName()).thenReturn("Build-Stage");

    // Creating job executions
    jobExecutions = List.of(
            new JobExecution("job-1", "ubuntu:latest", List.of("echo 'hello'"), List.of(), false),
            new JobExecution("job-2", "ubuntu:latest", List.of("echo 'world'"), List.of(), false)
    );

    stageExecution = new StageExecution(mockStage, jobExecutions);
  }

  @Test
  void testConstructorInitialization() {
    assertEquals("Build-Stage", stageExecution.getName());
    assertEquals(ExecutionStatus.PENDING, stageExecution.getStatus());
    assertNotNull(stageExecution.getStartTime());
    assertNull(stageExecution.getCompletionTime());
    assertEquals(2, stageExecution.getJobs().size());
  }

  @Test
  void testUpdateStatusToFailed() {
    // Set one job to FAILED
    jobExecutions.getFirst().updateState(ExecutionStatus.FAILED);
    stageExecution.updateStatus();

    assertEquals(ExecutionStatus.FAILED, stageExecution.getStatus());
  }

  @Test
  void testUpdateStatusToSuccess() {
    // Set all jobs to SUCCESS
    jobExecutions.forEach(job -> job.updateState(ExecutionStatus.SUCCESS));
    stageExecution.updateStatus();

    assertEquals(ExecutionStatus.SUCCESS, stageExecution.getStatus());
  }

  @Test
  void testUpdateStatusToCanceled() {
    // Set one job to CANCELED
    jobExecutions.get(1).updateState(ExecutionStatus.CANCELED);
    stageExecution.updateStatus();

    assertEquals(ExecutionStatus.CANCELED, stageExecution.getStatus());
  }

  @Test
  void testSetStatusManually() {
    stageExecution.setStatus(ExecutionStatus.RUNNING);
    assertEquals(ExecutionStatus.RUNNING, stageExecution.getStatus());
  }
}
