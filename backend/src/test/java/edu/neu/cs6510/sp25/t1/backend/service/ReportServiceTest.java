package edu.neu.cs6510.sp25.t1.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import edu.neu.cs6510.sp25.t1.backend.dto.PipelineDTO;
import edu.neu.cs6510.sp25.t1.backend.entity.Pipeline;
import edu.neu.cs6510.sp25.t1.backend.repository.PipelineRepository;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


class ReportServiceTest {

  private PipelineRepository pipelineRepository;
  private ReportService reportService;

  @BeforeEach
  void setUp() {
    pipelineRepository = mock(PipelineRepository.class);
    reportService = new ReportService(pipelineRepository);
  }

  @Test
  void testGetAllPipelines() {
    Pipeline mockPipeline = new Pipeline("testPipeline");  // Create a mock Pipeline
    when(pipelineRepository.findAll()).thenReturn(List.of(mockPipeline));

    List<PipelineDTO> result = reportService.getAllPipelines();

    assertFalse(result.isEmpty());
  }

  @Test
  void testGetPipelineExecutions_NoExecutions() {
    List<PipelineDTO> result = reportService.getPipelineExecutions("testPipeline");

    assertTrue(result.isEmpty());
  }

  @Test
  void testGetLatestPipelineRun_NotFound() {
    PipelineDTO result = reportService.getLatestPipelineRun("testPipeline");

    assertNull(result);
  }
}
