package edu.neu.cs6510.sp25.t1.client;

import edu.neu.cs6510.sp25.t1.model.execution.JobExecution;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Client for sending job execution requests to the Worker service.
 */
@Component
public class WorkerClient {
    private final RestTemplate restTemplate;
    private final String workerUrl = "http://localhost:8081/api/jobs"; // Default URL

    /**
     * Constructor for dependency injection.
     */
    public WorkerClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Sends a job execution request to the Worker.
     */
    public void sendJob(JobExecution job) {
        ResponseEntity<String> response = restTemplate.postForEntity(workerUrl, job, String.class);
        System.out.println("Worker Response: " + response.getBody());
    }
}
