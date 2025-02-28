package edu.neu.cs6510.sp25.t1.commands;

import edu.neu.cs6510.sp25.t1.api.RunPipelineRequest;
import edu.neu.cs6510.sp25.t1.api.BackendClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class RunCommandTest {
    private BackendClient mockBackendClient;
    private RunCommand runCommand;

    @BeforeEach
    void setUp() {
        mockBackendClient = mock(BackendClient.class);
        runCommand = new RunCommand(mockBackendClient);
    }

    @Test
    void testRunValidPipeline() throws Exception {
        String mockResponse = "Pipeline execution started successfully.";
        when(mockBackendClient.runPipeline(any(RunPipelineRequest.class))).thenReturn(mockResponse);

        System.out.println("Mock response set: " + mockResponse);
        System.out.println("Calling run command with: --pipeline testPipeline");

        CommandLine cmd = new CommandLine(runCommand);
        int exitCode = cmd.execute("--pipeline", "testPipeline");

        System.out.println("Expected: 0, Actual: " + exitCode);
        verify(mockBackendClient, times(1)).runPipeline(any(RunPipelineRequest.class));
        assertEquals(0, exitCode);
    }

    @Test
    void testRunMissingPipelineArgument() {
        System.out.println("Calling run command without --pipeline");

        CommandLine cmd = new CommandLine(runCommand);
        int exitCode = cmd.execute();  // No arguments

        System.out.println("Expected: 2 (Invalid arguments), Actual: " + exitCode);
        assertEquals(2, exitCode);
    }

    @Test
    void testRunHandlesExceptions() throws Exception {
        when(mockBackendClient.runPipeline(any(RunPipelineRequest.class)))
                .thenThrow(new RuntimeException("Backend failure"));

        System.out.println("Simulating backend failure for run command.");
        System.out.println("Calling run command with: --pipeline testPipeline");

        CommandLine cmd = new CommandLine(runCommand);
        int exitCode = cmd.execute("--pipeline", "testPipeline");

        System.out.println("Expected: 1, Actual: " + exitCode);
        assertEquals(1, exitCode);
    }
}
