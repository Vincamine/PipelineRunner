package edu.neu.cs6510.sp25.t1.worker.api.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;

/**
 * Client responsible for communicating job execution updates to the backend.
 * Ensures job statuses are updated and logs execution results.
 */
@Service
public class WorkerBackendClient {
  private static final Logger logger = LoggerFactory.getLogger(WorkerBackendClient.class);
  private final RestTemplate restTemplate;
  private final String backendUrl = "http://localhost:8080/api/jobs/status";

  /**
   * Constructor for WorkerBackendClient.
   *
   * @param restTemplate The Spring RestTemplate instance for making HTTP requests.
   */
  @Autowired
  public WorkerBackendClient(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  /**
   * Sends a job status update to the backend.
   *
   * @param jobName The name of the job.
   * @param status  The updated execution status.
   */
  public void sendJobStatus(String jobName, ExecutionStatus status) {
    try {
      JobStatusUpdate update = new JobStatusUpdate(jobName, status.name());
      ResponseEntity<String> response = restTemplate.postForEntity(backendUrl, update, String.class);

      if (response.getStatusCode().is2xxSuccessful()) {
        logger.info("Successfully updated job status: {} -> {}", jobName, status);
      } else {
        logger.warn("Failed to update job status: {} -> {} (Response Code: {})",
                jobName, status, response.getStatusCode());
      }
    } catch (Exception e) {
      logger.error("Error updating job status for {}: {}", jobName, e.getMessage());
    }
  }

  /**
   * Fetches the current job status from the backend.
   *
   * @param jobName The job name.
   * @return The execution status of the job.
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

  /**
   * Internal class representing a job status update request.
   */
  private static class JobStatusUpdate {
    private final String jobName;
    private final String status;

    public JobStatusUpdate(String jobName, String status) {
      this.jobName = jobName;
      this.status = status;
    }

    public String getJobName() {
      return jobName;
    }

    public String getStatus() {
      return status;
    }
  }
}
