package edu.neu.cs6510.sp25.t1.backend.service;

import edu.neu.cs6510.sp25.t1.backend.dto.PipelineDTO;
import edu.neu.cs6510.sp25.t1.backend.model.Pipeline;
import edu.neu.cs6510.sp25.t1.backend.repository.PipelineRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
