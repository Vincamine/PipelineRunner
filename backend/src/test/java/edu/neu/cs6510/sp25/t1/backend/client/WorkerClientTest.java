package edu.neu.cs6510.sp25.t1.backend.client;

import edu.neu.cs6510.sp25.t1.common.model.execution.JobExecution;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Field;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;


class WorkerClientTest {

  private RestTemplate restTemplate;
  private WorkerClient workerClient;

  @BeforeEach
  void setUp() throws Exception {
    restTemplate = mock(RestTemplate.class);
    workerClient = new WorkerClient(restTemplate);

    // Inject mock URL since @Value does not work in tests
    Field workerUrlField = WorkerClient.class.getDeclaredField("workerUrl");
    workerUrlField.setAccessible(true);
    workerUrlField.set(workerClient, "http://mock-worker-url");
  }

  @Test
  void testSendJob_Success() {
    JobExecution job = mock(JobExecution.class);
    ResponseEntity<String> mockResponse = mock(ResponseEntity.class);
    when(mockResponse.getBody()).thenReturn("Success");
    when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
            .thenReturn(mockResponse);

    workerClient.sendJob(job);

    verify(restTemplate, times(1)).postForEntity(eq("http://mock-worker-url"), eq(job), eq(String.class));
  }

  @Test
  void testSendJob_Failure() {
    JobExecution job = mock(JobExecution.class);
    doThrow(new RuntimeException("Worker not available"))
            .when(restTemplate).postForEntity(anyString(), any(), eq(String.class));

    workerClient.sendJob(job);

    verify(restTemplate, times(1)).postForEntity(eq("http://mock-worker-url"), eq(job), eq(String.class));
  }
}
