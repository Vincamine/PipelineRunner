package edu.neu.cs6510.sp25.t1.worker.executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;
import edu.neu.cs6510.sp25.t1.worker.api.client.WorkerBackendClient;
import edu.neu.cs6510.sp25.t1.worker.api.request.JobRequest;

/**
 * JobExecutor is responsible for executing jobs inside a Docker container.
 * Handles dependencies, execution, and reporting back to the backend.
 */
@Service
public class JobExecutor {
  private static final Logger logger = LoggerFactory.getLogger(JobExecutor.class);
  private final WorkerBackendClient backendClient;

  @Autowired
  public JobExecutor(WorkerBackendClient backendClient) {
    this.backendClient = backendClient;
  }

  /**
   * Executes a job request inside a Docker container.
   *
   * @param jobRequest The job request containing execution details.
   * @return ExecutionStatus of the job execution.
   */
  public ExecutionStatus executeJob(JobRequest jobRequest) {
    if (jobRequest.getJobName() == null || jobRequest.getJobName().isBlank()) {
      logger.error("Invalid job request: missing job name.");
      return ExecutionStatus.FAILED;
    }

    // Step 1: Notify Backend that Job is QUEUED
    backendClient.sendJobStatus(jobRequest.getJobName(), ExecutionStatus.QUEUED);

    // Step 2: Check and Wait for Dependencies
    if (!jobRequest.getNeeds().isEmpty()) {
      logger.info("Job {} is waiting for dependencies: {}", jobRequest.getJobName(), jobRequest.getNeeds());
      if (!waitForDependencies(jobRequest.getNeeds())) {
        logger.error("Job {} failed: Dependencies did not complete in time", jobRequest.getJobName());
        backendClient.sendJobStatus(jobRequest.getJobName(), ExecutionStatus.FAILED);
        return ExecutionStatus.FAILED;
      }
    }

    // Step 3: Mark Job as RUNNING
    backendClient.sendJobStatus(jobRequest.getJobName(), ExecutionStatus.RUNNING);

    // Step 4: Execute Job
    boolean success = executeInDocker(jobRequest);

    // Step 5: Determine final job status
    ExecutionStatus finalState = success ? ExecutionStatus.SUCCESS : ExecutionStatus.FAILED;
    backendClient.sendJobStatus(jobRequest.getJobName(), finalState);

    return finalState;
  }

  /**
   * Runs a job inside a simulated execution environment.
   *
   * @param jobRequest The job request containing execution details.
   * @return true if execution was successful, false otherwise.
   */
  private boolean executeInDocker(JobRequest jobRequest) {
    logger.info("Executing job: {}", jobRequest.getJobName());
    return Math.random() > 0.1; // Simulating a 90% success rate
  }

  /**
   * Waits for all dependencies to complete before executing the job.
   *
   * @param dependencies List of job dependencies.
   * @return true if all dependencies are complete, false otherwise.
   */
  private boolean waitForDependencies(List<String> dependencies) {
    for (String dependency : dependencies) {
      ExecutionStatus state = backendClient.getJobStatus(dependency);
      if (state != ExecutionStatus.SUCCESS) {
        return false;
      }
    }
    return true;
  }
}