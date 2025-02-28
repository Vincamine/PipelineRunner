package edu.neu.cs6510.sp25.t1.client;

import edu.neu.cs6510.sp25.t1.model.execution.JobExecution;
import edu.neu.cs6510.sp25.t1.model.definition.JobDefinition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class WorkerClientTest {
    private WorkerClient workerClient;
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        restTemplate = Mockito.mock(RestTemplate.class); // Mock RestTemplate
        workerClient = new WorkerClient(restTemplate); // Inject Mocked RestTemplate
    }

    @Test
    void testSendJob_Success() {
        // Create a valid JobDefinition
        JobDefinition jobDef = new JobDefinition(
            "job-001", "build", "gradle:8.12-jdk21",
            List.of("./gradlew compile"), List.of(), false
        );

        // Use JobDefinition in JobExecution
        JobExecution job = new JobExecution(jobDef, "PENDING", false, List.of());

        // Mock HTTP response
        ResponseEntity<String> mockResponse = ResponseEntity.ok("Job accepted");
        when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
                .thenReturn(mockResponse);

        // Run the method
        workerClient.sendJob(job);

        // Verify that RestTemplate was called
        verify(restTemplate, times(1)).postForEntity(anyString(), any(), eq(String.class));
    }
}
