package edu.neu.cs6510.sp25.t1.backend.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import edu.neu.cs6510.sp25.t1.backend.api.request.UpdateExecutionStateRequest;
import edu.neu.cs6510.sp25.t1.backend.database.dto.JobExecutionDTO;
import edu.neu.cs6510.sp25.t1.backend.service.JobExecutionService;
import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;
import edu.neu.cs6510.sp25.t1.worker.api.client.WorkerBackendClient;

/**
 * Controller for managing job executions within a pipeline.
 */
@RestController
@RequestMapping("/api/jobs")
public class JobController {
  private static final Logger logger = LoggerFactory.getLogger(JobController.class);
  private final JobExecutionService jobExecutionService;

  public JobController(JobExecutionService jobExecutionService) {
    this.jobExecutionService = jobExecutionService;
  }

  /**
   * Updates the execution status of a job.
   *
   * @param jobExecutionId The job execution ID.
   * @param jobResponse    The new job response.
   * @return Response indicating success or failure.
   */
  @PostMapping("/{jobExecutionId}/results")
  public ResponseEntity<String> updateJobResults(@PathVariable Long jobExecutionId,
                                                 @RequestBody WorkerBackendClient.JobResponse jobResponse) {
    logger.info("Updating job execution results for job: {}", jobResponse.getJobId());
    boolean updated = jobExecutionService.updateJobResults(jobExecutionId, jobResponse);
    if (!updated) {
      return ResponseEntity.badRequest().body("Failed to update job results.");
    }
    return ResponseEntity.ok("Job results updated successfully.");
  }

  /**
   * Retrieves the execution status of a specific job execution.
   *
   * @param jobExecutionId The job execution ID.
   * @return Response with job execution status.
   */
  @GetMapping("/{jobExecutionId}/status")
  public ResponseEntity<ExecutionStatus> getJobStatus(@PathVariable Long jobExecutionId) {
    JobExecutionDTO jobExecutionDTO = jobExecutionService.getJobExecution(jobExecutionId);
    if (jobExecutionDTO == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(jobExecutionDTO.getStatus());
  }

  /**
   * Fetches logs for a specific job execution.
   *
   * @param jobExecutionId The job execution ID.
   * @return Response containing job logs.
   */
  @GetMapping("/{jobExecutionId}/logs")
  public ResponseEntity<String> getJobLogs(@PathVariable Long jobExecutionId) {
    Optional<String> logs = jobExecutionService.getJobLogs(jobExecutionId);
    return logs.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
  }

  /**
   * Retrieves job execution details by job execution ID.
   *
   * @param jobExecutionId The job execution ID.
   * @return Response with job execution details.
   */
  @GetMapping("/{jobExecutionId}")
  public ResponseEntity<JobExecutionDTO> getJobExecution(@PathVariable Long jobExecutionId) {
    JobExecutionDTO jobExecutionDTO = jobExecutionService.getJobExecution(jobExecutionId);
    if (jobExecutionDTO == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(jobExecutionDTO);
  }

  /**
   * Updates the execution status of a job.
   *
   * @param jobExecutionId The job execution ID.
   * @param request        The new execution state request.
   * @return Response indicating success or failure.
   */
  @PostMapping("/{jobExecutionId}/status")
  public ResponseEntity<String> updateJobStatus(@PathVariable Long jobExecutionId,
                                                @RequestBody UpdateExecutionStateRequest request) {
    logger.info("Updating job status: {} -> {}", request.getName(), request.getState());
    boolean updated = jobExecutionService.updateJobStatus(jobExecutionId, request.getState());
    if (!updated) {
      return ResponseEntity.badRequest().body("Failed to update job status.");
    }
    return ResponseEntity.ok("Job status updated successfully.");
  }
}
