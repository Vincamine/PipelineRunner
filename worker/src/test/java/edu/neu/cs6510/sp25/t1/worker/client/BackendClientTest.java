package edu.neu.cs6510.sp25.t1.worker.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import edu.neu.cs6510.sp25.t1.worker.client.BackendClient;

import static org.mockito.Mockito.*;

class BackendClientTest {

    private BackendClient backendClient;

    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        backendClient = new BackendClient(restTemplate);
    }

    @Test
    void testSendJobStatus_Success() {
        String jobName = "test-job";
        String status = "SUCCESS";

        ResponseEntity<String> mockResponse = ResponseEntity.ok("Job status updated");
        when(restTemplate.postForEntity(anyString(), any(), eq(String.class))).thenReturn(mockResponse);

        backendClient.sendJobStatus(jobName, status);

        verify(restTemplate, times(1)).postForEntity(anyString(), any(), eq(String.class));
    }

    @Test
    void testSendJobStatus_Failure() {
        String jobName = "test-job";
        String status = "FAILED";

        ResponseEntity<String> mockResponse = ResponseEntity.badRequest().body("Failed to update job status");
        when(restTemplate.postForEntity(anyString(), any(), eq(String.class))).thenReturn(mockResponse);

        backendClient.sendJobStatus(jobName, status);

        verify(restTemplate, times(1)).postForEntity(anyString(), any(), eq(String.class));
    }

    @Test
    void testSendJobStatus_Exception() {
        String jobName = "test-job";
        String status = "ERROR";

        when(restTemplate.postForEntity(anyString(), any(), eq(String.class))).thenThrow(new RuntimeException("Network error"));

        backendClient.sendJobStatus(jobName, status);

        verify(restTemplate, times(1)).postForEntity(anyString(), any(), eq(String.class));
    }
}
