package edu.neu.cs6510.sp25.t1.backend.api.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import edu.neu.cs6510.sp25.t1.backend.model.JobExecution;
import edu.neu.cs6510.sp25.t1.backend.model.StageExecution;

/**
 * WorkerClient is a REST client that sends jobs and stages to the worker.
 */
@Component
public class WorkerClient {
  private static final Logger logger = LoggerFactory.getLogger(WorkerClient.class);
  private final RestTemplate restTemplate;

  @Value("${worker.api.url}") // Load worker URL from application.yml
  private String workerUrl;

  public WorkerClient(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  /**
   * Sends a job execution request to the worker.
   *
   * @param job JobExecution details.
   * @return True if the request was successful, false otherwise.
   */
  public boolean sendJob(JobExecution job) {
    try {
      String endpoint = workerUrl + "/api/jobs/execute";
      ResponseEntity<String> response = restTemplate.postForEntity(endpoint, job, String.class);

      if (response.getStatusCode().is2xxSuccessful()) {
        logger.info("Job sent to worker successfully: {}", response.getBody());
        return true;
      } else {
        logger.warn("Job execution request failed: {}", response.getBody());
        return false;
      }
    } catch (Exception e) {
      logger.error("Failed to send job to worker: {}", e.getMessage());
      return false;
    }
  }

  /**
   * Sends a stage execution request to the worker.
   *
   * @param stage StageExecution details.
   * @return True if the request was successful, false otherwise.
   */
  public boolean sendStage(StageExecution stage) {
    try {
      String endpoint = workerUrl + "/api/stages/execute";
      ResponseEntity<String> response = restTemplate.postForEntity(endpoint, stage, String.class);

      if (response.getStatusCode().is2xxSuccessful()) {
        logger.info("Stage sent to worker successfully: {}", response.getBody());
        return true;
      } else {
        logger.warn("Stage execution request failed: {}", response.getBody());
        return false;
      }
    } catch (Exception e) {
      logger.error("Failed to send stage to worker: {}", e.getMessage());
      return false;
    }
  }
}
