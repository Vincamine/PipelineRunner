package edu.neu.cs6510.sp25.t1.worker.api.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import edu.neu.cs6510.sp25.t1.common.api.BackendClientInterface;
import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;

/**
 * Client to send job status updates to the backend server.
 */
@Service
public class WorkerBackendClient implements BackendClientInterface {
  private static final Logger logger = LoggerFactory.getLogger(WorkerBackendClient.class);
  private final RestTemplate restTemplate;
  private final String backendUrl = "http://localhost:8080/api/jobs/status";

  /**
   * Constructor for the WorkerBackendClient.
   *
   * @param restTemplate The RestTemplate for sending requests.
   */
  @Autowired
  public WorkerBackendClient(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  /**
   * Sends a job status update to the backend server.
   *
   * @param jobName The name of the job to update.
   * @param status  The new status of the job.
   */
  public void sendJobStatus(String jobName, ExecutionStatus status) {
    JobStatusUpdate update = new JobStatusUpdate(jobName, status.name()); // Convert enum to string
    try {
      ResponseEntity<String> response = restTemplate.postForEntity(backendUrl, update, String.class);
      if (response.getStatusCode().is2xxSuccessful()) {
        logger.info("Successfully updated job status: {} -> {}", jobName, status);
      } else {
        logger.warn("Failed to update job status: {} -> {} (Response Code: {})", jobName, status, response.getStatusCode());
      }
    } catch (Exception e) {
      logger.error("Error updating job status for {}: {}", jobName, e.getMessage());
    }
  }

  /**
   * Fetches the job status from the backend.
   *
   * @param jobName The name of the job.
   * @return The ExecutionStatus of the job.
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
   * Represents the response of a job execution.
   * Contains information about the job ID, exit code, output, success status,
   * collected artifacts, and error message if any.
   * Used for sending the response back to the client.
   */
  public static class JobResponse {
    private final String jobId; // Unique identifier for the job execution.
    private final int exitCode; // Process exit code (0 = success, non-zero = failure).
    private final String output; // Console/log output from execution.
    private final List<String> collectedArtifacts; // Paths to collected artifacts.
    private final String errorMessage; // Error message if execution failed.
    private ExecutionStatus status;


    public JobResponse(String jobId, int exitCode, String output,
                       List<String> collectedArtifacts, String errorMessage) {
      this.jobId = jobId;
      this.exitCode = exitCode;
      this.output = output != null ? output : ""; // Avoid null values
      this.collectedArtifacts = collectedArtifacts != null ? collectedArtifacts : List.of();
      this.errorMessage = errorMessage != null ? errorMessage : "";
    }

    public String getJobId() {
      return jobId;
    }

    public int getExitCode() {
      return exitCode;
    }

    public String getOutput() {
      return output;
    }

    public List<String> getCollectedArtifacts() {
      return collectedArtifacts;
    }

    public String getErrorMessage() {
      return errorMessage;
    }

    public boolean isSuccess() {
      return exitCode == 0;
    }

    public ExecutionStatus getStatus() {
      return status;
    }


    @Override
    public String toString() {
      return "JobResponse{" +
              "jobId='" + jobId + '\'' +
              ", exitCode=" + exitCode +
              ", output='" + output + '\'' +
              ", success=" + isSuccess() +
              ", collectedArtifacts=" + collectedArtifacts +
              ", errorMessage='" + errorMessage + '\'' +
              '}';
    }
  }
}
