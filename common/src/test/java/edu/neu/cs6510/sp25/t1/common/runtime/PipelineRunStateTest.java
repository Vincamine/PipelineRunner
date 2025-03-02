package edu.neu.cs6510.sp25.t1.common.runtime;

import org.junit.jupiter.api.Test;

import java.util.List;

import edu.neu.cs6510.sp25.t1.common.execution.ExecutionState;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PipelineRunStateTest {

  @Test
  void testPipelineExecutionInitialization() {
    PipelineRunState pipelineExec = new PipelineRunState("test-pipeline", List.of(), List.of());

    assertEquals("test-pipeline", pipelineExec.getPipelineName());
    assertEquals(ExecutionState.PENDING, pipelineExec.getState());
    assertNotNull(pipelineExec.getStartTime());
    assertNotNull(pipelineExec.getLastUpdated());
  }

  @Test
  void testPipelineExecutionSetState() {
    PipelineRunState pipelineExec = new PipelineRunState("test-pipeline");

    pipelineExec.setState(ExecutionState.RUNNING);
    assertEquals(ExecutionState.RUNNING, pipelineExec.getState());

    pipelineExec.setState(ExecutionState.SUCCESS);
    assertEquals(ExecutionState.SUCCESS, pipelineExec.getState());
  }

  @Test
  void testPipelineExecutionUpdateState_Success() {
    List<JobRunState> jobs = List.of(
            new JobRunState("job1", "SUCCESS"),
            new JobRunState("job2", "SUCCESS")
    );
    PipelineRunState pipelineExec = new PipelineRunState("test-pipeline", List.of(), jobs);

    pipelineExec.updateState();
    assertEquals(ExecutionState.SUCCESS, pipelineExec.getState());
  }

  @Test
  void testPipelineExecutionUpdateState_Failure() {
    List<JobRunState> jobs = List.of(
            new JobRunState("job1", "FAILED")
    );
    PipelineRunState pipelineExec = new PipelineRunState("test-pipeline", List.of(), jobs);

    pipelineExec.updateState();
    assertEquals(ExecutionState.FAILED, pipelineExec.getState());
  }

  @Test
  void testPipelineExecutionUpdateState_Running() {
    List<JobRunState> jobs = List.of(
            new JobRunState("job1", "RUNNING"),
            new JobRunState("job2", "PENDING")
    );
    PipelineRunState pipelineExec = new PipelineRunState("test-pipeline", List.of(), jobs);

    pipelineExec.updateState();
    assertEquals(ExecutionState.RUNNING, pipelineExec.getState());
  }

  @Test
  void testPipelineExecutionUpdateState_Canceled() {
    List<JobRunState> jobs = List.of(
            new JobRunState("job1", "CANCELED"),
            new JobRunState("job2", "SUCCESS")
    );
    PipelineRunState pipelineExec = new PipelineRunState("test-pipeline", List.of(), jobs);

    pipelineExec.updateState();
    assertEquals(ExecutionState.CANCELED, pipelineExec.getState());
  }

  @Test
  void testPipelineExecutionUpdateState_Pending() {
    List<JobRunState> jobs = List.of(
            new JobRunState("job1", "PENDING"),
            new JobRunState("job2", "PENDING")
    );
    PipelineRunState pipelineExec = new PipelineRunState("test-pipeline", List.of(), jobs);

    pipelineExec.updateState();
    assertEquals(ExecutionState.PENDING, pipelineExec.getState());
  }

  @Test
  void testPipelineExecutionUpdateState_EmptyJobs() {
    PipelineRunState pipelineExec = new PipelineRunState("test-pipeline", List.of(), List.of());

    pipelineExec.updateState();
    assertEquals(ExecutionState.PENDING, pipelineExec.getState()); // Ensure no jobs = PENDING
  }

  @Test
  void testGetters() {
    PipelineRunState pipelineExec = new PipelineRunState("test-pipeline");

    assertEquals("test-pipeline", pipelineExec.getPipelineName());
    assertNotNull(pipelineExec.getStartTime());
    assertNotNull(pipelineExec.getLastUpdated());
  }

  @Test
  void testUpdateStateHandlesNullJobs() {
    PipelineRunState pipelineExec = new PipelineRunState("test-pipeline", List.of(), null);

    assertDoesNotThrow(pipelineExec::updateState);
    assertEquals(ExecutionState.PENDING, pipelineExec.getState()); // If jobs are null, it should remain PENDING
  }
}
