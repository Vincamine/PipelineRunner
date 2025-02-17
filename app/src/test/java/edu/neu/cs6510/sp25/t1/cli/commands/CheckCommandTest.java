package edu.neu.cs6510.sp25.t1.cli.commands;

import edu.neu.cs6510.sp25.t1.util.PipelineValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class CheckCommandTest {

    private CheckCommand checkCommand;
    private PipelineValidator pipelineValidator;

    @BeforeEach
    void setUp() {
        pipelineValidator = mock(PipelineValidator.class);
        checkCommand = new CheckCommand();
    }

    @Test
    void testCheck_ValidPipeline_Success() {
        doReturn(true).when(pipelineValidator).validatePipelineFile(anyString());

        final int exitCode = new CommandLine(checkCommand).execute("-f", "valid_pipeline.yaml");
        assertEquals(0, exitCode);
    }

    @Test
    void testCheck_InvalidPipeline_Failure() {
        doReturn(false).when(pipelineValidator).validatePipelineFile(anyString());

        final int exitCode = new CommandLine(checkCommand).execute("-f", "invalid_pipeline.yaml");
        assertNotEquals(0, exitCode);
    }
}
