//package edu.neu.cs6510.sp25.t1.backend.service;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.util.Optional;
//
//import edu.neu.cs6510.sp25.t1.backend.dto.PipelineExecutionSummary;
//import edu.neu.cs6510.sp25.t1.backend.executor.PipelineExecutor;
//import edu.neu.cs6510.sp25.t1.backend.repository.PipelineExecutionRepository;
//import edu.neu.cs6510.sp25.t1.backend.repository.PipelineRepository;
//import edu.neu.cs6510.sp25.t1.common.runtime.ExecutionState;
//import edu.neu.cs6510.sp25.t1.common.runtime.PipelineRunState;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertFalse;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.mockito.Mockito.any;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.verifyNoInteractions;
//import static org.mockito.Mockito.when;
//
//class PipelineRunStateServiceTest {
//
//  private PipelineRepository pipelineRepository;
//  private PipelineExecutionRepository pipelineExecutionRepository;
//  private PipelineExecutor pipelineExecutor;
//  private PipelineExecutionService pipelineExecutionService;
//
//  @BeforeEach
//  void setUp() {
//    pipelineRepository = mock(PipelineRepository.class);
//    pipelineExecutionRepository = mock(PipelineExecutionRepository.class);
//    pipelineExecutor = mock(PipelineExecutor.class);
//
//    pipelineExecutionService = new PipelineExecutionService(
//            pipelineRepository,
//            pipelineExecutionRepository,
//            pipelineExecutor
//    );
//  }
//
//  @Test
//  void testStartPipeline_Success() {
//    String pipelineName = "test-pipeline";
//    PipelineRunState execution = new PipelineRunState(pipelineName);
//    execution.setState(ExecutionState.PENDING);
//
//    when(pipelineRepository.findById(pipelineName)).thenReturn(Optional.of(mock(edu.neu.cs6510.sp25.t1.backend.entity.Pipeline.class)));
//    when(pipelineExecutionRepository.save(any(PipelineRunState.class))).thenReturn(execution);
//
//    Optional<PipelineExecutionSummary> result = pipelineExecutionService.startPipeline(pipelineName);
//
//    assertTrue(result.isPresent());
//    assertEquals(pipelineName, result.get().getPipelineName());
//    verify(pipelineExecutor).executePipeline(any(PipelineRunState.class));
//  }
//
//  @Test
//  void testStartPipeline_PipelineNotFound() {
//    String pipelineName = "non-existent";
//    when(pipelineRepository.findById(pipelineName)).thenReturn(Optional.empty());
//
//    Optional<PipelineExecutionSummary> result = pipelineExecutionService.startPipeline(pipelineName);
//
//    assertFalse(result.isPresent());
//    verifyNoInteractions(pipelineExecutionRepository);
//    verifyNoInteractions(pipelineExecutor);
//  }
//
//  @Test
//  void testGetPipelineExecution_Found() {
//    String pipelineName = "test-pipeline";
//    PipelineRunState execution = new PipelineRunState(pipelineName);
//    execution.setState(ExecutionState.RUNNING);
//
//    when(pipelineExecutionRepository.findFirstByPipelineNameOrderByStartTimeDesc(pipelineName))
//            .thenReturn(Optional.of(execution));
//
//    Optional<PipelineExecutionSummary> result = pipelineExecutionService.getPipelineExecution(pipelineName);
//
//    assertTrue(result.isPresent());
//    assertEquals(ExecutionState.RUNNING.name(), result.get().getStatus());
//  }
//
//  @Test
//  void testGetPipelineExecution_NotFound() {
//    String pipelineName = "non-existent";
//    when(pipelineExecutionRepository.findFirstByPipelineNameOrderByStartTimeDesc(pipelineName))
//            .thenReturn(Optional.empty());
//
//    Optional<PipelineExecutionSummary> result = pipelineExecutionService.getPipelineExecution(pipelineName);
//
//    assertFalse(result.isPresent());
//  }
//}
