package edu.neu.cs6510.sp25.t1.backend.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import edu.neu.cs6510.sp25.t1.backend.service.JobExecutionTrackingService;
import edu.neu.cs6510.sp25.t1.common.api.response.UpdateExecutionStateRequest;

/**
 * Controller for handling job execution logging requests.
 */
@RestController
@RequestMapping("/api/v1/jobs")
public class JobController {
  private final JobExecutionTrackingService jobExecutionTrackingService;

  public JobController(JobExecutionTrackingService jobExecutionTrackingService) {
    this.jobExecutionTrackingService = jobExecutionTrackingService;
  }

  /**
   * Logs when a job execution starts.
   */
  @PostMapping("/log/start")
  public ResponseEntity<Void> logJobExecutionStart(@RequestBody UpdateExecutionStateRequest request) {
    jobExecutionTrackingService.logJobExecution(request.getName(), "STARTED");
    return ResponseEntity.ok().build();
  }

  /**
   * Logs when a job execution succeeds.
   */
  @PostMapping("/log/success")
  public ResponseEntity<Void> logJobExecutionSuccess(@RequestBody UpdateExecutionStateRequest request) {
    jobExecutionTrackingService.logJobExecution(request.getName(), "SUCCESS");
    return ResponseEntity.ok().build();
  }

  /**
   * Logs when a job execution fails.
   */
  @PostMapping("/log/failure")
  public ResponseEntity<Void> logJobExecutionFailure(@RequestBody UpdateExecutionStateRequest request) {
    jobExecutionTrackingService.logJobExecution(request.getName(), "FAILED");
    return ResponseEntity.ok().build();
  }
}
