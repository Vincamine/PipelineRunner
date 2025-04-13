package edu.neu.cs6510.sp25.t1.cli.commands;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.neu.cs6510.sp25.t1.cli.api.CliBackendClient;
import edu.neu.cs6510.sp25.t1.common.logging.PipelineLogger;

/**
 * Unit tests for ReportCommand class
 */
@ExtendWith(MockitoExtension.class)
public class ReportCommandTest {

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private CliBackendClient backendClient;

    @InjectMocks
    private ReportCommand reportCommand;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    @BeforeEach
    public void setup() throws Exception {
        MockitoAnnotations.openMocks(this);
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));

        // Use reflection to inject backendClient
        Field backendClientField = ReportCommand.class.getDeclaredField("backendClient");
        backendClientField.setAccessible(true);
        backendClientField.set(reportCommand, backendClient);
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    // Helper method to set private fields using reflection
    private void setPrivateField(String fieldName, Object value) throws Exception {
        Field field = ReportCommand.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(reportCommand, value);
    }

    // Helper method to invoke private methods using reflection
    private Object invokePrivateMethod(String methodName, Class<?>[] paramTypes, Object[] args) throws Exception {
        Method method = ReportCommand.class.getDeclaredMethod(methodName, paramTypes);
        method.setAccessible(true);
        return method.invoke(reportCommand, args);
    }

    @Test
    public void testJobWithoutStage_ShouldReturnError() throws Exception {
        // Arrange
        setPrivateField("pipelineName", "test-pipeline");
        setPrivateField("jobName", "test-job");
        setPrivateField("stageName", null);

        // Act
        Integer result = reportCommand.call();

        // Assert
        assertEquals(1, result);
        assertTrue(errContent.toString().contains("--stage parameter is required when using --job"));
    }

    @Test
    public void testFetchPipelineHistory() throws Exception {
        // Arrange
        setPrivateField("pipelineName", "test-pipeline");
        setPrivateField("format", "text");

        String mockApiResponse = createMockPipelineHistoryResponse();
        when(backendClient.fetchPipelineReport(eq("test-pipeline"), eq(-1), eq(null), eq(null)))
                .thenReturn(mockApiResponse);

        // Act
        Integer result = reportCommand.call();

        // Assert
        assertEquals(0, result);
        verify(backendClient).fetchPipelineReport("test-pipeline", -1, null, null);
        assertTrue(outContent.toString().contains("pipeline"));
    }

    @Test
    public void testFetchPipelineRunSummary() throws Exception {
        // Arrange
        setPrivateField("pipelineName", "test-pipeline");
        setPrivateField("runNumber", 42);
        setPrivateField("format", "text");

        String mockApiResponse = createMockPipelineRunResponse();
        when(backendClient.fetchPipelineReport(eq("test-pipeline"), eq(42), eq(null), eq(null)))
                .thenReturn(mockApiResponse);

        // Act
        Integer result = reportCommand.call();

        // Assert
        assertEquals(0, result);
        verify(backendClient).fetchPipelineReport("test-pipeline", 42, null, null);
        assertTrue(outContent.toString().contains("Pipeline Name: test-pipeline"));
        assertTrue(outContent.toString().contains("Run Number: 42"));
    }

    @Test
    public void testFetchStageSummary() throws Exception {
        // Arrange
        setPrivateField("pipelineName", "test-pipeline");
        setPrivateField("runNumber", 42);
        setPrivateField("stageName", "build");
        setPrivateField("format", "text");

        String mockApiResponse = createMockStageResponse();
        when(backendClient.fetchPipelineReport(eq("test-pipeline"), eq(42), eq("build"), eq(null)))
                .thenReturn(mockApiResponse);

        // Act
        Integer result = reportCommand.call();

        // Assert
        assertEquals(0, result);
        verify(backendClient).fetchPipelineReport("test-pipeline", 42, "build", null);
        assertTrue(outContent.toString().contains("Stage Name: build"));
    }

    @Test
    public void testFetchJobSummary() throws Exception {
        // Arrange
        setPrivateField("pipelineName", "test-pipeline");
        setPrivateField("runNumber", 42);
        setPrivateField("stageName", "build");
        setPrivateField("jobName", "compile");
        setPrivateField("format", "text");

        String mockApiResponse = createMockJobResponse();
        when(backendClient.fetchPipelineReport(eq("test-pipeline"), eq(42), eq("build"), eq("compile")))
                .thenReturn(mockApiResponse);

        // Act
        Integer result = reportCommand.call();

        // Assert
        assertEquals(0, result);
        verify(backendClient).fetchPipelineReport("test-pipeline", 42, "build", "compile");
        assertTrue(outContent.toString().contains("Job Name: compile"));
    }

    @Test
    public void testFetchStageHistory() throws Exception {
        // Arrange
        setPrivateField("pipelineName", "test-pipeline");
        setPrivateField("stageName", "build");
        setPrivateField("format", "text");

        String mockApiResponse = createMockStageHistoryResponse();
        when(backendClient.fetchPipelineReport(eq("test-pipeline"), isNull(), eq("build"), eq(null)))
                .thenReturn(mockApiResponse);

        // Act
        Integer result = reportCommand.call();

        // Assert
        assertEquals(0, result);
        verify(backendClient).fetchPipelineReport("test-pipeline", null, "build", null);
        assertTrue(outContent.toString().contains("build"));
    }

    @Test
    public void testFormatResponseJson() throws Exception {
        // Arrange
        setPrivateField("format", "json");

        // Act
        String result = (String) invokePrivateMethod("formatResponse",
                new Class[] { String.class },
                new Object[] { createMockPipelineRunResponse() });

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("\"name\""));
        assertTrue(result.contains("\"runNumber\""));
    }

    @Test
    public void testFormatResponseText() throws Exception {
        // Arrange
        setPrivateField("format", "text");

        // Act
        String result = (String) invokePrivateMethod("formatResponse",
                new Class[] { String.class },
                new Object[] { createMockPipelineRunResponse() });

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("Pipeline Name:"));
        assertTrue(result.contains("Run Number:"));
    }

    @Test
    public void testFormatTimestamp() throws Exception {
        // Act
        String formattedTimestamp = (String) invokePrivateMethod("formatTimestamp",
                new Class[] { String.class },
                new Object[] { "2023-04-01T14:30:00Z" });

        String nullTimestamp = (String) invokePrivateMethod("formatTimestamp",
                new Class[] { String.class },
                new Object[] { (Object) null });

        String emptyTimestamp = (String) invokePrivateMethod("formatTimestamp",
                new Class[] { String.class },
                new Object[] { "" });

        // Assert
        assertNotNull(formattedTimestamp);
        assertTrue(formattedTimestamp.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}"));
        assertEquals("N/A", nullTimestamp);
        assertEquals("N/A", emptyTimestamp);
    }

    @Test
    public void testApiRequestFailure() throws Exception {
        // Arrange
        setPrivateField("pipelineName", "test-pipeline");
        when(backendClient.fetchPipelineReport(anyString(), any(), any(), any()))
                .thenThrow(new IOException("Network error"));

        // Act
        Integer result = reportCommand.call();

        // Assert
        assertEquals(1, result);
    }


    // Helper methods to create mock responses
    private String createMockPipelineHistoryResponse() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode arrayNode = mapper.createArrayNode();

        for (int i = 1; i <= 3; i++) {
            ObjectNode pipelineNode = createBasicPipelineNode(mapper, i);
            arrayNode.add(pipelineNode);
        }

        return mapper.writeValueAsString(arrayNode);
    }

    private String createMockPipelineRunResponse() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode pipelineNode = createBasicPipelineNode(mapper, 42);

        // Add stages
        ArrayNode stagesArray = mapper.createArrayNode();
        stagesArray.add(createStageNode(mapper, "build", "success"));
        stagesArray.add(createStageNode(mapper, "test", "success"));
        pipelineNode.set("stages", stagesArray);

        return mapper.writeValueAsString(pipelineNode);
    }

    private String createMockStageResponse() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode stageNode = createStageNode(mapper, "build", "success");

        // Add jobs
        ArrayNode jobsArray = mapper.createArrayNode();
        jobsArray.add(createJobNode(mapper, "compile", "success"));
        jobsArray.add(createJobNode(mapper, "package", "success"));
        stageNode.set("jobs", jobsArray);

        return mapper.writeValueAsString(stageNode);
    }

    private String createMockJobResponse() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode jobNode = createJobNode(mapper, "compile", "success");

        // Add executions
        ArrayNode executionsArray = mapper.createArrayNode();
        executionsArray.add(createExecutionNode(mapper, "1", "success"));
        jobNode.set("executions", executionsArray);

        return mapper.writeValueAsString(jobNode);
    }

    private String createMockStageHistoryResponse() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode arrayNode = mapper.createArrayNode();

        for (int i = 1; i <= 3; i++) {
            ObjectNode stageNode = createStageNode(mapper, "build", i % 2 == 0 ? "success" : "failed");
            arrayNode.add(stageNode);
        }

        return mapper.writeValueAsString(arrayNode);
    }

    private ObjectNode createBasicPipelineNode(ObjectMapper mapper, int runNumber) {
        ObjectNode pipelineNode = mapper.createObjectNode();
        pipelineNode.put("id", "pipeline-" + runNumber);
        pipelineNode.put("name", "test-pipeline");
        pipelineNode.put("runNumber", runNumber);
        pipelineNode.put("commitHash", "abc123def456");
        pipelineNode.put("status", "success");
        pipelineNode.put("startTime", "2023-04-01T12:00:00Z");
        pipelineNode.put("completionTime", "2023-04-01T12:15:00Z");
        return pipelineNode;
    }

    private ObjectNode createStageNode(ObjectMapper mapper, String name, String status) {
        ObjectNode stageNode = mapper.createObjectNode();
        stageNode.put("id", "stage-" + name);
        stageNode.put("name", name);
        stageNode.put("status", status);
        stageNode.put("startTime", "2023-04-01T12:01:00Z");
        stageNode.put("completionTime", "2023-04-01T12:10:00Z");
        return stageNode;
    }

    private ObjectNode createJobNode(ObjectMapper mapper, String name, String status) {
        ObjectNode jobNode = mapper.createObjectNode();
        jobNode.put("name", name);
        jobNode.put("pipelineName", "test-pipeline");
        jobNode.put("runNumber", 42);
        jobNode.put("stageName", "build");
        jobNode.put("status", status);
        jobNode.put("startTime", "2023-04-01T12:02:00Z");
        jobNode.put("completionTime", "2023-04-01T12:08:00Z");
        return jobNode;
    }

    private ObjectNode createExecutionNode(ObjectMapper mapper, String id, String status) {
        ObjectNode executionNode = mapper.createObjectNode();
        executionNode.put("id", id);
        executionNode.put("status", status);
        executionNode.put("allowFailure", false);
        executionNode.put("startTime", "2023-04-01T12:03:00Z");
        executionNode.put("completionTime", "2023-04-01T12:07:00Z");
        return executionNode;
    }
}