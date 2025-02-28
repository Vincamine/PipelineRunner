package edu.neu.cs6510.sp25.t1.commands;

import edu.neu.cs6510.sp25.t1.api.CliBackendClient;
import edu.neu.cs6510.sp25.t1.api.PipelineCheckResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CheckCommandTest {
    private CliBackendClient mockBackendClient;
    private CheckCommand checkCommand;
    private String validPipelinePath;
    private String invalidPipelinePath;

    @BeforeEach
    void setUp() throws URISyntaxException {
        mockBackendClient = mock(CliBackendClient.class);
        checkCommand = new CheckCommand(mockBackendClient);

        validPipelinePath = Paths.get(getClass().getClassLoader().getResource(".pipelines/pipeline.yaml").toURI()).toString();
        invalidPipelinePath = Paths.get(getClass().getClassLoader().getResource(".pipelines/invalid_pipeline.yaml").toURI()).toString();
    }

    @Test
    void testCheckValidPipeline() throws Exception {
        when(mockBackendClient.checkPipelineConfig(validPipelinePath))
                .thenReturn(new PipelineCheckResponse(true, null));

        System.out.println("Mock response set: Pipeline is valid.");
        System.out.println("Calling check command with: " + validPipelinePath);

        CommandLine cmd = new CommandLine(checkCommand);
        int exitCode = cmd.execute(validPipelinePath);  // ✅ FIX: Remove "-f"

        System.out.println("Expected: 0, Actual: " + exitCode);
        assertEquals(0, exitCode);
    }

    @Test
    void testCheckInvalidPipeline() throws Exception {
        when(mockBackendClient.checkPipelineConfig(invalidPipelinePath))
                .thenReturn(new PipelineCheckResponse(false, List.of("Syntax error")));

        System.out.println("Mock response set: Syntax error in pipeline.");
        System.out.println("Calling check command with: " + invalidPipelinePath);

        CommandLine cmd = new CommandLine(checkCommand);
        int exitCode = cmd.execute(invalidPipelinePath);  // ✅ FIX: Remove "-f"

        System.out.println("Expected: 3, Actual: " + exitCode);
        assertEquals(3, exitCode);
    }

    @Test
    void testCheckCommandHandlesExceptions() throws Exception {
        when(mockBackendClient.checkPipelineConfig(validPipelinePath))
                .thenThrow(new RuntimeException("Backend error"));

        System.out.println("Simulating backend failure for check command.");
        System.out.println("Calling check command with: " + validPipelinePath);

        CommandLine cmd = new CommandLine(checkCommand);
        int exitCode = cmd.execute(validPipelinePath);  // ✅ FIX: Remove "-f"

        System.out.println("Expected: 1, Actual: " + exitCode);
        assertEquals(1, exitCode);
    }
}
