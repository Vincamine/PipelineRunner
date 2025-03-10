package edu.neu.cs6510.sp25.t1.worker.api.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

import edu.neu.cs6510.sp25.t1.common.api.request.JobStatusUpdate;
import edu.neu.cs6510.sp25.t1.common.dto.JobExecutionDTO;
import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;
import lombok.RequiredArgsConstructor;

/**
 * Client for communicating with the backend API.
 */
@Component
@RequiredArgsConstructor
public class WorkerBackendClient {
  private final RestTemplate restTemplate;

  @Value("${backend.api.url:http://backend-service/api}")
  private String backendApiUrl;

  /**
   * Retrieves the job execution details.
   *
   * @param jobExecutionId The job execution ID.
   * @return The job execution details.
   */
  public JobExecutionDTO getJobExecution(UUID jobExecutionId) {
    String url = backendApiUrl + "/job/" + jobExecutionId;
    return restTemplate.getForObject(url, JobExecutionDTO.class);
  }


  /**
   * Retrieves the job dependencies.
   *
   * @param jobId The job ID.
   * @return The list of job dependencies.
   */
  public List<UUID> getJobDependencies(UUID jobId) {
    String url = backendApiUrl + "/jobs/" + jobId + "/dependencies";
    return restTemplate.getForObject(url, List.class);
  }

  /**
   * Retrieves the job execution status.
   *
   * @param jobId The job ID.
   * @return The job execution status.
   */
  public ExecutionStatus getJobStatus(UUID jobId) {
    String url = backendApiUrl + "/jobs/" + jobId + "/status";
    return restTemplate.getForObject(url, ExecutionStatus.class);
  }

  /**
   * Updates job execution status in the backend.
   *
   * @param jobExecutionId The job execution ID.
   * @param status         The execution status.
   */
  public void updateJobStatus(UUID jobExecutionId, ExecutionStatus status, String logs) {
    String url = backendApiUrl + "/job/status";

    JobStatusUpdate request = new JobStatusUpdate(jobExecutionId, status, logs);

    restTemplate.put(url, request);
  }

// not used for now
//  /**
//   * Uploads artifacts after execution.
//   */
//  public void uploadArtifacts(UUID jobExecutionId, List<String> artifacts) {
//    String url = backendApiUrl + "/job/artifact/upload";
//    restTemplate.postForObject(url, new ArtifactUploadRequest(jobExecutionId, artifacts), Void.class);
//  }
}