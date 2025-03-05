package edu.neu.cs6510.sp25.t1.backend.api.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.neu.cs6510.sp25.t1.common.api.request.JobExecutionRequest;
import edu.neu.cs6510.sp25.t1.common.api.request.JobStatusUpdate;
import edu.neu.cs6510.sp25.t1.common.api.response.JobExecutionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controller for job execution operations.
 */
@RestController
@RequestMapping("/api/job")
@Tag(name = "Job API", description = "Endpoints for managing job executions")
public class JobController {

  /**
   * Assigns a job to a worker node for execution.
   *
   * @param request The job execution request
   * @return The job execution response
   */
  @PostMapping("/execute")
  @Operation(summary = "Assign a job for execution", description = "Assigns a job to a worker node for execution.")
  public JobExecutionResponse executeJob(@RequestBody JobExecutionRequest request) {
    // TODO: Implement job execution logic
    return new JobExecutionResponse("67890", "QUEUED");
  }

  /**
   * Retrieves the status of a job execution.
   *
   * @param updateRequest The job status update request
   * @return The job status update response
   */
  @PostMapping("/status")
  @Operation(summary = "Update job execution status", description = "Allows workers to update the execution status of a job.")
  public String updateJobStatus(@RequestBody JobStatusUpdate updateRequest) {
    // TODO: Implement job status update logic
    return "{\"message\": \"Job status updated.\"}";
  }

  /**
   * Uploads job artifacts after execution
   *
   * @param request The artifact upload request
   * @return The artifact upload response
   */
  @PostMapping("/artifact/upload")
  @Operation(summary = "Upload job artifacts", description = "Allows workers to upload artifacts after job execution.")
  public String uploadJobArtifacts(@RequestBody Object request) {
    // TODO: Implement artifact upload logic
    return "{\"message\": \"Artifacts uploaded successfully.\"}";
  }

  /**
   * Cancels a running job execution.
   *
   * @param jobExecutionId The unique identifier for the job execution
   * @return A message indicating the job was canceled
   */
  @PostMapping("/cancel/{jobExecutionId}")
  @Operation(summary = "Cancel a job execution", description = "Cancels a running job execution.")
  public String cancelJobExecution(@PathVariable String jobExecutionId) {
    // TODO: Implement job cancellation logic
    return "{\"message\": \"Job execution canceled successfully.\"}";
  }
}
