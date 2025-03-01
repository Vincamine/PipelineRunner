package edu.neu.cs6510.sp25.t1.worker.client;

import edu.neu.cs6510.sp25.t1.common.api.JobStatusUpdate;
import edu.neu.cs6510.sp25.t1.common.model.ExecutionState;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;

class BackendClientTest {

    private final RestTemplate restTemplate = mock(RestTemplate.class);
    private final BackendClient backendClient = new BackendClient(restTemplate);

    @Test
    void testSendJobStatus_Success() {
        when(restTemplate.postForEntity(anyString(), any(JobStatusUpdate.class), eq(String.class)))
                .thenReturn(ResponseEntity.ok("Success"));

        backendClient.sendJobStatus("job1", ExecutionState.RUNNING);

        verify(restTemplate, times(1)).postForEntity(anyString(), any(JobStatusUpdate.class), eq(String.class));
    }

    @Test
    void testSendJobStatus_FailureAfterRetries() {
        when(restTemplate.postForEntity(anyString(), any(JobStatusUpdate.class), eq(String.class)))
                .thenThrow(new RuntimeException("Network error"));

        backendClient.sendJobStatus("job1", ExecutionState.FAILED);

        verify(restTemplate, times(3)).postForEntity(anyString(), any(JobStatusUpdate.class), eq(String.class));
    }
}
