package edu.neu.cs6510.sp25.t1.backend.api.controller;

import org.springframework.beans.factory.annotation.Value;
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
import edu.neu.cs6510.sp25.t1.common.api.request.JobExecutionRequest;
import edu.neu.cs6510.sp25.t1.common.api.request.JobStatusUpdate;
import edu.neu.cs6510.sp25.t1.common.api.response.JobExecutionResponse;
import edu.neu.cs6510.sp25.t1.common.dto.JobExecutionDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/job")
@Tag(name = "Job API", description = "Endpoints for managing job executions")
public class JobController {

  private final JobExecutionService jobExecutionService;

  @Value("${job.artifact.storage.path:/artifacts}")
  private String artifactStoragePath;

  public JobController(JobExecutionService jobExecutionService) {
    this.jobExecutionService = jobExecutionService;
  }

  @PostMapping("/execute")
  @Operation(summary = "Assign a job for execution", description = "Assigns a job to a worker node for execution.")
  public JobExecutionResponse executeJob(@RequestBody JobExecutionRequest request) {
    return jobExecutionService.startJobExecution(request);
  }

  @PutMapping("/status")
  @Operation(summary = "Update job execution status", description = "Allows workers to update job execution status.")
  public ResponseEntity<String> updateJobStatus(@RequestBody JobStatusUpdate updateRequest) {
    jobExecutionService.updateJobExecutionStatus(updateRequest.getJobExecutionId(), updateRequest.getStatus());
    return ResponseEntity.ok("{\"message\": \"Job status updated successfully.\"}");
  }

  @GetMapping("/{jobExecutionId}")
  @Operation(summary = "Retrieve job execution details", description = "Fetches execution details of a job.")
  public ResponseEntity<?> getJobExecution(@PathVariable UUID jobExecutionId) {
    try {
      return ResponseEntity.ok(jobExecutionService.getJobExecution(jobExecutionId));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(404).body("{\"error\": \"Job execution not found.\"}");
    }
  }

  @GetMapping("/{jobId}/dependencies")
  @Operation(summary = "Retrieve job dependencies", description = "Fetches dependencies of a job.")
  public ResponseEntity<List<UUID>> getJobDependencies(@PathVariable UUID jobId) {
    return ResponseEntity.ok(jobExecutionService.getJobDependencies(jobId));
  }

  @PostMapping("/worker/assign/{jobExecutionId}")
  @Operation(summary = "Notify worker of job assignment", description = "Notifies worker that a job has been assigned.")
  public ResponseEntity<String> notifyWorker(@PathVariable UUID jobExecutionId) {
    // Implement logic if needed
    return ResponseEntity.ok("{\"message\": \"Worker notified successfully.\"}");
  }

  // not implemented yet
//  @PostMapping("/artifact/upload")
//  @Operation(summary = "Upload job artifacts", description = "Receives artifact file paths after upload by workers.")
//  public ResponseEntity<String> uploadJobArtifacts(@RequestBody ArtifactUploadRequest request) {
//    jobExecutionService.saveArtifactPaths(request.getJobExecutionId(), request.getArtifactPaths());
//    return ResponseEntity.ok("{\"message\": \"Artifact paths saved successfully.\"}");
//  }
}