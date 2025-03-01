package edu.neu.cs6510.sp25.t1.common.model.execution;

import org.junit.jupiter.api.Test;

import java.util.List;

import edu.neu.cs6510.sp25.t1.common.model.ExecutionState;
import edu.neu.cs6510.sp25.t1.common.model.definition.StageDefinition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


class StageExecutionTest {

  @Test
  void testStageExecutionInitialization() {
    List<JobExecution> jobs = List.of(new JobExecution("test-job", "PENDING"));
    StageDefinition stageDef = new StageDefinition("build-stage", List.of());

    StageExecution stageExec = new StageExecution(stageDef, jobs);

    assertEquals("build-stage", stageExec.getStageName());
    assertEquals(ExecutionState.PENDING, stageExec.getStageStatus());
    assertNotNull(stageExec.getStartTime());
    assertNull(stageExec.getCompletionTime());
    assertEquals(1, stageExec.getJobExecutions().size());
  }

  @Test
  void testStageExecutionUpdateStatusToSuccess() {
    List<JobExecution> jobs = List.of(new JobExecution("test-job", "SUCCESS"));
    StageDefinition stageDef = new StageDefinition("build-stage", List.of());

    StageExecution stageExec = new StageExecution(stageDef, jobs);
    stageExec.updateStatus();

    assertEquals(ExecutionState.SUCCESS, stageExec.getStageStatus());
  }

  @Test
  void testStageExecutionUpdateStatusToFailed() {
    List<JobExecution> jobs = List.of(new JobExecution("test-job", "FAILED", true));
    StageDefinition stageDef = new StageDefinition("build-stage", List.of());

    StageExecution stageExec = new StageExecution(stageDef, jobs);
    stageExec.updateStatus();

    assertEquals(ExecutionState.FAILED, stageExec.getStageStatus());
  }

  @Test
  void testStageExecutionUpdateStatusToCanceled() {
    List<JobExecution> jobs = List.of(new JobExecution("test-job", "CANCELED"));
    StageDefinition stageDef = new StageDefinition("build-stage", List.of());

    StageExecution stageExec = new StageExecution(stageDef, jobs);
    stageExec.updateStatus();

    assertEquals(ExecutionState.CANCELED, stageExec.getStageStatus());
  }

  @Test
  void testStageExecutionUpdateStatusWithMixedStates() {
    List<JobExecution> jobs = List.of(
            new JobExecution("job1", "RUNNING"),
            new JobExecution("job2", "PENDING")
    );
    StageDefinition stageDef = new StageDefinition("build-stage", List.of());

    StageExecution stageExec = new StageExecution(stageDef, jobs);
    stageExec.updateStatus();

    assertEquals(ExecutionState.RUNNING, stageExec.getStageStatus());
  }

  @Test
  void testStageExecutionWithAllowFailure() {
    List<JobExecution> jobs = List.of(
            new JobExecution("job1", "FAILED", true) // Allowed to fail
    );
    StageDefinition stageDef = new StageDefinition("build-stage", List.of());

    StageExecution stageExec = new StageExecution(stageDef, jobs);
    stageExec.updateStatus();

    assertEquals(ExecutionState.FAILED, stageExec.getStageStatus()); // Should still fail
  }

  @Test
  void testStageExecutionComplete() {
    List<JobExecution> jobs = List.of(new JobExecution("test-job", "PENDING"));
    StageDefinition stageDef = new StageDefinition("build-stage", List.of());

    StageExecution stageExec = new StageExecution(stageDef, jobs);
    assertNull(stageExec.getCompletionTime());

    stageExec.complete();
    assertNotNull(stageExec.getCompletionTime());
    assertTrue(stageExec.getCompletionTime().isAfter(stageExec.getStartTime()));
  }
}
