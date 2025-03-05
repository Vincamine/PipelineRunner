package edu.neu.cs6510.sp25.t1.backend.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controller for stage execution operations.
 */
@RestController
@RequestMapping("/api/stage")
@Tag(name = "Stage API", description = "Endpoints for managing pipeline stages")
public class StageController {

  /**
   * Retrieves the status of a specific stage in a pipeline execution
   *
   * @param pipelineExecutionId The unique identifier for the pipeline execution
   * @param stageName           The name of the stage
   * @return The status of the stage execution
   */
  @GetMapping("/status/{pipelineExecutionId}/{stageName}")
  @Operation(summary = "Get stage execution status", description = "Retrieves the status of a specific stage in a pipeline execution.")
  public String getStageStatus(@PathVariable String pipelineExecutionId, @PathVariable String stageName) {
    // TODO: Implement stage execution status retrieval
    return "{\"stage\": \"" + stageName + "\", \"status\": \"RUNNING\"}";
  }
}
