package edu.neu.cs6510.sp25.t1.backend.api.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Component
public class WorkerClient {

  private final RestTemplate restTemplate;

  public WorkerClient(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public void notifyWorkerJobAssigned(UUID jobExecutionId) {
    String workerUrl = "http://worker-service/api/worker/job/" + jobExecutionId;
    restTemplate.postForEntity(workerUrl, null, String.class);
  }
}
