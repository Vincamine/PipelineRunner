package edu.neu.cs6510.sp25.t1.backend.api.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class WorkerClientTest {

  @Mock
  private RestTemplate restTemplate;

  private WorkerClient workerClient;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    workerClient = new WorkerClient(restTemplate);
  }

  @Test
  void testNotifyWorkerJobAssigned_Success() {
    // Arrange
    UUID jobExecutionId = UUID.randomUUID();
    String expectedUrl = "http://worker-service/api/worker/job/" + jobExecutionId;
    ResponseEntity<String> mockResponse = new ResponseEntity<>("Success", HttpStatus.OK);

    when(restTemplate.postForEntity(eq(expectedUrl), eq(null), eq(String.class)))
            .thenReturn(mockResponse);

    // Act & Assert
    assertDoesNotThrow(() -> workerClient.notifyWorkerJobAssigned(jobExecutionId));

    // Verify the REST call was made with correct parameters
    verify(restTemplate, times(1)).postForEntity(eq(expectedUrl), eq(null), eq(String.class));
  }

  @Test
  void testNotifyWorkerJobAssigned_WithNullJobId() {
    // Arrange
    UUID jobExecutionId = null;
    String expectedUrl = "http://worker-service/api/worker/job/null";

    // Act
    workerClient.notifyWorkerJobAssigned(jobExecutionId);

    // Assert
    // Verify that REST call was made with the URL containing "null"
    verify(restTemplate, times(1)).postForEntity(eq(expectedUrl), eq(null), eq(String.class));
  }

  @Test
  void testNotifyWorkerJobAssigned_HandlesRestClientException() {
    // Arrange
    UUID jobExecutionId = UUID.randomUUID();
    String expectedUrl = "http://worker-service/api/worker/job/" + jobExecutionId;

    // Mock RestTemplate to throw exception
    doThrow(new RestClientException("Connection refused"))
            .when(restTemplate).postForEntity(eq(expectedUrl), eq(null), eq(String.class));

    // Act & Assert
    assertThrows(RestClientException.class, () -> workerClient.notifyWorkerJobAssigned(jobExecutionId));

    // Verify the REST call was attempted
    verify(restTemplate, times(1)).postForEntity(eq(expectedUrl), eq(null), eq(String.class));
  }

  @Test
  void testConstructor() {
    // Simple test to ensure constructor works properly
    WorkerClient client = new WorkerClient(restTemplate);
    assertDoesNotThrow(() -> client.notifyWorkerJobAssigned(UUID.randomUUID()));
  }
}