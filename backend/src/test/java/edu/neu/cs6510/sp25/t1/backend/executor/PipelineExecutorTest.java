//package edu.neu.cs6510.sp25.t1.backend.executor;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.ArgumentCaptor;
//
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//
//import edu.neu.cs6510.sp25.t1.backend.data.repository.PipelineExecutionRepository;
//import edu.neu.cs6510.sp25.t1.common.runtime.ExecutionState;
//import edu.neu.cs6510.sp25.t1.common.runtime.PipelineRunState;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.Mockito.any;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.never;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//class PipelineExecutorTest {
//
//  private PipelineExecutionRepository pipelineExecutionRepository;
//  private PipelineExecutor pipelineExecutor;
//
//  @BeforeEach
//  void setUp() {
//    pipelineExecutionRepository = mock(PipelineExecutionRepository.class);
//    pipelineExecutor = new PipelineExecutor(pipelineExecutionRepository);
//  }
//
//  @Test
//  void testExecutePipeline_Success() {
//    // Given: Create a new pipeline execution
//    PipelineRunState execution = new PipelineRunState("test-pipeline");
//
//    // Mock repository behavior to return the execution object when saving
//    when(pipelineExecutionRepository.save(any(PipelineRunState.class))).thenAnswer(invocation -> {
//      PipelineRunState savedExecution = invocation.getArgument(0);
//      return new PipelineRunState(savedExecution.getPipelineName()); // Simulate repository persistence
//    });
//
//    // When: Execute the pipeline
//    pipelineExecutor.executePipeline(execution);
//
//    // Capture execution state changes
//    ArgumentCaptor<PipelineRunState> captor = ArgumentCaptor.forClass(PipelineRunState.class);
//    verify(pipelineExecutionRepository, times(2)).save(captor.capture());
//
//    // Extract captured execution states
//    List<PipelineRunState> capturedExecutions = captor.getAllValues();
//
//    // Then: Verify state transitions
//    assertEquals(ExecutionState.SUCCESS, capturedExecutions.get(1).getState(), "Pipeline should be successful after execution");
//  }
//
//
//
//  @Test
//  void testUpdatePipelineExecutionState_ExistingExecution() {
//    UUID executionId = UUID.randomUUID();
//    PipelineRunState execution = new PipelineRunState("test-pipeline");
//    execution.setState(ExecutionState.RUNNING);
//
//    when(pipelineExecutionRepository.findById(executionId)).thenReturn(Optional.of(execution));
//    when(pipelineExecutionRepository.save(any(PipelineRunState.class))).thenReturn(execution);
//
//    pipelineExecutor.updatePipelineExecutionState(executionId, ExecutionState.FAILED);
//
//    verify(pipelineExecutionRepository).save(execution);
//    assertEquals(ExecutionState.FAILED, execution.getState(), "Pipeline execution state should be updated to FAILED");
//  }
//
//  @Test
//  void testUpdatePipelineExecutionState_NonExistingExecution() {
//    UUID executionId = UUID.randomUUID();
//    when(pipelineExecutionRepository.findById(executionId)).thenReturn(Optional.empty());
//
//    pipelineExecutor.updatePipelineExecutionState(executionId, ExecutionState.FAILED);
//
//    verify(pipelineExecutionRepository, never()).save(any(PipelineRunState.class));
//  }
//}
