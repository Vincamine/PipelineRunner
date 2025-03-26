package edu.neu.cs6510.sp25.t1.backend.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import edu.neu.cs6510.sp25.t1.backend.service.execution.StageExecutionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controller for managing pipeline stages
 */
@RestController
@RequestMapping("/api/stage")
@Tag(name = "Stage API", description = "Endpoints for managing pipeline stages")
public class StageController {

  private final StageExecutionService stageExecutionService;

  /**
   * Constructor
   *
   * @param stageExecutionService Service for managing pipeline stages
   */
  public StageController(StageExecutionService stageExecutionService) {
    this.stageExecutionService = stageExecutionService;
  }

  /**
   * Get the status of a specific stage in a pipeline execution.
   *
   * @param pipelineExecutionId the pipeline execution ID
   * @param stageId             the stage ID
   * @return the status of the stage
   */
  @GetMapping("/status/{pipelineExecutionId}/{stageId}")
  @Operation(summary = "Get stage execution status", description = "Retrieves the status of a specific stage in a pipeline execution.")
  public ResponseEntity<String> getStageStatus(@PathVariable UUID pipelineExecutionId, @PathVariable UUID stageId) {
    String status = stageExecutionService.getStageStatus(pipelineExecutionId, stageId);
    return ResponseEntity.ok("{\"status\": \"" + status + "\"}");
  }

  /**
   * Manually start a stage execution.
   *
   * @param stageExecutionId UUID of the stage execution
   * @return ResponseEntity object
   */
  @PostMapping("/execute/{stageExecutionId}")
  @Operation(summary = "Start stage execution", description = "Manually triggers execution of a pipeline stage.")
  public ResponseEntity<String> executeStage(@PathVariable UUID stageExecutionId) {
    stageExecutionService.processStageExecution(stageExecutionId);
    return ResponseEntity.ok("{\"message\": \"Stage execution started successfully.\"}");
  }
}
