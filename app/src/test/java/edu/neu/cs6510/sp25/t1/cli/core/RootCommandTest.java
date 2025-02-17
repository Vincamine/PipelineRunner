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
        final CommandLine cmd = new CommandLine(rootCommand);
        final int exitCode = cmd.execute("--help");
        assertEquals(0, exitCode, "Help command should execute successfully.");
    }

    @Test
    void testVerboseFlag() {
        final CommandLine cmd = new CommandLine(rootCommand);
        final int exitCode = cmd.execute("--verbose");
        assertEquals(0, exitCode, "Verbose mode should not cause errors.");
    }

    @Test
    void testRunCommandExecutesSuccessfully() {
        // Mock GitValidator to prevent actual Git repo checks
        try (MockedStatic<GitValidator> gitValidatorMock = mockStatic(GitValidator.class)) {
            gitValidatorMock.when(GitValidator::isGitRepository).thenReturn(true);
            gitValidatorMock.when(GitValidator::validateGitRepo).then(invocation -> null); // Correct way to mock a void
                                                                                           // method

            // Mock file existence check
            final Path mockFile = Paths.get("mock-pipeline.yaml");
            try (MockedStatic<Files> filesMock = mockStatic(Files.class)) {
                filesMock.when(() -> Files.exists(mockFile)).thenReturn(true);
                filesMock.when(() -> Files.isRegularFile(mockFile)).thenReturn(true);
                filesMock.when(() -> Files.isReadable(mockFile)).thenReturn(true);

                final CommandLine cmd = new CommandLine(rootCommand);
                final int exitCode = cmd.execute("--run", "-f", "mock-pipeline.yaml");
                assertEquals(0, exitCode, "Run command should execute successfully.");
            }
        }
    }
}
