package edu.neu.cs6510.sp25.t1.worker.api.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

import java.util.Optional;
import java.util.UUID;

import edu.neu.cs6510.sp25.t1.common.api.request.JobStatusUpdate;
import edu.neu.cs6510.sp25.t1.common.dto.JobExecutionDTO;
import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Client for communicating with the backend API.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class WorkerBackendClient {
  private final RestTemplate restTemplate;

  @Value("${backend.api.url:http://localhost:8080/api}")
  private String backendApiUrl;

  /**
   * Retrieves the job execution details from the backend.
   *
   * @param jobExecutionId The job execution ID.
   * @return The job execution details.
   */
  public Optional<JobExecutionDTO> getJobExecution(UUID jobExecutionId) {
    String url = backendApiUrl + "/job/" + jobExecutionId;
    try {
      return Optional.ofNullable(restTemplate.getForObject(url, JobExecutionDTO.class));
    } catch (RestClientException e) {
      log.error("Failed to fetch job execution details for {}: {}", jobExecutionId, e.getMessage());
      return Optional.empty();
    }
  }

  /**
   * Updates the job execution status in the backend.
   *
   * @param jobExecutionId The job execution ID.
   * @param status The new execution status.
   * @param logs Logs related to execution.
   */
  public void updateJobStatus(UUID jobExecutionId, ExecutionStatus status, String logs) {
    String url = backendApiUrl + "/job/status";
    JobStatusUpdate request = new JobStatusUpdate(jobExecutionId, status, logs);

    try {
      restTemplate.put(url, request);
      log.info("Job {} status updated to {}.", jobExecutionId, status);
    } catch (RestClientException e) {
      log.error("Failed to update job {} status: {}", jobExecutionId, e.getMessage());
    }
  }
}
