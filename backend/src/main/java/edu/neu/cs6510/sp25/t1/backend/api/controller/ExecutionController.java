package edu.neu.cs6510.sp25.t1.backend.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import edu.neu.cs6510.sp25.t1.backend.service.ExecutionService;
import edu.neu.cs6510.sp25.t1.backend.data.entity.PipelineExecutionEntity;
import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;

import java.util.Optional;

/**
 * Controller for managing pipeline executions.
 */
@RestController
@RequestMapping("/api/executions")
public class ExecutionController {
  private final ExecutionService executionService;

  /**
   * Constructor for ExecutionController.
   *
   * @param executionService The service managing execution states.
   */
  public ExecutionController(ExecutionService executionService) {
    this.executionService = executionService;
  }

  /**
   * Starts a new execution for a given pipeline.
   *
   * @param pipelineName The name of the pipeline.
   * @return Response indicating success or failure.
   */
  @PostMapping("/{pipelineName}/start")
  public ResponseEntity<String> startExecution(@PathVariable String pipelineName) {
    boolean started = executionService.startPipelineExecution(pipelineName);

    if (!started) {
      return ResponseEntity.badRequest().body("Pipeline execution could not be started.");
    }

    return ResponseEntity.ok("Pipeline execution started successfully.");
  }

  /**
   * Retrieves the current execution status of a pipeline.
   *
   * @param pipelineName The name of the pipeline.
   * @return Execution status of the pipeline.
   */
  @GetMapping("/{pipelineName}/status")
  public ResponseEntity<ExecutionStatus> getExecutionStatus(@PathVariable String pipelineName) {
    Optional<PipelineExecutionEntity> execution = executionService.getPipelineExecution(pipelineName);

    if (execution.isEmpty()) {
      return ResponseEntity.notFound().build();
    }

    return ResponseEntity.ok(execution.get().getState());
  }

  /**
   * Stops a running execution of a pipeline.
   *
   * @param pipelineName The name of the pipeline.
   * @return Response indicating success or failure.
   */
  @PostMapping("/{pipelineName}/stop")
  public ResponseEntity<String> stopExecution(@PathVariable String pipelineName) {
    boolean stopped = executionService.stopPipelineExecution(pipelineName);

    if (!stopped) {
      return ResponseEntity.badRequest().body("Pipeline execution could not be stopped.");
    }

    return ResponseEntity.ok("Pipeline execution stopped successfully.");
  }
}
