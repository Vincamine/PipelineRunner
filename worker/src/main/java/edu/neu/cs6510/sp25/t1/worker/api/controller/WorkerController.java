package edu.neu.cs6510.sp25.t1.worker.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.neu.cs6510.sp25.t1.common.api.request.JobRequest;
import edu.neu.cs6510.sp25.t1.common.executor.JobExecutor;


/**
 * REST controller for handling job execution requests.
 */
@RestController
@RequestMapping("/api/jobs")
public class WorkerController {
  private static final Logger logger = LoggerFactory.getLogger(WorkerController.class);
  private final JobExecutor jobExecutor;

  /**
   * Constructor for WorkerController.
   *
   * @param jobExecutor JobExecutor instance to use for executing jobs.
   */
  @Autowired
  public WorkerController(JobExecutor jobExecutor) {
    this.jobExecutor = jobExecutor;
  }

  /**
   * Endpoint for executing a job.
   *
   * @param jobRequest JobRequest object containing the job name to execute.
   * @return ResponseEntity containing the result of the job execution.
   */
  @PostMapping("/execute")
  public ResponseEntity<String> executeJob(@RequestBody JobRequest jobRequest) {
    if (jobRequest == null || jobRequest.getJobName() == null || jobRequest.getJobName().isBlank()) {
      return ResponseEntity.badRequest().body("Error: Job name cannot be null or empty.");
    }

    logger.info("Received job execution request: {}", jobRequest.getJobName());

    try {
      jobExecutor.executeJob(jobRequest);
      return ResponseEntity.ok("Job execution started successfully.");
    } catch (Exception e) {
      logger.error("Error executing job: {}", jobRequest.getJobName(), e);
      return ResponseEntity.status(500).body("Job execution failed: " + e.getMessage());
    }
  }
}
