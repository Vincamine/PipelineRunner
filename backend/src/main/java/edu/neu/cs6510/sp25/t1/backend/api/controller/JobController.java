package edu.neu.cs6510.sp25.t1.backend.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import edu.neu.cs6510.sp25.t1.backend.service.JobExecutionService;
import edu.neu.cs6510.sp25.t1.common.api.request.JobExecutionRequest;
import edu.neu.cs6510.sp25.t1.common.api.request.JobStatusUpdate;
import edu.neu.cs6510.sp25.t1.common.api.response.JobExecutionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/job")
@Tag(name = "Job API", description = "Endpoints for managing job executions")
public class JobController {

  private final JobExecutionService jobExecutionService;

  public JobController(JobExecutionService jobExecutionService) {
    this.jobExecutionService = jobExecutionService;
  }

  @PostMapping("/execute")
  @Operation(summary = "Assign a job for execution", description = "Assigns a job to a worker node for execution.")
  public JobExecutionResponse executeJob(@RequestBody JobExecutionRequest request) {
    return jobExecutionService.startJobExecution(request);
  }

  @PostMapping("/status")
  @Operation(summary = "Update job execution status", description = "Allows workers to update the execution status of a job.")
  public String updateJobStatus(@RequestBody JobStatusUpdate updateRequest) {
    jobExecutionService.updateJobExecutionStatus(updateRequest.getJobExecutionId(), updateRequest.getStatus());
    return "{\"message\": \"Job status updated.\"}";
  }

  @PostMapping("/artifact/upload")
  @Operation(summary = "Upload job artifacts", description = "Allows workers to upload artifacts after job execution.")
  public ResponseEntity<String> uploadJobArtifacts(@RequestParam("jobId") UUID jobId,
                                                   @RequestParam("file") MultipartFile file) {
    try {
      // Define artifact storage location
      String storagePath = "/artifacts/" + jobId + "/";
      File directory = new File(storagePath);
      if (!directory.exists()) {
        directory.mkdirs();
      }

      // Save file
      File destinationFile = new File(directory, file.getOriginalFilename());
      file.transferTo(destinationFile);

      return ResponseEntity.ok("{\"message\": \"Artifacts uploaded successfully.\"}");
    } catch (IOException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
              .body("{\"error\": \"Failed to upload artifact: " + e.getMessage() + "\"}");
    }
  }


  @PostMapping("/cancel/{jobExecutionId}")
  @Operation(summary = "Cancel a job execution", description = "Cancels a running job execution.")
  public String cancelJobExecution(@PathVariable UUID jobExecutionId) {
    jobExecutionService.cancelJobExecution(jobExecutionId);
    return "{\"message\": \"Job execution canceled successfully.\"}";
  }
}
