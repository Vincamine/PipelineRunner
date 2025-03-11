package edu.neu.cs6510.sp25.t1.backend.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

import edu.neu.cs6510.sp25.t1.backend.service.JobExecutionService;
import edu.neu.cs6510.sp25.t1.common.api.request.JobStatusUpdate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controller class for handling job execution related endpoints.
 */
@RestController
@RequestMapping("/api/job")
@Tag(name = "Job API", description = "Endpoints for managing job executions")
public class JobController {

  private final JobExecutionService jobExecutionService;

  /**
   * Constructor for JobController.
   *
   * @param jobExecutionService JobExecutionService object
   */
  public JobController(JobExecutionService jobExecutionService) {
    this.jobExecutionService = jobExecutionService;
  }

  /**
   * Start a job execution.
   *
   * @param jobExecutionId UUID of the job execution
   * @return ResponseEntity object
   */
  @PostMapping("/execute/{jobExecutionId}")
  @Operation(summary = "Start job execution", description = "Triggers the execution of a job.")
  public ResponseEntity<String> executeJob(@PathVariable UUID jobExecutionId) {
    try {
      jobExecutionService.startJobExecution(jobExecutionId);
      return ResponseEntity.ok("{\"message\": \"Job execution started successfully.\"}");
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(404).body("{\"error\": \"Job execution not found.\"}");
    }
  }

  /**
   * Update job execution status.
   *
   * @param updateRequest JobStatusUpdate object
   * @return ResponseEntity object
   */
  @PutMapping("/status")
  @Operation(summary = "Update job execution status", description = "Allows workers to update job execution status.")
  public ResponseEntity<String> updateJobStatus(@RequestBody JobStatusUpdate updateRequest) {
    jobExecutionService.updateJobExecutionStatus(updateRequest.getJobExecutionId(), updateRequest.getStatus());
    return ResponseEntity.ok("{\"message\": \"Job status updated successfully.\"}");
  }

  /**
   * Retrieve job dependencies.
   *
   * @param jobId UUID of the job
   * @return ResponseEntity object
   */
  @GetMapping("/{jobId}/dependencies")
  @Operation(summary = "Retrieve job dependencies", description = "Fetches dependencies of a job.")
  public ResponseEntity<List<UUID>> getJobDependencies(@PathVariable UUID jobId) {
    return ResponseEntity.ok(jobExecutionService.getJobDependencies(jobId));
  }

  /**
   * Cancel a job execution.
   *
   * @param jobExecutionId UUID of the job execution
   * @return ResponseEntity object
   */
  @PostMapping("/cancel/{jobExecutionId}")
  @Operation(summary = "Cancel a job execution", description = "Cancels an ongoing job execution.")
  public ResponseEntity<String> cancelJob(@PathVariable UUID jobExecutionId) {
    jobExecutionService.cancelJobExecution(jobExecutionId);
    return ResponseEntity.ok("{\"message\": \"Job execution canceled successfully.\"}");
  }

// not implemented yet for this version
//  @PostMapping("/artifact/upload")
//  @Operation(summary = "Upload job artifacts", description = "Receives artifact file paths after upload by workers.")
//  public ResponseEntity<String> uploadJobArtifacts(@RequestBody ArtifactUploadRequest request) {
//    jobExecutionService.saveArtifactPaths(request.getJobExecutionId(), request.getArtifactPaths());
//    return ResponseEntity.ok("{\"message\": \"Artifact paths saved successfully.\"}");
//  }
}