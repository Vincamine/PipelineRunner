package edu.neu.cs6510.sp25.t1.backend.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import edu.neu.cs6510.sp25.t1.backend.data.dto.JobExecutionDTO;
import edu.neu.cs6510.sp25.t1.backend.service.JobExecutionService;
import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;

import java.util.Optional;

/**
 * Controller for managing job executions within a pipeline.
 */
@RestController
@RequestMapping("/api/jobs")
public class JobController {

  private final JobExecutionService jobExecutionService;

  public JobController(JobExecutionService jobExecutionService) {
    this.jobExecutionService = jobExecutionService;
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

    if (logs.isEmpty()) {
      return ResponseEntity.notFound().build();
    }

    return ResponseEntity.ok(logs.get());
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
}
