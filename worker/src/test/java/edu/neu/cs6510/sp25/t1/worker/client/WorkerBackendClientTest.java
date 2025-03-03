package edu.neu.cs6510.sp25.t1.worker.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import edu.neu.cs6510.sp25.t1.common.api.JobStatusUpdate;
import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;
import edu.neu.cs6510.sp25.t1.worker.api.client.WorkerBackendClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class WorkerBackendClientTest {

  @Mock
  private RestTemplate restTemplate;

  @InjectMocks
  private WorkerBackendClient workerBackendClient;

  private static final String JOB_NAME = "test-job";
  private static final String BACKEND_URL = "http://localhost:8080/api/jobs/status";

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testSendJobStatus_Success() {
    when(restTemplate.postForEntity(eq(BACKEND_URL), any(JobStatusUpdate.class), eq(String.class)))
            .thenReturn(new ResponseEntity<>("OK", HttpStatus.OK));

    workerBackendClient.sendJobStatus(JOB_NAME, ExecutionStatus.SUCCESS);
    verify(restTemplate, times(1)).postForEntity(eq(BACKEND_URL), any(JobStatusUpdate.class), eq(String.class));
  }

  @Test
  void testSendJobStatus_Failure() {
    when(restTemplate.postForEntity(eq(BACKEND_URL), any(JobStatusUpdate.class), eq(String.class)))
            .thenThrow(new RuntimeException("Failed request"));

    workerBackendClient.sendJobStatus(JOB_NAME, ExecutionStatus.FAILED);
    verify(restTemplate, times(3)).postForEntity(eq(BACKEND_URL), any(JobStatusUpdate.class), eq(String.class));
  }

  @Test
  void testGetJobStatus_Success() {
    when(restTemplate.getForEntity(BACKEND_URL + "/" + JOB_NAME, String.class))
            .thenReturn(new ResponseEntity<>("SUCCESS", HttpStatus.OK));

    ExecutionStatus state = workerBackendClient.getJobStatus(JOB_NAME);
    assertEquals(ExecutionStatus.SUCCESS, state);
  }

  @Test
  void testGetJobStatus_UnknownOnFailure() {
    when(restTemplate.getForEntity(BACKEND_URL + "/" + JOB_NAME, String.class))
            .thenThrow(new RuntimeException("Failed request"));

    ExecutionStatus state = workerBackendClient.getJobStatus(JOB_NAME);
    assertEquals(ExecutionStatus.UNKNOWN, state);
  }
}

