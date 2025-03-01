package edu.neu.cs6510.sp25.t1.worker.client;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class BackendClient {
    private final RestTemplate restTemplate;
    private final String backendUrl = "http://localhost:8080/api/jobs/status";

    public BackendClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void sendJobStatus(String jobName, String status) {
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(backendUrl,
                    new JobStatusUpdate(jobName, status),
                    String.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                System.err.println("Failed to update job status for " + jobName);
            }
        } catch (Exception e) {
            System.err.println("Error sending job status: " + e.getMessage());
        }
    }
}

class JobStatusUpdate {
    private String jobName;
    private String status;

    public JobStatusUpdate(String jobName, String status) {
        this.jobName = jobName;
        this.status = status;
    }

    public String getJobName() { return jobName; }
    public String getStatus() { return status; }
}
