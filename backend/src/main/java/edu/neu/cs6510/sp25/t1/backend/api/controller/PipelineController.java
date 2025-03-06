package edu.neu.cs6510.sp25.t1.backend.api.controller;

import edu.neu.cs6510.sp25.t1.backend.service.PipelineExecutionService;
import edu.neu.cs6510.sp25.t1.common.api.request.PipelineExecutionRequest;
import edu.neu.cs6510.sp25.t1.common.api.response.PipelineExecutionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/pipeline")
@Tag(name = "Pipeline API", description = "Endpoints for managing pipeline executions")
public class PipelineController {

  private final PipelineExecutionService pipelineExecutionService;

  public PipelineController(PipelineExecutionService pipelineExecutionService) {
    this.pipelineExecutionService = pipelineExecutionService;
  }

  @PostMapping("/execute")
  @Operation(summary = "Trigger a pipeline execution", description = "Starts a new pipeline execution.")
  public PipelineExecutionResponse executePipeline(@RequestBody PipelineExecutionRequest request) {
    return pipelineExecutionService.startPipelineExecution(request);
  }

  @GetMapping("/status/{executionId}")
  @Operation(summary = "Get pipeline execution status", description = "Retrieves the status of a running or completed pipeline execution.")
  public PipelineExecutionResponse getPipelineStatus(@PathVariable UUID executionId) {
    return pipelineExecutionService.getPipelineExecution(executionId);
  }

  @PostMapping("/check-duplicate")
  @Operation(summary = "Check for duplicate pipeline execution", description = "Verifies if a pipeline execution with the same parameters already exists.")
  public boolean checkDuplicateExecution(@RequestBody PipelineExecutionRequest request) {
    return pipelineExecutionService.isDuplicateExecution(request);
  }
}
