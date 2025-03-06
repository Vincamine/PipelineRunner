package edu.neu.cs6510.sp25.t1.backend.api.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WorkerClientTest {

  @Mock
  private RestTemplate restTemplate;

  private WorkerClient workerClient;

  @BeforeEach
  public void setup() {
    workerClient = new WorkerClient(restTemplate);
  }

  @Test
  public void testNotifyWorkerJobAssigned_Success() {
    // Arrange
    UUID jobExecutionId = UUID.randomUUID();
    String expectedUrl = "http://worker-service/api/worker/job/" + jobExecutionId;
    ResponseEntity<String> responseEntity = new ResponseEntity<>("Success", HttpStatus.OK);

    when(restTemplate.postForEntity(eq(expectedUrl), eq(null), eq(String.class)))
            .thenReturn(responseEntity);

    // Act - should not throw any exception
    assertDoesNotThrow(() -> workerClient.notifyWorkerJobAssigned(jobExecutionId));

    // Assert
    verify(restTemplate, times(1)).postForEntity(eq(expectedUrl), eq(null), eq(String.class));
  }

  @Test
  public void testNotifyWorkerJobAssigned_WithNullJobId() {
    // Arrange
    UUID nullJobId = null;
    String expectedUrl = "http://worker-service/api/worker/job/" + nullJobId;

    // Act
    workerClient.notifyWorkerJobAssigned(nullJobId);

    // Assert - verify the URL contains "null" as string when null UUID is passed
    verify(restTemplate, times(1)).postForEntity(eq(expectedUrl), eq(null), eq(String.class));
  }

  @Test
  public void testNotifyWorkerJobAssigned_ClientError() {
    // Arrange
    UUID jobExecutionId = UUID.randomUUID();
    String expectedUrl = "http://worker-service/api/worker/job/" + jobExecutionId;

    when(restTemplate.postForEntity(eq(expectedUrl), eq(null), eq(String.class)))
            .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

    // Act & Assert
    HttpClientErrorException exception = assertThrows(HttpClientErrorException.class,
            () -> workerClient.notifyWorkerJobAssigned(jobExecutionId));

    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    verify(restTemplate, times(1)).postForEntity(eq(expectedUrl), eq(null), eq(String.class));
  }

  @Test
  public void testNotifyWorkerJobAssigned_ServerError() {
    // Arrange
    UUID jobExecutionId = UUID.randomUUID();
    String expectedUrl = "http://worker-service/api/worker/job/" + jobExecutionId;

    when(restTemplate.postForEntity(eq(expectedUrl), eq(null), eq(String.class)))
            .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

    // Act & Assert
    HttpServerErrorException exception = assertThrows(HttpServerErrorException.class,
            () -> workerClient.notifyWorkerJobAssigned(jobExecutionId));

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
    verify(restTemplate, times(1)).postForEntity(eq(expectedUrl), eq(null), eq(String.class));
  }

  @Test
  public void testNotifyWorkerJobAssigned_NetworkError() {
    // Arrange
    UUID jobExecutionId = UUID.randomUUID();
    String expectedUrl = "http://worker-service/api/worker/job/" + jobExecutionId;

    when(restTemplate.postForEntity(eq(expectedUrl), eq(null), eq(String.class)))
            .thenThrow(new ResourceAccessException("Connection refused"));

    // Act & Assert
    ResourceAccessException exception = assertThrows(ResourceAccessException.class,
            () -> workerClient.notifyWorkerJobAssigned(jobExecutionId));

    assertTrue(exception.getMessage().contains("Connection refused"));
    verify(restTemplate, times(1)).postForEntity(eq(expectedUrl), eq(null), eq(String.class));
  }
}