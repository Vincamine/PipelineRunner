package edu.neu.cs6510.sp25.t1.backend.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.context.annotation.Lazy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import edu.neu.cs6510.sp25.t1.backend.error.ApiError;
import edu.neu.cs6510.sp25.t1.backend.service.PipelineExecutionService;
import edu.neu.cs6510.sp25.t1.backend.service.queue.PipelineExecutionQueueService;
import edu.neu.cs6510.sp25.t1.common.api.request.PipelineExecutionRequest;
import edu.neu.cs6510.sp25.t1.common.api.response.PipelineExecutionResponse;
import edu.neu.cs6510.sp25.t1.common.logging.PipelineLogger;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controller class for handling pipeline execution related endpoints.
 * This controller has been updated to work with the queue-based execution system.
 */
@RestController
@RequestMapping("/api/pipeline")
@Tag(name = "Pipeline API", description = "Endpoints for managing pipeline executions")
public class PipelineController {

  private final PipelineExecutionService pipelineExecutionService;
  @Lazy
  private final PipelineExecutionQueueService pipelineExecutionQueueService;

  /**
   * Constructor for PipelineController.
   *
   * @param pipelineExecutionService PipelineExecutionService instance
   * @param pipelineExecutionQueueService PipelineExecutionQueueService instance
   */
  public PipelineController(
      PipelineExecutionService pipelineExecutionService,
      PipelineExecutionQueueService pipelineExecutionQueueService) {
    this.pipelineExecutionService = pipelineExecutionService;
    this.pipelineExecutionQueueService = pipelineExecutionQueueService;
  }

  /**
   * Retrieve the status of a pipeline execution.
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
   * Trigger a pipeline execution.
   *
   * @param request PipelineExecutionRequest object
   * @return ResponseEntity object
   */
  @PostMapping("/run")
  @Operation(summary = "Trigger pipeline execution", description = "Starts a new pipeline execution.")
  public ResponseEntity<?> runPipeline(@RequestBody PipelineExecutionRequest request) {
    PipelineLogger.info("Received pipeline execution request for: " + request.getFilePath());

    try {
      // Validate filePath exists
      if (request.getFilePath() == null || request.getFilePath().isEmpty()) {
        PipelineLogger.error("Pipeline file path is missing in the request");
        return ResponseEntity.badRequest().body(
            new ApiError(HttpStatus.BAD_REQUEST, "Invalid Request", "Pipeline file path is required"));
      }

      PipelineExecutionResponse response = pipelineExecutionService.startPipelineExecution(request);
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      PipelineLogger.error("Failed pipeline execution: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
          new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "Pipeline Execution Failed", e.getMessage()));
    }
  }
  
  /**
   * Get information about the execution queue.
   * This is useful for debugging and monitoring the queue.
   *
   * @return ResponseEntity with queue information
   */
  @GetMapping("/queue/status")
  @Operation(summary = "Get pipeline queue status", description = "Returns information about the pipeline execution queue.")
  public ResponseEntity<?> getQueueStatus() {
    Map<String, Object> response = new HashMap<>();
    response.put("queueSize", pipelineExecutionQueueService.getQueueSize());
    response.put("isProcessing", pipelineExecutionQueueService.isProcessing());
    
    return ResponseEntity.ok(response);
  }
  
  /**
   * Debug endpoint to check the database status for a specific pipeline execution.
   * This is useful for debugging persistence issues.
   *
   * @param executionId The pipeline execution ID to check
   * @return ResponseEntity with detailed database status
   */
  @GetMapping("/debug/{executionId}")
  @Operation(summary = "Debug pipeline database state", description = "Checks if all entities for a pipeline execution exist in the database.")
  public ResponseEntity<?> debugDatabaseState(@PathVariable UUID executionId) {
    try {
      // Get necessary repositories
      var pipelineExecRepo = pipelineExecutionService.getClass().getDeclaredField("pipelineExecutionRepository").get(pipelineExecutionService);
      var stageExecRepo = pipelineExecutionService.getClass().getDeclaredField("stageExecutionRepository").get(pipelineExecutionService);
      var jobExecRepo = pipelineExecutionService.getClass().getDeclaredField("jobExecutionRepository").get(pipelineExecutionService);
      
      Map<String, Object> response = new HashMap<>();
      
      // Check pipeline execution
      boolean pipelineExists = ((boolean)pipelineExecRepo.getClass().getMethod("existsById", Object.class).invoke(pipelineExecRepo, executionId));
      response.put("pipelineExecutionExists", pipelineExists);
      
      if (pipelineExists) {
        // Get and check stage executions
        List<Object> stages = (List<Object>)stageExecRepo.getClass().getMethod("findByPipelineExecutionId", UUID.class).invoke(stageExecRepo, executionId);
        response.put("stageExecutionCount", stages.size());
        
        // Check job executions for each stage
        int totalJobs = 0;
        for (Object stage : stages) {
          List<Object> jobs = (List<Object>)jobExecRepo.getClass().getMethod("findByStageExecution", stage.getClass()).invoke(jobExecRepo, stage);
          totalJobs += jobs.size();
        }
        response.put("jobExecutionCount", totalJobs);
        
        // Overall status
        response.put("status", (stages.size() > 0 && totalJobs > 0) ? "COMPLETE" : "INCOMPLETE");
      } else {
        response.put("status", "NOT_FOUND");
      }
      
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      PipelineLogger.error("Error checking database state: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "Error checking database state", e.getMessage()));
    }
  }
}
