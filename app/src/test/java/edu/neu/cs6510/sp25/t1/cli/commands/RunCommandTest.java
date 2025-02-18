package edu.neu.cs6510.sp25.t1.cli.commands;

import edu.neu.cs6510.sp25.t1.util.GitValidator;
import edu.neu.cs6510.sp25.t1.validation.YamlPipelineValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;
import picocli.CommandLine;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RunCommandTest {
    private RunCommand runCommand;

    @BeforeEach
    void setUp() {
        runCommand = new RunCommand();
    }

    /** Ensures that a valid pipeline executes successfully */
    @Test
    void testRunCommandExecutesSuccessfully(@TempDir Path tempDir) {
        final Path pipelineFile = tempDir.resolve("valid.yaml");

        try (MockedStatic<GitValidator> gitValidatorMock = mockStatic(GitValidator.class);
             MockedStatic<YamlPipelineValidator> yamlValidatorMock = mockStatic(YamlPipelineValidator.class);
             MockedStatic<Files> filesMock = mockStatic(Files.class)) {

            gitValidatorMock.when(GitValidator::isGitRepository).thenReturn(true);
            yamlValidatorMock.when(() -> new YamlPipelineValidator().validatePipeline(pipelineFile.toString()))
                             .thenReturn(true);

            filesMock.when(() -> Files.exists(pipelineFile)).thenReturn(true);
            filesMock.when(() -> Files.isRegularFile(pipelineFile)).thenReturn(true);
            filesMock.when(() -> Files.isReadable(pipelineFile)).thenReturn(true);

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

    /** Ensures execution fails when the specified file does not exist */
    @Test
    void testRunCommandFailsIfFileDoesNotExist() {
        try (MockedStatic<GitValidator> gitValidatorMock = mockStatic(GitValidator.class);
            final MockedStatic<Files> filesMock = mockStatic(Files.class)) {

            gitValidatorMock.when(GitValidator::isGitRepository).thenReturn(true);
            final Path nonexistentFile = Paths.get("nonexistent.yaml");

            filesMock.when(() -> Files.exists(nonexistentFile)).thenReturn(false);

            final CommandLine cmd = new CommandLine(runCommand);
            final int exitCode = cmd.execute("--local", "--file", "nonexistent.yaml");

            assertNotEquals(0, exitCode, "Run command should fail if file does not exist.");
        }
    }

    /** Ensures execution fails when the file is unreadable */
    @Test
    void testRunCommandFailsIfFileIsUnreadable(@TempDir Path tempDir) {
        final Path unreadableFile = tempDir.resolve("unreadable.yaml");

        try (MockedStatic<GitValidator> gitValidatorMock = mockStatic(GitValidator.class);
            final MockedStatic<Files> filesMock = mockStatic(Files.class)) {

            gitValidatorMock.when(GitValidator::isGitRepository).thenReturn(true);

            filesMock.when(() -> Files.exists(unreadableFile)).thenReturn(true);
            filesMock.when(() -> Files.isRegularFile(unreadableFile)).thenReturn(true);
            filesMock.when(() -> Files.isReadable(unreadableFile)).thenReturn(false);

            final CommandLine cmd = new CommandLine(runCommand);
            final int exitCode = cmd.execute("--local", "--file", unreadableFile.toString());

            assertNotEquals(0, exitCode, "Run command should fail if file is not readable.");
        }
    }

    /** Ensures execution fails if not inside a Git repository */
    @Test
    void testRunCommandFailsIfNotInGitRepo() {
        try (MockedStatic<GitValidator> gitValidatorMock = mockStatic(GitValidator.class)) {
            gitValidatorMock.when(GitValidator::isGitRepository).thenReturn(false);

            final CommandLine cmd = new CommandLine(runCommand);
            final int exitCode = cmd.execute("--local", "--file", "valid.yaml");

            assertNotEquals(0, exitCode, "Run command should fail if not inside a Git repository.");
        }
    }

    /** Ensures that remote execution fails since it is not supported */
    @Test
    void testRunCommandFailsForRemoteExecution() {
        final CommandLine cmd = new CommandLine(runCommand);
        final int exitCode = cmd.execute("--file", "valid.yaml");

        assertNotEquals(0, exitCode, "Run command should fail for remote execution.");
    }
}
