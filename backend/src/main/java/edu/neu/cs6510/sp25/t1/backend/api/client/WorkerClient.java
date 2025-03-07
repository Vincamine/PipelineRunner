package edu.neu.cs6510.sp25.t1.backend.api.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

/**
 * Client to communicate with the worker service.
 */
@Component
public class WorkerClient {

  private final RestTemplate restTemplate;

  /**
   * Constructor to initialize the worker client.
   * @param restTemplate The REST template to use for communication.
   */
  public WorkerClient(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  /**
   * Notifies the worker service that a job has been assigned.
   * @param jobExecutionId The ID of the job execution.
   */
  public void notifyWorkerJobAssigned(UUID jobExecutionId) {
    String workerUrl = "http://worker-service/api/worker/job/" + jobExecutionId;
    restTemplate.postForEntity(workerUrl, null, String.class);
  }
}
