package edu.neu.cs6510.sp25.t1.worker.api.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import edu.neu.cs6510.sp25.t1.common.api.JobStatusUpdate;
import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;

/**
 * Client to send job status updates to the backend server.
 */
@Service
public class WorkerBackendClient {
  private static final Logger logger = LoggerFactory.getLogger(WorkerBackendClient.class);
  private RestTemplate restTemplate = new RestTemplate();
  private final String backendUrl = "http://localhost:8080/api/jobs/status";

  /**
   * Constructor for the BackendClient.
   *
   * @param restTemplate The RestTemplate to use for sending requests.
   */
  @Autowired
  public WorkerBackendClient(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public WorkerBackendClient() {

  }

  /**
   * Send a job status update to the backend server.
   *
   * @param jobName The name of the job to update.
   * @param status  The new status of the job.
   */
  public void sendJobStatus(String jobName, ExecutionStatus status) {
    JobStatusUpdate update = new JobStatusUpdate(jobName, status.name()); // Convert enum to string

    for (int i = 0; i < 3; i++) { // Retry logic
      try {
        ResponseEntity<String> response = restTemplate.postForEntity(backendUrl, update, String.class);
        if (response.getStatusCode().is2xxSuccessful()) {
          logger.info("Successfully updated job status: {} -> {}", jobName, status.name());
          return;
        }
      } catch (Exception e) {
        logger.warn("Attempt {}/3 failed to update job status for {}: {}", i + 1, jobName, e.getMessage());
      }
    }
    logger.error("Failed to update job status for {} after 3 attempts.", jobName);
  }

  /**
   * Fetch the job status from the backend.
   *
   * @param jobName The name of the job.
   * @return The ExecutionState of the job.
   */
  public ExecutionStatus getJobStatus(String jobName) {
    try {
      ResponseEntity<String> response = restTemplate.getForEntity(backendUrl + "/" + jobName, String.class);
      return ExecutionStatus.fromString(response.getBody());
    } catch (Exception e) {
      logger.warn("Failed to fetch job status for {}: {}", jobName, e.getMessage());
      return ExecutionStatus.FAILED;
    }
  }

}
