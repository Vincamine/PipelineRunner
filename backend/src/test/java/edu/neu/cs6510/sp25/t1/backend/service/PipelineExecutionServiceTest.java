package edu.neu.cs6510.sp25.t1.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import edu.neu.cs6510.sp25.t1.backend.dto.PipelineDTO;
import edu.neu.cs6510.sp25.t1.backend.model.Pipeline;
import edu.neu.cs6510.sp25.t1.backend.repository.PipelineRepository;

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
  }

  @Test
  void testStartPipeline_NotFound() {
    when(pipelineRepository.findById("testPipeline")).thenReturn(Optional.empty());

    Optional<PipelineDTO> result = pipelineExecutionService.startPipeline("testPipeline");

    assertTrue(result.isEmpty());
  }
}
