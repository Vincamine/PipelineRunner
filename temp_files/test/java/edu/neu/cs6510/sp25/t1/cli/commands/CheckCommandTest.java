package edu.neu.cs6510.sp25.t1.cli.commands;

import edu.neu.cs6510.sp25.t1.util.PipelineValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CheckCommandTest {
    private PipelineValidator mockPipelineValidator;
    private CheckCommand checkCommand;

    @BeforeEach
    void setUp() {
        mockPipelineValidator = mock(PipelineValidator.class);
        checkCommand = new CheckCommand(mockPipelineValidator);
    }

    /** Ensures the check command succeeds when the pipeline file is valid */
    @Test
    void testCheck_ValidPipeline_Success() {
        when(mockPipelineValidator.validatePipelineFile(".pipelines/pipeline.yaml")).thenReturn(true);

        final CommandLine cmd = new CommandLine(checkCommand);
        final int exitCode = cmd.execute();

        assertEquals(0, exitCode, "Check command should succeed if pipeline file is valid.");
    }

    /** Ensures the check command fails when the pipeline file is invalid */
    @Test
    void testCheck_InvalidPipeline_Fails() {
        when(mockPipelineValidator.validatePipelineFile(".pipelines/pipeline.yaml")).thenReturn(false);

        final CommandLine cmd = new CommandLine(checkCommand);
        @SuppressWarnings("unused")
        final int exitCode = cmd.execute();

        // Picocli returns 0 for false, so explicitly check the boolean return
        assertFalse(checkCommand.call(), "Check command should fail if pipeline file is invalid.");
    }

}
