package edu.neu.cs6510.sp25.t1.backend.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import edu.neu.cs6510.sp25.t1.backend.service.PipelineExecutionService;
import edu.neu.cs6510.sp25.t1.common.api.request.PipelineExecutionRequest;
import edu.neu.cs6510.sp25.t1.common.api.response.PipelineExecutionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controller class for handling pipeline execution related endpoints.
 */
@RestController
@RequestMapping("/api/pipeline")
@Tag(name = "Pipeline API", description = "Endpoints for managing pipeline executions")
public class PipelineController {

  private final PipelineExecutionService pipelineExecutionService;

  /**
   * Constructor for PipelineController.
   *
   * @param pipelineExecutionService PipelineExecutionService instance
   */
  public PipelineController(PipelineExecutionService pipelineExecutionService) {
    this.pipelineExecutionService = pipelineExecutionService;
  }

  /**
   * Endpoint for retrieving the status of a pipeline execution.
   *
   * @param executionId UUID of the pipeline execution
   * @return ResponseEntity object
   */
  @GetMapping("/status/{executionId}")
  @Operation(summary = "Get pipeline execution status", description = "Retrieves the status of a running or completed pipeline execution.")
  public ResponseEntity<?> getPipelineStatus(@PathVariable UUID executionId) {
    try {
      return ResponseEntity.ok(pipelineExecutionService.getPipelineExecution(executionId));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(404).body("{\"error\": \"Pipeline execution not found.\"}");
    }
  }

  /**
   * Endpoint for triggering a pipeline execution.
   *
   * @param request PipelineExecutionRequest object
   * @return ResponseEntity object
   */
  @PostMapping("/run")
  @Operation(summary = "Trigger pipeline execution", description = "Starts a new pipeline execution.")
  public ResponseEntity<PipelineExecutionResponse> runPipeline(@RequestBody PipelineExecutionRequest request) {
    PipelineExecutionResponse response = pipelineExecutionService.startPipelineExecution(request);
    return ResponseEntity.ok(response);
  }

}