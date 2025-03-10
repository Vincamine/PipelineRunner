package edu.neu.cs6510.sp25.t1.backend.api.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import edu.neu.cs6510.sp25.t1.backend.api.client.WorkerClient;
import edu.neu.cs6510.sp25.t1.backend.service.JobExecutionService;
import edu.neu.cs6510.sp25.t1.common.api.request.JobExecutionRequest;
import edu.neu.cs6510.sp25.t1.common.api.request.JobStatusUpdate;
import edu.neu.cs6510.sp25.t1.common.api.response.JobExecutionResponse;
import edu.neu.cs6510.sp25.t1.common.dto.JobExecutionDTO;
import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class JobControllerTest {

  @Mock
  private JobExecutionService jobExecutionService;

  @Mock
  private WorkerClient workerClient;

  private JobController jobController;

  private UUID testUuid;
  private String testUuidString;
  private ExecutionStatus completedStatus;

  @BeforeEach
  void setUp() {
    testUuid = UUID.randomUUID();
    testUuidString = testUuid.toString();
    completedStatus = ExecutionStatus.SUCCESS;

    // Create controller with mocked dependencies
    jobController = new JobController(jobExecutionService, workerClient);
    ReflectionTestUtils.setField(jobController, "artifactStoragePath", "/test/artifacts");
  }

  @Test
  void testExecuteJob() {
    // Arrange
    JobExecutionRequest request = mock(JobExecutionRequest.class);
    JobExecutionResponse expectedResponse = mock(JobExecutionResponse.class);

    // Set up mock behavior with CORRECT STRING RETURN TYPE
    doReturn(expectedResponse).when(jobExecutionService).startJobExecution(any());
    doReturn(testUuidString).when(expectedResponse).getJobExecutionId();

    // Act
    JobExecutionResponse actualResponse = jobController.executeJob(request);

    // Assert
    assertNotNull(actualResponse);
    assertEquals(testUuidString, actualResponse.getJobExecutionId());
    verify(jobExecutionService, times(1)).startJobExecution(any());
  }

  @Test
  void testUpdateJobStatus() {
    // Arrange
    JobStatusUpdate request = mock(JobStatusUpdate.class);
    doReturn(testUuid).when(request).getJobExecutionId();
    doReturn(completedStatus).when(request).getStatus();

    // Act
    ResponseEntity<String> response = jobController.updateJobStatus(request);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertTrue(response.getBody().contains("Job status updated successfully"));
    verify(jobExecutionService, times(1))
            .updateJobExecutionStatus(eq(testUuid), eq(completedStatus));
  }

  @Test
  void testGetJobExecution_Success() {
    // Arrange - Use JobExecutionDTO instead of JobExecutionResponse
    JobExecutionDTO expectedDTO = mock(JobExecutionDTO.class);
    doReturn(expectedDTO).when(jobExecutionService).getJobExecution(any(UUID.class));

    // Act
    ResponseEntity<?> response = jobController.getJobExecution(testUuid);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(expectedDTO, response.getBody());
  }

  @Test
  void testGetJobExecution_NotFound() {
    // Arrange
    doThrow(new IllegalArgumentException("Job execution not found"))
            .when(jobExecutionService).getJobExecution(any(UUID.class));

    // Act
    ResponseEntity<?> response = jobController.getJobExecution(testUuid);

    // Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertTrue(response.getBody().toString().contains("Job execution not found"));
  }

  @Test
  void testGetJobDependencies() {
    // Arrange
    List<UUID> dependencies = Arrays.asList(UUID.randomUUID(), UUID.randomUUID());
    doReturn(dependencies).when(jobExecutionService).getJobDependencies(any(UUID.class));

    // Act
    ResponseEntity<List<UUID>> response = jobController.getJobDependencies(testUuid);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(dependencies, response.getBody());
    assertEquals(2, response.getBody().size());
  }

  @Test
  void testNotifyWorker_Success() {
    // Act
    ResponseEntity<String> response = jobController.notifyWorker(testUuid);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertTrue(response.getBody().contains("Worker notified successfully"));
    verify(workerClient, times(1)).notifyWorkerJobAssigned(any(UUID.class));
  }

  @Test
  void testNotifyWorker_Failure() {
    // Arrange
    doThrow(new RuntimeException("Connection failed"))
            .when(workerClient).notifyWorkerJobAssigned(any(UUID.class));

    // Act
    ResponseEntity<String> response = jobController.notifyWorker(testUuid);

    // Assert
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertTrue(response.getBody().contains("Failed to notify worker"));
    verify(workerClient, times(1)).notifyWorkerJobAssigned(any(UUID.class));
  }

  @Test
  void testConstructor() {
    // Arrange & Act
    JobController controller = new JobController(jobExecutionService, workerClient);

    // Assert
    assertNotNull(controller);
  }
}