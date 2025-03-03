package edu.neu.cs6510.sp25.t1.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import edu.neu.cs6510.sp25.t1.backend.api.dto.PipelineDTO;
import edu.neu.cs6510.sp25.t1.backend.data.entity.PipelineEntity;
import edu.neu.cs6510.sp25.t1.backend.data.repository.PipelineRepository;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


class PipelineEntityReportServiceTest {

  private PipelineRepository pipelineRepository;
  private PipelineReportService reportService;

  @BeforeEach
  void setUp() {
    pipelineRepository = mock(PipelineRepository.class);
    reportService = new PipelineReportService(pipelineRepository);
  }

  @Test
  void testGetAllPipelines() {
    PipelineEntity mockPipelineEntity = new PipelineEntity("testPipeline");  // Create a mock Pipeline
    when(pipelineRepository.findAll()).thenReturn(List.of(mockPipelineEntity));

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
