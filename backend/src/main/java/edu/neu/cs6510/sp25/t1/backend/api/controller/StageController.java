package edu.neu.cs6510.sp25.t1.backend.api.controller;

import edu.neu.cs6510.sp25.t1.backend.service.StageExecutionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/stage")
@Tag(name = "Stage API", description = "Endpoints for managing pipeline stages")
public class StageController {

  private final StageExecutionService stageExecutionService;

  public StageController(StageExecutionService stageExecutionService) {
    this.stageExecutionService = stageExecutionService;
  }

  @GetMapping("/status/{pipelineExecutionId}/{stageId}")
  @Operation(summary = "Get stage execution status", description = "Retrieves the status of a specific stage in a pipeline execution.")
  public ResponseEntity<?> getStageStatus(@PathVariable UUID pipelineExecutionId, @PathVariable UUID stageId) {
    String status = stageExecutionService.getStageStatus(pipelineExecutionId, stageId);
    return ResponseEntity.ok("{\"status\": \"" + status + "\"}");
  }
}
