package edu.neu.cs6510.sp25.t1.backend.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Service for interacting with worker nodes.
 * Handles job execution requests and status polling.
 */
@Service
public class WorkerClientService {

  private final RestTemplate restTemplate;
  private final String workerApiBaseUrl;

  public WorkerClientService() {
    this.restTemplate = new RestTemplate();
    this.workerApiBaseUrl = "http://localhost:8081/api/worker"; // Update with actual worker API URL
  }

  /**
   * Sends a job execution request to the worker.
   *
   * @param jobId  The ID of the job.
   * @param runId  The unique execution run ID.
   * @param jobData The job configuration details.
   * @return The response from the worker node.
   */
  public ResponseEntity<String> executeJob(Long jobId, String runId, Map<String, Object> jobData) {
    String url = String.format("%s/execute/%s/%s", workerApiBaseUrl, runId, jobId);
    return restTemplate.postForEntity(url, jobData, String.class);
  }

  /**
   * Retrieves the current execution status of a job from the worker.
   *
   * @param jobId The job ID.
   * @param runId The unique execution run ID.
   * @return The job execution status.
   */
  public ResponseEntity<String> getJobStatus(Long jobId, String runId) {
    String url = String.format("%s/status/%s/%s", workerApiBaseUrl, runId, jobId);
    return restTemplate.getForEntity(url, String.class);
  }

  /**
   * Cancels a running job.
   *
   * @param jobId The ID of the job to cancel.
   * @param runId The execution run ID.
   * @return The response from the worker node.
   */
  public ResponseEntity<String> cancelJob(Long jobId, String runId) {
    String url = String.format("%s/cancel/%s/%s", workerApiBaseUrl, runId, jobId);
    return restTemplate.postForEntity(url, null, String.class);
  }
}
