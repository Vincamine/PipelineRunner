package edu.neu.cs6510.sp25.t1.backend.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import edu.neu.cs6510.sp25.t1.backend.api.client.WorkerClient;
import edu.neu.cs6510.sp25.t1.backend.api.request.RunPipelineRequest;
import edu.neu.cs6510.sp25.t1.backend.database.dto.PipelineDTO;
import edu.neu.cs6510.sp25.t1.backend.database.dto.PipelineExecutionDTO;
import edu.neu.cs6510.sp25.t1.backend.database.entity.PipelineExecutionEntity;
import edu.neu.cs6510.sp25.t1.backend.service.PipelineExecutionService;
import edu.neu.cs6510.sp25.t1.backend.service.PipelineService;

/**
 * Controller for managing CI/CD pipeline executions.
 */
@RestController
@RequestMapping("/api/v1/pipelines")
public class PipelineController {
  private static final Logger logger = LoggerFactory.getLogger(PipelineController.class);
  private final PipelineService pipelineService;
  private final PipelineExecutionService pipelineExecutionService;

  public PipelineController(PipelineService pipelineService,
                            PipelineExecutionService pipelineExecutionService,
                            WorkerClient workerClient) {
    this.pipelineService = pipelineService;
    this.pipelineExecutionService = pipelineExecutionService;
  }

  /**
   * Fetch all registered pipelines.
   *
   * @return List of pipelines.
   */
  @GetMapping
  public ResponseEntity<List<PipelineDTO>> getAllPipelines() {
    List<PipelineDTO> pipelines = pipelineService.getAllPipelines();
    return ResponseEntity.ok(pipelines);
  }

  /**
   * Fetch executions for a specific pipeline.
   *
   * @param pipelineName Name of the pipeline.
   * @return List of pipeline executions.
   */
  @GetMapping("/{pipelineName}/executions")
  public ResponseEntity<List<PipelineExecutionEntity>> getPipelineExecutions(@PathVariable String pipelineName) {
    List<PipelineExecutionEntity> executions = pipelineExecutionService.getPipelineExecutions(pipelineName);
    return ResponseEntity.ok(executions);
  }

  /**
   * Fetch a specific pipeline execution by run ID.
   *
   * @param pipelineName Name of the pipeline.
   * @param runId        Run ID.
   * @return Pipeline execution entity.
   */
  @GetMapping("/{pipelineName}/executions/{runId}")
  public ResponseEntity<PipelineExecutionEntity> getPipelineExecution(
          @PathVariable String pipelineName,
          @PathVariable String runId) {
    PipelineExecutionEntity execution = pipelineExecutionService.getPipelineExecution(pipelineName, runId);
    return ResponseEntity.ok(execution);
  }

  /**
   * Fetch the latest execution of a pipeline.
   *
   * @param pipelineName Name of the pipeline.
   * @return Latest pipeline execution entity.
   */
  @GetMapping("/{pipelineName}/executions/latest")
  public ResponseEntity<PipelineExecutionEntity> getLatestPipelineExecution(@PathVariable String pipelineName) {
    PipelineExecutionEntity latestExecution = pipelineExecutionService.getLatestPipelineExecution(pipelineName);
    return ResponseEntity.ok(latestExecution);
  }

  /**
   * Start a new pipeline execution.
   *
   * @param request Request containing pipeline execution details.
   * @return Response indicating pipeline execution started.
   */
  @PostMapping("/execute")
  public ResponseEntity<String> executePipeline(@RequestBody RunPipelineRequest request) {
    logger.info("Starting pipeline execution: {}", request.getPipeline());

    // Ensure commit hash is passed correctly
    String commitHash = (request.getCommit() != null) ? request.getCommit() : "default-commit";

    PipelineExecutionDTO pipelineExecution = pipelineExecutionService.startPipelineExecution(
            request.getPipeline(), commitHash
    );

    return ResponseEntity.ok("Pipeline execution started successfully.");
  }
}
