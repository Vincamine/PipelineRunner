package edu.neu.cs6510.sp25.t1.backend.api;

import edu.neu.cs6510.sp25.t1.backend.dto.PipelineDTO;
import edu.neu.cs6510.sp25.t1.backend.service.ReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ReportControllerTest {

  private ReportService reportService;
  private ReportController reportController;

  @BeforeEach
  void setUp() {
    reportService = mock(ReportService.class);
    reportController = new ReportController(reportService);
  }

  @Test
  void testGetAllPipelines() {
    when(reportService.getAllPipelines()).thenReturn(List.of(new PipelineDTO("testPipeline", null)));

    List<PipelineDTO> response = reportController.getAllPipelines();

    assertNotNull(response);
    assertEquals(1, response.size());
    assertEquals("testPipeline", response.getFirst().getName());
  }

  @Test
  void testGetPipelineExecutions() {
    when(reportService.getPipelineExecutions("testPipeline"))
            .thenReturn(List.of(new PipelineDTO("testPipeline", null)));

    List<PipelineDTO> response = reportController.getPipelineExecutions("testPipeline");

    assertNotNull(response);
    assertEquals(1, response.size());
    assertEquals("testPipeline", response.getFirst().getName());
  }

  @Test
  void testGetLatestPipelineRun() {
    PipelineDTO expectedDto = new PipelineDTO("testPipeline", null);
    when(reportService.getLatestPipelineRun("testPipeline")).thenReturn(expectedDto);

    PipelineDTO response = reportController.getLatestPipelineRun("testPipeline");

    assertNotNull(response);
    assertEquals("testPipeline", response.getName());
  }
}
