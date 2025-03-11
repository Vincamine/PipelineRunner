package edu.neu.cs6510.sp25.t1.backend.api.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

import java.util.UUID;
import java.util.Optional;

import edu.neu.cs6510.sp25.t1.common.logging.PipelineLogger;

/**
 * Client to communicate with the worker service.
 */
@Component
public class WorkerClient {

  private final RestTemplate restTemplate;

  @Value("${worker.service.url:http://localhost:5000/api/worker}")  // Default URL
  private String workerServiceUrl;

  /**
   * Constructor to initialize the worker client.
   *
   * @param restTemplate The REST template to use for communication.
   */
  public WorkerClient(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  /**
   * Notifies the worker service that a job has been assigned.
   *
   * @param jobExecutionId The ID of the job execution.
   */
  public Optional<String> notifyWorkerJobAssigned(UUID jobExecutionId) {
    String workerUrl = workerServiceUrl + "/job/" + jobExecutionId;

    try {
      ResponseEntity<String> response = restTemplate.postForEntity(workerUrl, null, String.class);

      if (response.getStatusCode().is2xxSuccessful()) {
        PipelineLogger.info("Successfully notified worker about job: " + jobExecutionId);
        return Optional.ofNullable(response.getBody());
      } else {
        PipelineLogger.error("Worker notification failed for job: " + jobExecutionId + " | Status: " + response.getStatusCode());
        return Optional.empty();
      }

    } catch (RestClientException e) {
      PipelineLogger.error("Error notifying worker about job " + jobExecutionId + ": " + e.getMessage());
      return Optional.empty();
    }
  }
}
