package edu.neu.cs6510.sp25.t1.worker.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import edu.neu.cs6510.sp25.t1.worker.api.client.WorkerBackendClient;
import edu.neu.cs6510.sp25.t1.worker.api.request.JobRequest;
import edu.neu.cs6510.sp25.t1.worker.executor.JobExecutor;
import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;

import java.util.List;

/**
 * REST controller handling job execution requests sent from the backend.
 * Ensures proper execution and status reporting.
 */
@RestController
@RequestMapping("/api/jobs")
public class WorkerController {
  private static final Logger logger = LoggerFactory.getLogger(WorkerController.class);
  private final JobExecutor jobExecutor;
  private final WorkerBackendClient workerBackendClient;

  /**
   * Constructor for WorkerController.
   *
   * @param jobExecutor         The job execution handler.
   * @param workerBackendClient The backend client for reporting execution status.
   */
  @Autowired
  public WorkerController(JobExecutor jobExecutor, WorkerBackendClient workerBackendClient) {
    this.jobExecutor = jobExecutor;
    this.workerBackendClient = workerBackendClient;
  }

  /**
   * Handles job execution requests from the backend.
   *
   * @param jobRequest The job execution request payload.
   * @return Response indicating job execution success or failure.
   */
  @PostMapping("/execute")
  public ResponseEntity<String> executeJob(@RequestBody JobRequest jobRequest) {
    if (jobRequest == null || jobRequest.getJobName() == null || jobRequest.getJobName().isBlank()) {
      return ResponseEntity.badRequest().body("Error: Job name cannot be null or empty.");
    }

    logger.info("Received job execution request: {}", jobRequest.getJobName());

    // Execute the job using JobExecutor
    ExecutionStatus result = jobExecutor.executeJob(jobRequest);

    // Send the final status back to the backend
    workerBackendClient.sendJobStatus(jobRequest.getJobName(), result);

    return ResponseEntity.ok("Job execution completed with status: " + result);
  }

  @PostMapping("/stages/execute")
  public ResponseEntity<String> executeStage(@RequestBody List<JobRequest> jobRequests) {
    if (jobRequests == null || jobRequests.isEmpty()) {
      return ResponseEntity.badRequest().body("Error: Stage must contain at least one job.");
    }

    logger.info("Received stage execution request with {} jobs.", jobRequests.size());

    for (JobRequest job : jobRequests) {
      ExecutionStatus jobStatus = jobExecutor.executeJob(job);

      // If a job fails and is not allowed to fail, stop execution
      if (jobStatus == ExecutionStatus.FAILED && !job.isAllowFailure()) {
        return ResponseEntity.status(500).body("Stage execution failed due to job failure.");
      }
    }

    return ResponseEntity.ok("Stage execution completed successfully.");
  }

}
