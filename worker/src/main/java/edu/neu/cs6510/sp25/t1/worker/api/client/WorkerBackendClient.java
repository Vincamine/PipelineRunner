package edu.neu.cs6510.sp25.t1.worker.api.client;

import edu.neu.cs6510.sp25.t1.common.api.request.ArtifactUploadRequest;
import edu.neu.cs6510.sp25.t1.common.api.request.JobStatusUpdate;
import edu.neu.cs6510.sp25.t1.common.dto.JobExecutionDTO;
import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

/**
 * Client for communicating with the backend API.
 */
@Component
@RequiredArgsConstructor
public class WorkerBackendClient {
  private final RestTemplate restTemplate;
  private static final String BACKEND_API_URL = "http://backend-service/api";

  /**
   * Retrieves job execution details from the backend.
   */
  public JobExecutionDTO getJobExecution(UUID jobExecutionId) {
    String url = BACKEND_API_URL + "/jobs/" + jobExecutionId;
    return restTemplate.getForObject(url, JobExecutionDTO.class);
  }

  /**
   * Retrieves dependencies of a job.
   */
  public List<UUID> getJobDependencies(UUID jobId) {
    String url = BACKEND_API_URL + "/jobs/" + jobId + "/dependencies";
    return restTemplate.getForObject(url, List.class);
  }

  /**
   * Retrieves the execution status of a job.
   */
  public ExecutionStatus getJobStatus(UUID jobId) {
    String url = BACKEND_API_URL + "/jobs/" + jobId + "/status";
    return restTemplate.getForObject(url, ExecutionStatus.class);
  }

  /**
   * Updates job execution status in the backend.
   *
   * @param jobExecutionId The job execution ID.
   * @param status         The execution status.
   */
  public void updateJobStatus(UUID jobExecutionId, ExecutionStatus status, String logs) {
    String url = BACKEND_API_URL + "/job/status";

    JobStatusUpdate request = new JobStatusUpdate(jobExecutionId, status, logs);

    restTemplate.put(url, request);
  }


  /**
   * Uploads artifacts after execution.
   */
  public void uploadArtifacts(UUID jobExecutionId, List<String> artifacts) {
    String url = BACKEND_API_URL + "/job/artifact/upload";
    restTemplate.postForObject(url, new ArtifactUploadRequest(jobExecutionId, artifacts), Void.class);
  }
}