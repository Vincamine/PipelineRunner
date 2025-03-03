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
import edu.neu.cs6510.sp25.t1.common.runtime.ExecutionState;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BackendClientTest {

  @Mock
  private RestTemplate restTemplate;

  @InjectMocks
  private BackendClient backendClient;

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

    backendClient.sendJobStatus(JOB_NAME, ExecutionState.SUCCESS);
    verify(restTemplate, times(1)).postForEntity(eq(BACKEND_URL), any(JobStatusUpdate.class), eq(String.class));
  }

  @Test
  void testSendJobStatus_Failure() {
    when(restTemplate.postForEntity(eq(BACKEND_URL), any(JobStatusUpdate.class), eq(String.class)))
            .thenThrow(new RuntimeException("Failed request"));

    backendClient.sendJobStatus(JOB_NAME, ExecutionState.FAILED);
    verify(restTemplate, times(3)).postForEntity(eq(BACKEND_URL), any(JobStatusUpdate.class), eq(String.class));
  }

  @Test
  void testGetJobStatus_Success() {
    when(restTemplate.getForEntity(BACKEND_URL + "/" + JOB_NAME, String.class))
            .thenReturn(new ResponseEntity<>("SUCCESS", HttpStatus.OK));

    ExecutionState state = backendClient.getJobStatus(JOB_NAME);
    assertEquals(ExecutionState.SUCCESS, state);
  }

  @Test
  void testGetJobStatus_UnknownOnFailure() {
    when(restTemplate.getForEntity(BACKEND_URL + "/" + JOB_NAME, String.class))
            .thenThrow(new RuntimeException("Failed request"));

    ExecutionState state = backendClient.getJobStatus(JOB_NAME);
    assertEquals(ExecutionState.UNKNOWN, state);
  }
}

