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
    assertEquals(ExecutionState.PENDING, pipelineExec.getState());
  }

  @Test
  void testPipelineExecutionUpdateState() {
    List<JobExecution> jobs = List.of(new JobExecution("job1", "SUCCESS"), new JobExecution("job2", "SUCCESS"));
    PipelineExecution pipelineExec = new PipelineExecution("test-pipeline", List.of(), jobs);

    pipelineExec.updateState();
    assertEquals(ExecutionState.SUCCESS, pipelineExec.getState());
  }

  @Test
  void testPipelineExecutionFailureCase() {
    List<JobExecution> jobs = List.of(new JobExecution("job1", "FAILED"));
    PipelineExecution pipelineExec = new PipelineExecution("test-pipeline", List.of(), jobs);

    pipelineExec.updateState();
    assertEquals(ExecutionState.FAILED, pipelineExec.getState());
  }
}
