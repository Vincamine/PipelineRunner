package edu.neu.cs6510.sp25.t1.common.model.execution;

import org.junit.jupiter.api.Test;

import java.util.List;

import edu.neu.cs6510.sp25.t1.common.model.ExecutionState;
import edu.neu.cs6510.sp25.t1.common.model.definition.StageDefinition;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StageExecutionTest {

  @Test
  void testStageExecutionInitialization() {
    List<JobExecution> jobs = List.of(new JobExecution("test-job", "PENDING"));
    StageDefinition stageDef = new StageDefinition("build-stage", List.of());

    StageExecution stageExec = new StageExecution(stageDef, jobs);

    assertEquals("build-stage", stageExec.getStageName());
    assertEquals(ExecutionState.PENDING, stageExec.getStageStatus());
  }

  @Test
  void testStageExecutionUpdateStatus() {
    List<JobExecution> jobs = List.of(new JobExecution("test-job", "SUCCESS"));
    StageDefinition stageDef = new StageDefinition("build-stage", List.of());

    StageExecution stageExec = new StageExecution(stageDef, jobs);
    stageExec.updateStatus();

    assertEquals(ExecutionState.SUCCESS, stageExec.getStageStatus());
  }
}
