package edu.neu.cs6510.sp25.t1.common.runtime;

import org.junit.jupiter.api.Test;

import java.util.List;

import edu.neu.cs6510.sp25.t1.common.execution.ExecutionState;
import edu.neu.cs6510.sp25.t1.common.config.StageConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


class StageRunStateTest {

  @Test
  void testStageExecutionInitialization() {
    List<JobRunState> jobs = List.of(new JobRunState("test-job", "PENDING"));
    StageConfig stageDef = new StageConfig("build-stage", List.of());

    StageRunState stageExec = new StageRunState(stageDef, jobs);

    assertEquals("build-stage", stageExec.getStageName());
    assertEquals(ExecutionState.PENDING, stageExec.getStageStatus());
    assertNotNull(stageExec.getStartTime());
    assertNull(stageExec.getCompletionTime());
    assertEquals(1, stageExec.getJobExecutions().size());
  }

  @Test
  void testStageExecutionUpdateStatusToSuccess() {
    List<JobRunState> jobs = List.of(new JobRunState("test-job", "SUCCESS"));
    StageConfig stageDef = new StageConfig("build-stage", List.of());

    StageRunState stageExec = new StageRunState(stageDef, jobs);
    stageExec.updateStatus();

    assertEquals(ExecutionState.SUCCESS, stageExec.getStageStatus());
  }

  @Test
  void testStageExecutionUpdateStatusToFailed() {
    List<JobRunState> jobs = List.of(new JobRunState("test-job", "FAILED", true));
    StageConfig stageDef = new StageConfig("build-stage", List.of());

    StageRunState stageExec = new StageRunState(stageDef, jobs);
    stageExec.updateStatus();

    assertEquals(ExecutionState.FAILED, stageExec.getStageStatus());
  }

  @Test
  void testStageExecutionUpdateStatusToCanceled() {
    List<JobRunState> jobs = List.of(new JobRunState("test-job", "CANCELED"));
    StageConfig stageDef = new StageConfig("build-stage", List.of());

    StageRunState stageExec = new StageRunState(stageDef, jobs);
    stageExec.updateStatus();

    assertEquals(ExecutionState.CANCELED, stageExec.getStageStatus());
  }

  @Test
  void testStageExecutionUpdateStatusWithMixedStates() {
    List<JobRunState> jobs = List.of(
            new JobRunState("job1", "RUNNING"),
            new JobRunState("job2", "PENDING")
    );
    StageConfig stageDef = new StageConfig("build-stage", List.of());

    StageRunState stageExec = new StageRunState(stageDef, jobs);
    stageExec.updateStatus();

    assertEquals(ExecutionState.RUNNING, stageExec.getStageStatus());
  }

  @Test
  void testStageExecutionWithAllowFailure() {
    List<JobRunState> jobs = List.of(
            new JobRunState("job1", "FAILED", true) // Allowed to fail
    );
    StageConfig stageDef = new StageConfig("build-stage", List.of());

    StageRunState stageExec = new StageRunState(stageDef, jobs);
    stageExec.updateStatus();

    assertEquals(ExecutionState.FAILED, stageExec.getStageStatus()); // Should still fail
  }

  @Test
  void testStageExecutionComplete() {
    List<JobRunState> jobs = List.of(new JobRunState("test-job", "PENDING"));
    StageConfig stageDef = new StageConfig("build-stage", List.of());

    StageRunState stageExec = new StageRunState(stageDef, jobs);
    assertNull(stageExec.getCompletionTime());

    stageExec.complete();
    assertNotNull(stageExec.getCompletionTime());
    assertTrue(stageExec.getCompletionTime().isAfter(stageExec.getStartTime()));
  }
}
