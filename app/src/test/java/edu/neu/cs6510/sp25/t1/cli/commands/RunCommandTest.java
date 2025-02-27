package edu.neu.cs6510.sp25.t1.cli.commands;

import edu.neu.cs6510.sp25.t1.execution.DockerRunner;
import edu.neu.cs6510.sp25.t1.execution.PipelineExecutor;
import edu.neu.cs6510.sp25.t1.execution.StageExecutor;
import edu.neu.cs6510.sp25.t1.util.GitValidator;
import edu.neu.cs6510.sp25.t1.validation.YamlPipelineValidator;
import edu.neu.cs6510.sp25.t1.util.ErrorHandler;
import edu.neu.cs6510.sp25.t1.util.PipelineParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import picocli.CommandLine;
import java.lang.reflect.Field;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class RunCommandTest {
    private RunCommand runCommand;
    private YamlPipelineValidator mockValidator;
    private DockerRunner mockDockerRunner;
    private PipelineParser mockParser;
    private PipelineExecutor mockExecutor;

    @BeforeEach
    void setUp() {
        mockValidator = Mockito.mock(YamlPipelineValidator.class);
        mockDockerRunner = Mockito.mock(DockerRunner.class);
        mockParser = Mockito.mock(PipelineParser.class);
        mockExecutor = Mockito.mock(PipelineExecutor.class);

        runCommand = new RunCommand(mockValidator);
    }

    /** Tests successful execution with a valid pipeline file */
    @Test
    void testValidPipelineExecution() {
        when(mockValidator.validatePipeline(anyString())).thenReturn(true);

        String[] args = {"--local", "--file", "src/test/resources/valid_pipeline.yml"};
        CommandLine cmd = new CommandLine(runCommand);

        assertDoesNotThrow(() -> cmd.execute(args));
    }

    /** Ensures execution fails when no pipeline file is specified */
    @Test
    void testRunCommandFailsWithoutPipelineFile() {
        final CommandLine cmd = new CommandLine(runCommand);
        final int exitCode = cmd.execute("--local");

        assertNotEquals(0, exitCode, "Run command should fail if no pipeline file is specified.");
    }

    @Test
    void testExecuteLocalJobs() throws Exception {
        String testPipelineFile = "src/test/resources/valid_pipeline.yml";

        // Set pipelineFile field using reflection
        Field pipelineFileField = RunCommand.class.getDeclaredField("pipelineFile");
        pipelineFileField.setAccessible(true);
        pipelineFileField.set(runCommand, testPipelineFile);

        // Mock DockerRunner initialization
        when(mockDockerRunner.getDockerClient()).thenReturn(null); // Simulate Docker client

        // Mock PipelineParser behavior
        when(mockParser.getPipelineName()).thenReturn("Test Pipeline");
        when(mockParser.getStages()).thenReturn(Collections.emptyList());

        // Mock PipelineExecutor execution
        doNothing().when(mockExecutor).execute();

        // Use reflection to call the private method
        Method method = RunCommand.class.getDeclaredMethod("executeLocalJobs");
        method.setAccessible(true);

        // Ensure method executes without throwing exceptions
        assertDoesNotThrow(() -> method.invoke(runCommand));

        // Verify interactions
        verify(mockParser, times(1)).getPipelineName();
        verify(mockParser, times(1)).getStages();
        verify(mockExecutor, times(1)).execute();
    }

}
