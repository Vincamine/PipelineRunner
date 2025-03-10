package edu.neu.cs6510.sp25.t1.backend.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import edu.neu.cs6510.sp25.t1.backend.service.PipelineExecutionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/pipeline")
@Tag(name = "Pipeline API", description = "Endpoints for managing pipeline executions")
public class PipelineController {

  private final PipelineExecutionService pipelineExecutionService;

  public PipelineController(PipelineExecutionService pipelineExecutionService) {
    this.pipelineExecutionService = pipelineExecutionService;
  }

  @GetMapping("/status/{executionId}")
  @Operation(summary = "Get pipeline execution status", description = "Retrieves the status of a running or completed pipeline execution.")
  public ResponseEntity<?> getPipelineStatus(@PathVariable UUID executionId) {
    try {
      return ResponseEntity.ok(pipelineExecutionService.getPipelineExecution(executionId));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(404).body("{\"error\": \"Pipeline execution not found.\"}");
    }
  }
}