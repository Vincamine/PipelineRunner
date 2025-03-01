package edu.neu.cs6510.sp25.t1.backend.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import edu.neu.cs6510.sp25.t1.backend.dto.PipelineDTO;
import edu.neu.cs6510.sp25.t1.backend.service.PipelineExecutionService;
import edu.neu.cs6510.sp25.t1.common.api.RunPipelineRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PipelineControllerTest {

  private PipelineExecutionService pipelineService;
  private PipelineController pipelineController;

  @BeforeEach
  void setUp() {
    pipelineService = mock(PipelineExecutionService.class);
    pipelineController = new PipelineController(pipelineService);
  }

  @Test
  void testRunPipeline_Success() {
    RunPipelineRequest request = new RunPipelineRequest("testPipeline");
    PipelineDTO pipelineDTO = new PipelineDTO("testPipeline", null);
    when(pipelineService.startPipeline("testPipeline")).thenReturn(Optional.of(pipelineDTO));

    ResponseEntity<PipelineDTO> response = pipelineController.runPipeline(request);

    assertEquals(200, response.getStatusCode().value());
    assertEquals("testPipeline", response.getBody().getName());
  }

  @Test
  void testRunPipeline_NotFound() {
    RunPipelineRequest request = new RunPipelineRequest("unknownPipeline");
    when(pipelineService.startPipeline("unknownPipeline")).thenReturn(Optional.empty());

    ResponseEntity<PipelineDTO> response = pipelineController.runPipeline(request);

    assertEquals(404, response.getStatusCode().value());

  }

  @Test
  void testGetStatus_Success() {
    PipelineDTO pipelineDTO = new PipelineDTO("testPipeline", null);
    when(pipelineService.getPipelineExecution("testPipeline")).thenReturn(Optional.of(pipelineDTO));

    ResponseEntity<PipelineDTO> response = pipelineController.getStatus("testPipeline");

    assertEquals(200, response.getStatusCode().value());
    assertEquals("testPipeline", response.getBody().getName());
  }

  @Test
  void testGetStatus_NotFound() {
    when(pipelineService.getPipelineExecution("unknownPipeline")).thenReturn(Optional.empty());

    ResponseEntity<PipelineDTO> response = pipelineController.getStatus("unknownPipeline");

    assertEquals(404, response.getStatusCode().value());
  }
}
