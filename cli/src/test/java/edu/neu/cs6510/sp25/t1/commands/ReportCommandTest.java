package edu.neu.cs6510.sp25.t1.commands;

import edu.neu.cs6510.sp25.t1.api.BackendClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import picocli.CommandLine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * Unit tests for ReportCommand.
 */
class ReportCommandTest {
    private BackendClient backendClient;
    private CommandLine cmd;
    private ByteArrayOutputStream outputStream;

    @BeforeEach
    void setUp() {
        backendClient = Mockito.mock(BackendClient.class);
        cmd = new CommandLine(new ReportCommand(backendClient));

        // Redirect system output for testing
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        System.setErr(new PrintStream(outputStream));
    }

    @Test
    void testReportCommand_SuccessPlaintext() throws Exception {
        String mockResponse = "Execution History:\nPipeline: build\nRun 1: Success\nRun 2: Failed";
        when(backendClient.getPipelineExecutions(eq("build"), eq("plaintext"))).thenReturn(mockResponse);

        int exitCode = cmd.execute("--pipeline", "build", "--output", "plaintext");

        assertEquals(0, exitCode);
        String output = outputStream.toString().trim();
        assertEquals("Execution History:\nPipeline: build\nRun 1: Success\nRun 2: Failed", output);
    }

    @Test
    void testReportCommand_SuccessJson() throws Exception {
        String mockResponse = "{ \"pipeline\": \"build\", \"executions\": [ { \"runNumber\": 1, \"status\": \"success\" } ] }";
        when(backendClient.getPipelineExecutions(eq("build"), eq("json"))).thenReturn(mockResponse);

        int exitCode = cmd.execute("--pipeline", "build", "--output", "json");

        assertEquals(0, exitCode);
        String output = outputStream.toString().trim();
        assertEquals("Execution History:\n" + mockResponse, output);
    }

    @Test
    void testReportCommand_SuccessYaml() throws Exception {
        String mockResponse = "pipeline: build\nexecutions:\n  - runNumber: 1\n    status: success";
        when(backendClient.getPipelineExecutions(eq("build"), eq("yaml"))).thenReturn(mockResponse);

        int exitCode = cmd.execute("--pipeline", "build", "--output", "yaml");

        assertEquals(0, exitCode);
        String output = outputStream.toString().trim();
        assertEquals("Execution History:\n" + mockResponse, output);
    }

    @Test
    void testReportCommand_MissingPipelineArgument() {
        int exitCode = cmd.execute("--output", "json");

        assertEquals(2, exitCode); // Picocli returns 2 for missing required arguments
        String output = outputStream.toString().trim();
        assert output.contains("Missing required option: '--pipeline=<pipeline>'");
    }

    @Test
    void testReportCommand_BackendError() throws Exception {
        when(backendClient.getPipelineExecutions(anyString(), anyString()))
                .thenThrow(new RuntimeException("Backend unavailable"));

        int exitCode = cmd.execute("--pipeline", "build", "--output", "json");

        assertEquals(1, exitCode);
        String output = outputStream.toString().trim();
        assert output.contains("Error fetching pipeline execution history: Backend unavailable");
    }
}
