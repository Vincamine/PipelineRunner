package edu.neu.cs6510.sp25.t1.cli.commands;

import edu.neu.cs6510.sp25.t1.cli.api.CliBackendClient;
import edu.neu.cs6510.sp25.t1.cli.commands.DryRunCommand;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URISyntaxException;
import java.nio.file.Paths;

class DryRunCommandTest {
    private CliBackendClient mockBackendClient;
    private DryRunCommand dryRunCommand;
    private String validPipelinePath;
    private String invalidPipelinePath;

    @BeforeEach
    void setUp() throws URISyntaxException {
        mockBackendClient = mock(CliBackendClient.class);
        dryRunCommand = new DryRunCommand(mockBackendClient);

        validPipelinePath = Paths.get(getClass().getClassLoader()
                .getResource(".pipelines/pipeline.yaml").toURI()).toString();

        invalidPipelinePath = Paths.get(getClass().getClassLoader()
                .getResource(".pipelines/invalid_pipeline.yaml").toURI()).toString();
    }

    @Test
    void testDryRunValidPipeline() throws Exception {
        String mockResponse = "execution: success";
        when(mockBackendClient.dryRunPipeline(validPipelinePath)).thenReturn(mockResponse);

        System.out.println("Mock response set: " + mockResponse);
        System.out.println("Calling dry-run with: " + validPipelinePath);

        CommandLine cmd = new CommandLine(dryRunCommand);
        int exitCode = cmd.execute("-f", validPipelinePath);

        System.out.println("Expected: 0, Actual: " + exitCode);
        assertEquals(0, exitCode);
    }

    @Test
    void testDryRunInvalidPipeline() throws Exception {
        String mockResponse = "Error: Invalid pipeline syntax";
        when(mockBackendClient.dryRunPipeline(invalidPipelinePath)).thenReturn(mockResponse);

        System.out.println("Mock response set: " + mockResponse);
        System.out.println("Calling dry-run with: " + invalidPipelinePath);

        CommandLine cmd = new CommandLine(dryRunCommand);
        int exitCode = cmd.execute("-f", invalidPipelinePath);

        System.out.println("Expected: 3, Actual: " + exitCode);
        assertEquals(3, exitCode);
    }

    @Test
    void testDryRunHandlesExceptions() throws Exception {
        when(mockBackendClient.dryRunPipeline(anyString()))
                .thenThrow(new RuntimeException("Backend error"));

        System.out.println("Simulating an exception for dry-run");
        System.out.println("Calling dry-run with: " + validPipelinePath);

        CommandLine cmd = new CommandLine(dryRunCommand);
        int exitCode = cmd.execute("-f", validPipelinePath);

        System.out.println("Expected: 1, Actual: " + exitCode);
        assertEquals(1, exitCode);
    }
}
