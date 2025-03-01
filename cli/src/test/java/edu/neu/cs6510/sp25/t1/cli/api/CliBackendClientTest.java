package edu.neu.cs6510.sp25.t1.cli.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import edu.neu.cs6510.sp25.t1.common.api.PipelineCheckResponse;
import edu.neu.cs6510.sp25.t1.common.api.RunPipelineRequest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CliBackendClientTest {

    private MockWebServer mockWebServer;
    private CliBackendClient client;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final YAMLMapper yamlMapper = new YAMLMapper();

    @BeforeAll
    void setUpServer() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @BeforeEach
    void setUpClient() {
        String baseUrl = mockWebServer.url("/api/v1").toString();
        client = new CliBackendClient(baseUrl);
    }

    @AfterAll
    void tearDownServer() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void testCheckPipelineConfig_Success() throws IOException {
        PipelineCheckResponse mockResponse = new PipelineCheckResponse(true, null);
        mockWebServer.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(mockResponse))
                .setResponseCode(200));

        PipelineCheckResponse response = client.checkPipelineConfig("config.yml");

        assertNotNull(response);
        assertTrue(response.isValid());
    }

    @Test
    void testCheckPipelineConfig_Failure() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(400).setBody("Invalid config"));

        IOException exception = assertThrows(IOException.class, () -> client.checkPipelineConfig("config.yml"));
        assertTrue(exception.getMessage().contains("API Error"));
    }

    @Test
    void testDryRunPipeline_Success() throws IOException {
        String mockExecutionPlan = "Step1 -> Step2 -> Step3";
        mockWebServer.enqueue(new MockResponse()
                .setBody(mockExecutionPlan)
                .setResponseCode(200));

        String response = client.dryRunPipeline("config.yml");

        assertNotNull(response);
        assertEquals(mockExecutionPlan, response);
    }

    @Test
    void testDryRunPipeline_Failure() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(500).setBody("Server Error"));

        IOException exception = assertThrows(IOException.class, () -> client.dryRunPipeline("config.yml"));
        assertTrue(exception.getMessage().contains("API Error"));
    }

    @Test
    void testRunPipeline_Success() throws IOException {
        RunPipelineRequest request = new RunPipelineRequest("pipeline-name");
        mockWebServer.enqueue(new MockResponse()
                .setBody("{\"status\":\"RUNNING\"}")
                .setResponseCode(200));

        String response = client.runPipeline(request);

        assertNotNull(response);
        assertTrue(response.contains("RUNNING"));
    }

    @Test
    void testRunPipeline_Failure() {
        RunPipelineRequest request = new RunPipelineRequest("pipeline-name");
        mockWebServer.enqueue(new MockResponse().setResponseCode(400).setBody("Invalid request"));

        IOException exception = assertThrows(IOException.class, () -> client.runPipeline(request));
        assertTrue(exception.getMessage().contains("API Error"));
    }

    @Test
    void testGetPipelineExecutions_Success() throws IOException {
        String mockExecutions = "[{\"pipeline\":\"test-pipeline\",\"status\":\"SUCCESS\"}]";
        mockWebServer.enqueue(new MockResponse()
                .setBody(mockExecutions)
                .setResponseCode(200));

        String response = client.getPipelineExecutions("test-pipeline", "json");

        assertNotNull(response);
        assertTrue(response.contains("test-pipeline"));
    }

    @Test
    void testGetPipelineExecutions_Failure() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(404).setBody("Not found"));

        IOException exception = assertThrows(IOException.class, () -> client.getPipelineExecutions("invalid-pipeline", "json"));
        assertTrue(exception.getMessage().contains("API Error"));
    }

    @Test
    void testGetAllPipelines_Success() throws IOException {
        String mockPipelines = "[{\"name\":\"pipeline1\"},{\"name\":\"pipeline2\"}]";
        mockWebServer.enqueue(new MockResponse()
                .setBody(mockPipelines)
                .setResponseCode(200));

        String response = client.getAllPipelines();

        assertNotNull(response);
        assertTrue(response.contains("pipeline1"));
        assertTrue(response.contains("pipeline2"));
    }

    @Test
    void testGetAllPipelines_Failure() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(500).setBody("Internal Server Error"));

        IOException exception = assertThrows(IOException.class, () -> client.getAllPipelines());
        assertTrue(exception.getMessage().contains("API Error"));
    }
}
