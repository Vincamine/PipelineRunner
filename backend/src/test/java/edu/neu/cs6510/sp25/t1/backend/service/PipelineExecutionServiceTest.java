package edu.neu.cs6510.sp25.t1.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import edu.neu.cs6510.sp25.t1.backend.dto.PipelineDTO;
import edu.neu.cs6510.sp25.t1.backend.model.Pipeline;
import edu.neu.cs6510.sp25.t1.backend.repository.PipelineRepository;
import edu.neu.cs6510.sp25.t1.common.model.ExecutionState;
import edu.neu.cs6510.sp25.t1.common.model.execution.PipelineExecution;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


class PipelineExecutionServiceTest {

  private PipelineRepository pipelineRepository;
  private PipelineExecutionService pipelineExecutionService;

  @BeforeEach
  void setUp() {
    pipelineRepository = mock(PipelineRepository.class);
    pipelineExecutionService = new PipelineExecutionService(pipelineRepository);
  }

  @Test
  void testStartPipeline_Success() {
    Pipeline mockPipeline = new Pipeline("testPipeline");  // Create a mock Pipeline
    when(pipelineRepository.findById(anyString())).thenReturn(Optional.of(mockPipeline));

    Optional<PipelineDTO> result = pipelineExecutionService.startPipeline("testPipeline");

    assertTrue(result.isPresent());
    assertEquals("testPipeline", result.get().getName());
  }

  @Test
  void testStartPipeline_NotFound() {
    when(pipelineRepository.findById("testPipeline")).thenReturn(Optional.empty());

    Optional<PipelineDTO> result = pipelineExecutionService.startPipeline("testPipeline");

    assertTrue(result.isEmpty());
  }

  @Test
  void testGetPipelineExecution_NotFound() {
    Optional<PipelineDTO> result = pipelineExecutionService.getPipelineExecution("nonExistentPipeline");

    assertTrue(result.isEmpty());
  }

  @Test
  void testGetPipelineExecution_Found() {
    PipelineExecution mockExecution = new PipelineExecution("testPipeline", List.of(), List.of());

    // Explicitly store mock execution
    pipelineExecutionService.getExecutionStore().put("testPipeline", mockExecution);

    pipelineExecutionService.updatePipelineStatus("testPipeline", ExecutionState.RUNNING); // Ensure it exists

    Optional<PipelineDTO> result = pipelineExecutionService.getPipelineExecution("testPipeline");

    assertTrue(result.isPresent());
    assertEquals("testPipeline", result.get().getName());
  }

  @Test
  void testUpdatePipelineStatus() {
    PipelineExecution mockExecution = new PipelineExecution("testPipeline", List.of(), List.of());

    // Explicitly store mock execution
    pipelineExecutionService.getExecutionStore().put("testPipeline", mockExecution);

    pipelineExecutionService.updatePipelineStatus("testPipeline", ExecutionState.SUCCESS);

    Optional<PipelineDTO> result = pipelineExecutionService.getPipelineExecution("testPipeline");

    assertTrue(result.isPresent());
  }


  @Test
  void testUpdatePipelineStatus_NotFound() {
    // Updating a non-existing pipeline should not cause an exception
    assertDoesNotThrow(() -> pipelineExecutionService.updatePipelineStatus("nonExistentPipeline", ExecutionState.FAILED));
  }
}
