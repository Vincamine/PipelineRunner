package edu.neu.cs6510.sp25.t1.backend.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import edu.neu.cs6510.sp25.t1.backend.data.entity.PipelineExecutionEntity;
import edu.neu.cs6510.sp25.t1.backend.service.PipelineService;


/**
 * Controller for managing CI/CD pipeline executions.
 */
@RestController
@RequestMapping("/api/v1/pipelines")
public class PipelineController {
  private final PipelineService pipelineService;

  public PipelineController(PipelineService pipelineService) {
    this.pipelineService = pipelineService;
  }

  /**
   * Retrieves all pipelines with execution history.
   *
   * @return A list of all pipelines.
   */
  @GetMapping
  public ResponseEntity<List<String>> getAllPipelines() {
    List<String> pipelines = pipelineService.getAllPipelines();
    return ResponseEntity.ok(pipelines);
  }

  /**
   * Retrieves all executions for a specific pipeline.
   *
   * @param pipelineName The name of the pipeline.
   * @return A list of pipeline execution summaries.
   */
  @GetMapping("/{pipelineName}/executions")
  public ResponseEntity<List<PipelineExecutionEntity>> getPipelineExecutions(@PathVariable String pipelineName) {
    List<PipelineExecutionEntity> executions = pipelineService.getPipelineExecutions(pipelineName);
    return ResponseEntity.ok(executions);
  }

  /**
   * Retrieves details of a specific pipeline execution.
   *
   * @param pipelineName The name of the pipeline.
   * @param runId The execution ID.
   * @return The pipeline execution details.
   */
  @GetMapping("/{pipelineName}/executions/{runId}")
  public ResponseEntity<PipelineExecutionEntity> getPipelineExecution(
          @PathVariable String pipelineName,
          @PathVariable Long runId) {
    PipelineExecutionEntity execution = pipelineService.getPipelineExecution(pipelineName, runId);
    return ResponseEntity.ok(execution);
  }

  /**
   * Retrieves the latest execution of a pipeline.
   *
   * @param pipelineName The pipeline name.
   * @return The latest pipeline execution.
   */
  @GetMapping("/{pipelineName}/executions/latest")
  public ResponseEntity<PipelineExecutionEntity> getLatestPipelineExecution(@PathVariable String pipelineName) {
    PipelineExecutionEntity latestExecution = pipelineService.getLatestPipelineExecution(pipelineName);
    return ResponseEntity.ok(latestExecution);
  }
}
