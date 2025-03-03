package edu.neu.cs6510.sp25.t1.backend.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;

import java.util.Optional;

/**
 * Controller for managing job executions within a pipeline.
 */
@RestController
@RequestMapping("/api/jobs")
public class JobController {
  private final JobService jobService;

  /**
   * Constructor for JobController.
   *
   * @param jobService The service handling job executions.
   */
  public JobController(JobService jobService) {
    this.jobService = jobService;
  }

  /**
   * Retrieves the execution status of a specific job.
   *
   * @param jobId The job execution ID.
   * @return Response with job execution status.
   */
  @GetMapping("/{jobId}/status")
  public ResponseEntity<ExecutionStatus> getJobStatus(@PathVariable Long jobId) {
    Optional<JobExecutionEntity> jobExecution = jobService.getJobExecution(jobId);

    if (jobExecution.isEmpty()) {
      return ResponseEntity.notFound().build();
    }

    return ResponseEntity.ok(jobExecution.get().getStatus());
  }

  /**
   * Retries a failed job execution.
   *
   * @param jobId The job execution ID.
   * @return Response indicating success or failure.
   */
  @PostMapping("/{jobId}/retry")
  public ResponseEntity<String> retryJob(@PathVariable Long jobId) {
    boolean retried = jobService.retryJob(jobId);

    if (!retried) {
      return ResponseEntity.badRequest().body("Failed to retry job. Either job not found or not retryable.");
    }

    return ResponseEntity.ok("Job retried successfully.");
  }

  /**
   * Fetches logs for a specific job execution.
   *
   * @param jobId The job execution ID.
   * @return Response containing job logs.
   */
  @GetMapping("/{jobId}/logs")
  public ResponseEntity<String> getJobLogs(@PathVariable Long jobId) {
    Optional<String> logs = jobService.getJobLogs(jobId);

    if (logs.isEmpty()) {
      return ResponseEntity.notFound().build();
    }

    return ResponseEntity.ok(logs.get());
  }
}
