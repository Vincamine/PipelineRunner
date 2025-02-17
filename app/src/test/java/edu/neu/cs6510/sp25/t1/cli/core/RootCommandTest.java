package edu.neu.cs6510.sp25.t1.cli.core;

import edu.neu.cs6510.sp25.t1.util.GitValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import picocli.CommandLine;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RootCommandTest {
    private RootCommand rootCommand;

    @BeforeEach
    void setUp() {
        rootCommand = new RootCommand();
    }

    @Test
    void testHelpCommand() {
        CommandLine cmd = new CommandLine(rootCommand);
        int exitCode = cmd.execute("--help");
        assertEquals(0, exitCode, "Help command should execute successfully.");
    }

    @Test
    void testVerboseFlag() {
        CommandLine cmd = new CommandLine(rootCommand);
        int exitCode = cmd.execute("--verbose");
        assertEquals(0, exitCode, "Verbose mode should not cause errors.");
    }

    @Test
    void testRunCommandExecutesSuccessfully() {
        // Mock GitValidator so it doesn’t check for an actual Git repo
        try (MockedStatic<GitValidator> gitValidatorMock = mockStatic(GitValidator.class)) {
            gitValidatorMock.when(GitValidator::validateGitRepo).thenReturn(true);

            // Mock file existence check
            Path mockFile = Paths.get("mock-pipeline.yaml");
            try (MockedStatic<Files> filesMock = mockStatic(Files.class)) {
                filesMock.when(() -> Files.exists(mockFile)).thenReturn(true);
                filesMock.when(() -> Files.isRegularFile(mockFile)).thenReturn(true);
                filesMock.when(() -> Files.isReadable(mockFile)).thenReturn(true);

                CommandLine cmd = new CommandLine(rootCommand);
                int exitCode = cmd.execute("--run", "-f", "mock-pipeline.yaml");
                assertEquals(0, exitCode, "Run command should execute successfully.");
            }
        }
    }

    @Test
    void testRunCommandFailsWithoutFile() {
        try (MockedStatic<GitValidator> gitValidatorMock = mockStatic(GitValidator.class)) {
            gitValidatorMock.when(GitValidator::validateGitRepo).thenReturn(true);

            CommandLine cmd = new CommandLine(rootCommand);
            int exitCode = cmd.execute("--run");
            assertNotEquals(0, exitCode, "Run command should fail without a file.");
        }
    }

    @Test
    void testRunCommandFailsIfFileDoesNotExist() {
        try (MockedStatic<GitValidator> gitValidatorMock = mockStatic(GitValidator.class)) {
            gitValidatorMock.when(GitValidator::validateGitRepo).thenReturn(true);

            Path mockFile = Paths.get("nonexistent.yaml");
            try (MockedStatic<Files> filesMock = mockStatic(Files.class)) {
                filesMock.when(() -> Files.exists(mockFile)).thenReturn(false);

                CommandLine cmd = new CommandLine(rootCommand);
                int exitCode = cmd.execute("--run", "-f", "nonexistent.yaml");
                assertNotEquals(0, exitCode, "Run command should fail if the file does not exist.");
            }
        }
    }
}
