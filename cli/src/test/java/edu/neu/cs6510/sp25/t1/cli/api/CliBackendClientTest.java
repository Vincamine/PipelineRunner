package edu.neu.cs6510.sp25.t1.cli.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CliBackendClientTest {

  private static final String BACKEND_URL = "https://cicd-backend.example.com";

  @Mock
  private OkHttpClient mockHttpClient;

  @Mock
  private Call mockCall;

  @Mock
  private Response mockResponse;

  @Mock
  private ResponseBody mockResponseBody;

  private CliBackendClient client;

  @BeforeEach
  public void setUp() throws IOException {
    // Create client with mocked HTTP client
    client = new CliBackendClient(BACKEND_URL, mockHttpClient);

    // Basic setup for all tests
    when(mockHttpClient.newCall(any(Request.class))).thenReturn(mockCall);
    when(mockCall.execute()).thenReturn(mockResponse);
  }

  @Test
  public void testTriggerPipelineExecution_Success() throws IOException {
    // Arrange
    String repo = "github.com/user/repo";
    String branch = "main";
    String commit = "abc123";
    String pipeline = "build-and-test";
    String expectedResponse = "{\"id\": \"execution-123\"}";

    when(mockResponse.isSuccessful()).thenReturn(true);
    when(mockResponse.body()).thenReturn(mockResponseBody);
    when(mockResponseBody.string()).thenReturn(expectedResponse);

    // Act
    String result = client.triggerPipelineExecution(repo, branch, commit, pipeline);

    // Assert
    assertEquals(expectedResponse, result);

    // Verify request
    ArgumentCaptor<Request> requestCaptor = ArgumentCaptor.forClass(Request.class);
    verify(mockHttpClient).newCall(requestCaptor.capture());

    Request capturedRequest = requestCaptor.getValue();
    assertEquals("POST", capturedRequest.method());
    assertEquals(BACKEND_URL + "/api/pipeline/run", capturedRequest.url().toString());

    // Verify payload contains all parameters
    RequestBody requestBody = capturedRequest.body();
    Buffer buffer = new Buffer();
    requestBody.writeTo(buffer);
    String actualPayload = buffer.readUtf8();

    assertTrue(actualPayload.contains(repo));
    assertTrue(actualPayload.contains(branch));
    assertTrue(actualPayload.contains(commit));
    assertTrue(actualPayload.contains(pipeline));
  }

  @Test
  public void testTriggerPipelineExecution_Error() throws IOException {
    // Arrange
    String repo = "github.com/user/repo";
    String branch = "main";
    String commit = "abc123";
    String pipeline = "build-and-test";
    String errorMessage = "Internal Server Error";

    when(mockResponse.isSuccessful()).thenReturn(false);
    when(mockResponse.code()).thenReturn(500);
    when(mockResponse.body()).thenReturn(mockResponseBody);
    when(mockResponseBody.string()).thenReturn(errorMessage);

    // Act & Assert
    IOException exception = assertThrows(IOException.class, () ->
            client.triggerPipelineExecution(repo, branch, commit, pipeline));

    assertTrue(exception.getMessage().contains("500"));
    assertTrue(exception.getMessage().contains(errorMessage));
  }

  @Test
  public void testFetchPipelineReport_PipelineLevel() throws IOException {
    // Arrange
    String pipelineName = "build-pipeline";
    int runNumber = 42;
    String expectedUrl = BACKEND_URL + "/api/report/pipeline/build-pipeline/run/42";
    String expectedResponse = "{\"status\": \"success\", \"stages\": []}";

    when(mockResponse.isSuccessful()).thenReturn(true);
    when(mockResponse.body()).thenReturn(mockResponseBody);
    when(mockResponseBody.string()).thenReturn(expectedResponse);

    // Act
    String result = client.fetchPipelineReport(pipelineName, runNumber, null, null);

    // Assert
    assertEquals(expectedResponse, result);

    // Verify request
    ArgumentCaptor<Request> requestCaptor = ArgumentCaptor.forClass(Request.class);
    verify(mockHttpClient).newCall(requestCaptor.capture());

    Request capturedRequest = requestCaptor.getValue();
    assertEquals("GET", capturedRequest.method());
    assertEquals(expectedUrl, capturedRequest.url().toString());
  }

  @Test
  public void testFetchPipelineReport_StageLevel() throws IOException {
    // Arrange
    String pipelineName = "build-pipeline";
    int runNumber = 42;
    String stageName = "test";
    String expectedUrl = BACKEND_URL + "/api/report/pipeline/build-pipeline/run/42/stage/test";
    String expectedResponse = "{\"status\": \"success\", \"jobs\": []}";

    when(mockResponse.isSuccessful()).thenReturn(true);
    when(mockResponse.body()).thenReturn(mockResponseBody);
    when(mockResponseBody.string()).thenReturn(expectedResponse);

    // Act
    String result = client.fetchPipelineReport(pipelineName, runNumber, stageName, null);

    // Assert
    assertEquals(expectedResponse, result);

    // Verify request
    ArgumentCaptor<Request> requestCaptor = ArgumentCaptor.forClass(Request.class);
    verify(mockHttpClient).newCall(requestCaptor.capture());

    Request capturedRequest = requestCaptor.getValue();
    assertEquals("GET", capturedRequest.method());
    assertEquals(expectedUrl, capturedRequest.url().toString());
  }

  @Test
  public void testFetchPipelineReport_JobLevel() throws IOException {
    // Arrange
    String pipelineName = "build-pipeline";
    int runNumber = 42;
    String stageName = "test";
    String jobName = "unit-tests";
    String expectedUrl = BACKEND_URL + "/api/report/pipeline/build-pipeline/run/42/stage/test/job/unit-tests";
    String expectedResponse = "{\"status\": \"success\", \"logs\": \"...\"}";

    when(mockResponse.isSuccessful()).thenReturn(true);
    when(mockResponse.body()).thenReturn(mockResponseBody);
    when(mockResponseBody.string()).thenReturn(expectedResponse);

    // Act
    String result = client.fetchPipelineReport(pipelineName, runNumber, stageName, jobName);

    // Assert
    assertEquals(expectedResponse, result);

    // Verify request
    ArgumentCaptor<Request> requestCaptor = ArgumentCaptor.forClass(Request.class);
    verify(mockHttpClient).newCall(requestCaptor.capture());

    Request capturedRequest = requestCaptor.getValue();
    assertEquals("GET", capturedRequest.method());
    assertEquals(expectedUrl, capturedRequest.url().toString());
  }

  @Test
  public void testFetchPipelineReport_EmptyResponse() throws IOException {
    // Arrange
    String pipelineName = "build-pipeline";
    int runNumber = 42;

    // Setup mock with null body
    when(mockResponse.isSuccessful()).thenReturn(true);
    when(mockResponse.body()).thenReturn(null);

    // Act
    String result = client.fetchPipelineReport(pipelineName, runNumber, null, null);

    // Assert
    assertEquals("Error: Empty response", result);
  }

  @Test
  public void testValidatePipelineConfig_Success() throws IOException {
    // Arrange
    String repo = "github.com/user/repo";
    String file = "pipeline.yml";
    String expectedUrl = BACKEND_URL + "/api/pipeline/validate";
    String expectedResponse = "{\"valid\": true, \"messages\": []}";

    when(mockResponse.isSuccessful()).thenReturn(true);
    when(mockResponse.body()).thenReturn(mockResponseBody);
    when(mockResponseBody.string()).thenReturn(expectedResponse);

    // Act
    String result = client.validatePipelineConfig(repo, file);

    // Assert
    assertEquals(expectedResponse, result);

    // Verify request
    ArgumentCaptor<Request> requestCaptor = ArgumentCaptor.forClass(Request.class);
    verify(mockHttpClient).newCall(requestCaptor.capture());

    Request capturedRequest = requestCaptor.getValue();
    assertEquals("POST", capturedRequest.method());
    assertEquals(expectedUrl, capturedRequest.url().toString());

    // Verify payload contains all parameters
    RequestBody requestBody = capturedRequest.body();
    Buffer buffer = new Buffer();
    requestBody.writeTo(buffer);
    String actualPayload = buffer.readUtf8();

    assertTrue(actualPayload.contains(repo));
    assertTrue(actualPayload.contains(file));
  }

  @Test
  public void testValidatePipelineConfig_Error() throws IOException {
    // Arrange
    String repo = "github.com/user/repo";
    String file = "pipeline.yml";
    String errorMessage = "Invalid pipeline configuration";

    when(mockResponse.isSuccessful()).thenReturn(false);
    when(mockResponse.code()).thenReturn(400);
    when(mockResponse.body()).thenReturn(mockResponseBody);
    when(mockResponseBody.string()).thenReturn(errorMessage);

    // Act & Assert
    IOException exception = assertThrows(IOException.class, () ->
            client.validatePipelineConfig(repo, file));

    assertTrue(exception.getMessage().contains("400"));
    assertTrue(exception.getMessage().contains(errorMessage));
  }
}