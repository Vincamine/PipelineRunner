package edu.neu.cs6510.sp25.t1.cli.commands;

import edu.neu.cs6510.sp25.t1.model.ApiResponse;
import edu.neu.cs6510.sp25.t1.validation.YamlPipelineValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import picocli.CommandLine;

import java.io.IOException;
import java.net.HttpURLConnection;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class RunCommandTest {

    private RunCommand runCommand;
    private YamlPipelineValidator validator;

    @BeforeEach
    void setUp() {
        validator = mock(YamlPipelineValidator.class);
        runCommand = spy(new RunCommand());
    }

    @Test
    void testRun_ValidPipeline_Success() throws IOException {
        doReturn("dummy config").when(runCommand).readPipelineConfig(anyString());
        doReturn(true).when(validator).validatePipeline(anyString());
        doReturn(new ApiResponse(HttpURLConnection.HTTP_OK, "Pipeline executed")).when(runCommand).sendRequestToApi(any());

        final int exitCode = new CommandLine(runCommand).execute();
        assertEquals(0, exitCode);
    }

    @Test
    void testRun_InvalidPipeline_Failure() {
        doReturn("dummy config").when(runCommand).readPipelineConfig(anyString());
        doReturn(false).when(validator).validatePipeline(anyString());

        final int exitCode = new CommandLine(runCommand).execute();
        assertNotEquals(0, exitCode);
    }

    @Test
    void testRun_ApiFailure() {
        doReturn("dummy config").when(runCommand).readPipelineConfig(anyString());
        doReturn(true).when(validator).validatePipeline(anyString());
        doReturn(new ApiResponse(HttpURLConnection.HTTP_INTERNAL_ERROR, "API failure")).when(runCommand).sendRequestToApi(any());

        final int exitCode = new CommandLine(runCommand).execute();
        assertNotEquals(0, exitCode);
    }
}
