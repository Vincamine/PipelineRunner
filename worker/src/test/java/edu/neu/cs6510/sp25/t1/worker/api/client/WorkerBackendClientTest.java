package edu.neu.cs6510.sp25.t1.worker.api.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

import edu.neu.cs6510.sp25.t1.common.api.request.JobStatusUpdate;
import edu.neu.cs6510.sp25.t1.common.dto.JobExecutionDTO;
import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkerBackendClientTest {

  @Mock
  private RestTemplate restTemplate;

  @InjectMocks
  private WorkerBackendClient workerBackendClient;

  private final String backendApiUrl = "http://mock-backend/api";

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(workerBackendClient, "backendApiUrl", backendApiUrl);
  }

  @Test
  void testGetJobExecution() {
    UUID jobExecutionId = UUID.randomUUID();
    JobExecutionDTO mockResponse = new JobExecutionDTO();

    String expectedUrl = backendApiUrl + "/job/" + jobExecutionId;
    when(restTemplate.getForObject(expectedUrl, JobExecutionDTO.class)).thenReturn(mockResponse);

    JobExecutionDTO result = workerBackendClient.getJobExecution(jobExecutionId);
    assertNotNull(result);
    verify(restTemplate).getForObject(expectedUrl, JobExecutionDTO.class);
  }

  @Test
  void testGetJobDependencies() {
    UUID jobId = UUID.randomUUID();
    List<UUID> mockResponse = List.of(UUID.randomUUID(), UUID.randomUUID());

    String expectedUrl = backendApiUrl + "/jobs/" + jobId + "/dependencies";
    when(restTemplate.getForObject(expectedUrl, List.class)).thenReturn(mockResponse);

    List<UUID> result = workerBackendClient.getJobDependencies(jobId);
    assertNotNull(result);
    verify(restTemplate).getForObject(expectedUrl, List.class);
  }

  @Test
  void testGetJobStatus() {
    UUID jobId = UUID.randomUUID();
    ExecutionStatus mockResponse = ExecutionStatus.RUNNING;

    String expectedUrl = backendApiUrl + "/jobs/" + jobId + "/status";
    when(restTemplate.getForObject(expectedUrl, ExecutionStatus.class)).thenReturn(mockResponse);

    ExecutionStatus result = workerBackendClient.getJobStatus(jobId);
    assertEquals(mockResponse, result);
    verify(restTemplate).getForObject(expectedUrl, ExecutionStatus.class);
  }

  @Test
  void testUpdateJobStatus() {
    UUID jobExecutionId = UUID.randomUUID();
    ExecutionStatus status = ExecutionStatus.SUCCESS;
    String logs = "Job completed successfully.";

    String expectedUrl = backendApiUrl + "/job/status";
    JobStatusUpdate expectedRequest = new JobStatusUpdate(jobExecutionId, status, logs);

    doNothing().when(restTemplate).put(eq(expectedUrl), any(JobStatusUpdate.class));

    workerBackendClient.updateJobStatus(jobExecutionId, status, logs);
    verify(restTemplate).put(eq(expectedUrl), any(JobStatusUpdate.class));
  }
}
