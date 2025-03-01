package edu.neu.cs6510.sp25.t1.backend.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import edu.neu.cs6510.sp25.t1.common.model.execution.JobExecution;

/**
 * WorkerClient is a REST client that sends jobs to the worker.
 */
@Component
public class WorkerClient {
  private final RestTemplate restTemplate;

  @Value("${worker.api.url}") // Load worker URL from application.yml
  private String workerUrl;

  /**
   * Constructor
   *
   * @param restTemplate RestTemplate
   */
  public WorkerClient(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  /**
   * Send a job to the worker.
   *
   * @param job JobExecution
   */
  public void sendJob(JobExecution job) {
    try {
      ResponseEntity<String> response = restTemplate.postForEntity(workerUrl, job, String.class);
      System.out.println("Worker Response: " + response.getBody());
    } catch (Exception e) {
      System.err.println("Failed to send job to worker: " + e.getMessage());
    }
  }
}
