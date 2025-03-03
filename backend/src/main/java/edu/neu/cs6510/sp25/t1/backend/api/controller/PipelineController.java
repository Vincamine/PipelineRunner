package edu.neu.cs6510.sp25.t1.backend.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import edu.neu.cs6510.sp25.t1.backend.data.dto.PipelineDTO;
import edu.neu.cs6510.sp25.t1.backend.data.entity.PipelineExecutionEntity;
import edu.neu.cs6510.sp25.t1.backend.service.PipelineExecutionService;
import edu.neu.cs6510.sp25.t1.backend.service.PipelineService;

/**
 * Controller for managing CI/CD pipeline executions.
 */
@RestController
@RequestMapping("/api/v1/pipelines")
public class PipelineController {
  private final PipelineService pipelineService;
  private final PipelineExecutionService pipelineExecutionService;

  public PipelineController(PipelineService pipelineService, PipelineExecutionService pipelineExecutionService) {
    this.pipelineService = pipelineService;
    this.pipelineExecutionService = pipelineExecutionService;
  }

  @GetMapping
  public ResponseEntity<List<PipelineDTO>> getAllPipelines() {
    List<PipelineDTO> pipelines = pipelineService.getAllPipelines();
    return ResponseEntity.ok(pipelines);
  }

  @GetMapping("/{pipelineName}/executions")
  public ResponseEntity<List<PipelineExecutionEntity>> getPipelineExecutions(@PathVariable String pipelineName) {
    List<PipelineExecutionEntity> executions = pipelineExecutionService.getPipelineExecutions(pipelineName);
    return ResponseEntity.ok(executions);
  }

  @GetMapping("/{pipelineName}/executions/{runId}")
  public ResponseEntity<PipelineExecutionEntity> getPipelineExecution(
          @PathVariable String pipelineName,
          @PathVariable String runId) {
    PipelineExecutionEntity execution = pipelineExecutionService.getPipelineExecution(pipelineName, runId);
    return ResponseEntity.ok(execution);
  }

  @GetMapping("/{pipelineName}/executions/latest")
  public ResponseEntity<PipelineExecutionEntity> getLatestPipelineExecution(@PathVariable String pipelineName) {
    PipelineExecutionEntity latestExecution = pipelineExecutionService.getLatestPipelineExecution(pipelineName);
    return ResponseEntity.ok(latestExecution);
  }
}
