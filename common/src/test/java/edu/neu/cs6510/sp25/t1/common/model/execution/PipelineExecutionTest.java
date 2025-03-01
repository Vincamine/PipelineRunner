package edu.neu.cs6510.sp25.t1.common.model.execution;

import org.junit.jupiter.api.Test;

import java.util.List;

import edu.neu.cs6510.sp25.t1.common.model.ExecutionState;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PipelineExecutionTest {

  @Test
  void testPipelineExecutionInitialization() {
    PipelineExecution pipelineExec = new PipelineExecution("test-pipeline", List.of(), List.of());

    assertEquals("test-pipeline", pipelineExec.getPipelineName());
    assertEquals(ExecutionState.PENDING, pipelineExec.getState()); // ✅ Ensures initial state is PENDING
  }

  @Test
  void testPipelineExecutionUpdateState_Success() {
    List<JobExecution> jobs = List.of(
            new JobExecution("job1", "SUCCESS"),
            new JobExecution("job2", "SUCCESS")
    );
    PipelineExecution pipelineExec = new PipelineExecution("test-pipeline", List.of(), jobs);

    pipelineExec.updateState();
    assertEquals(ExecutionState.SUCCESS, pipelineExec.getState()); // ✅ Ensures pipeline updates to SUCCESS
  }

  @Test
  void testPipelineExecutionFailureCase() {
    List<JobExecution> jobs = List.of(
            new JobExecution("job1", "FAILED")
    );
    PipelineExecution pipelineExec = new PipelineExecution("test-pipeline", List.of(), jobs);

    pipelineExec.updateState();
    assertEquals(ExecutionState.FAILED, pipelineExec.getState()); // ✅ Ensures pipeline updates to FAILED
  }

  @Test
  void testPipelineExecutionRunningState() {
    List<JobExecution> jobs = List.of(
            new JobExecution("job1", "RUNNING"),
            new JobExecution("job2", "PENDING")
    );
    PipelineExecution pipelineExec = new PipelineExecution("test-pipeline", List.of(), jobs);

    pipelineExec.updateState();
    assertEquals(ExecutionState.RUNNING, pipelineExec.getState()); // ✅ Ensures pipeline updates to RUNNING
  }

  @Test
  void testPipelineExecutionCanceledState() {
    List<JobExecution> jobs = List.of(
            new JobExecution("job1", "CANCELED"),
            new JobExecution("job2", "SUCCESS")
    );
    PipelineExecution pipelineExec = new PipelineExecution("test-pipeline", List.of(), jobs);

    pipelineExec.updateState();
    assertEquals(ExecutionState.CANCELED, pipelineExec.getState()); // ✅ Ensures pipeline updates to CANCELED
  }

  @Test
  void testPipelineExecutionPendingState() {
    List<JobExecution> jobs = List.of(
            new JobExecution("job1", "PENDING"),
            new JobExecution("job2", "PENDING")
    );
    PipelineExecution pipelineExec = new PipelineExecution("test-pipeline", List.of(), jobs);

    pipelineExec.updateState();
    assertEquals(ExecutionState.PENDING, pipelineExec.getState()); // ✅ Ensures pipeline updates to PENDING
  }
}
