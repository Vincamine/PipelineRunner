package edu.neu.cs6510.sp25.t1.cli.commands;

import edu.neu.cs6510.sp25.t1.util.GitValidator;
import edu.neu.cs6510.sp25.t1.validation.YamlPipelineValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import picocli.CommandLine;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.net.URISyntaxException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

class RunCommandTest {
    private RunCommand runCommand;

    @BeforeEach
    void setUp() {
        runCommand = new RunCommand();
    }

    /**
     * Helper method to get the full path of a test resource file.
     */
    private Path getTestResourcePath(String resource) throws URISyntaxException {
        final ClassLoader classLoader = getClass().getClassLoader();
        final URL resourceURL = classLoader.getResource("yaml/commands/execution_pipelines/" + resource);
        assertNotNull(resourceURL, "Test resource file not found: " + resource);
        return Paths.get(resourceURL.toURI());
    }

    /** Ensures that a valid pipeline executes successfully */
    @Test
    void testRunCommandExecutesSuccessfully() throws Exception {
        final Path pipelineFile = getTestResourcePath("valid_pipeline.yml");

        // Mocking the dependencies
        try (MockedStatic<GitValidator> gitValidatorMock = mockStatic(GitValidator.class);
                MockedStatic<Files> filesMock = mockStatic(Files.class)) {

            // Create a mock for YamlPipelineValidator
            final YamlPipelineValidator mockValidator = mock(YamlPipelineValidator.class);
            when(mockValidator.validatePipeline(pipelineFile.toString())).thenReturn(true); // ✅ Fix here

            // Ensure inside a Git repository
            gitValidatorMock.when(GitValidator::isGitRepository).thenReturn(true);

            // Mock file checks
            filesMock.when(() -> Files.exists(pipelineFile)).thenReturn(true);
            filesMock.when(() -> Files.isRegularFile(pipelineFile)).thenReturn(true);
            filesMock.when(() -> Files.isReadable(pipelineFile)).thenReturn(true);

            // Inject the mock validator into RunCommand (Modify RunCommand constructor if
            // needed)
            final RunCommand runCommand = new RunCommand(mockValidator);

            final CommandLine cmd = new CommandLine(runCommand);
            final int exitCode = cmd.execute("--local", "--file", pipelineFile.toString());

            assertEquals(0, exitCode, "Run command should execute successfully.");
        }
    }

    /** Ensures execution fails when no pipeline file is specified */
    @Test
    void testRunCommandFailsWithoutPipelineFile() {
        final CommandLine cmd = new CommandLine(runCommand);
        final int exitCode = cmd.execute("--local");

        assertNotEquals(0, exitCode, "Run command should fail if no pipeline file is specified.");
    }
}