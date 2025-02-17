package edu.neu.cs6510.sp25.t1.cli;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;

import edu.neu.cs6510.sp25.t1.cli.core.RootCommand;
import edu.neu.cs6510.sp25.t1.util.GitValidator;
import picocli.CommandLine;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;

/**
 * Unit tests for {@link RootCommand}.
 */
class RootCommandTest {

    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    private ByteArrayOutputStream outContent;
    private ByteArrayOutputStream errContent;
    private RootCommand rootCommand;
    private CommandLine cmd;
    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        outContent = new ByteArrayOutputStream();
        errContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));

        rootCommand = new RootCommand();
        cmd = new CommandLine(rootCommand);
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    /**
     * Tests whether the CLI displays a help message when `--help` is used.
     */
    @Test
    @DisplayName("✅ CLI should display help message")
    void testHelpCommand() {
        final int exitCode = cmd.execute("--help");
        assertEquals(0, exitCode, "Expected CLI to return exit code 0 for --help.");

        final String output = outContent.toString();
        assertTrue(output.contains("Usage") || output.contains("--help"),
                "Expected CLI help output to contain 'Usage'.");
    }

    /**
     * Tests whether the `--verbose` flag is correctly recognized.
     */
    @Test
    @DisplayName("🚀 CLI should accept --verbose flag")
    void testVerboseFlag() {
        try (MockedStatic<GitValidator> mockGitValidator = mockStatic(GitValidator.class)) {
            // Mock GitValidator to prevent failure due to missing .git directory
            mockGitValidator.when(GitValidator::validateGitRepo).thenAnswer(invocation -> null);
            mockGitValidator.when(GitValidator::isGitRepository).thenReturn(true);

            final int exitCode = cmd.execute("--verbose", "-f", "test.txt");
            assertEquals(0, exitCode);
            assertTrue(outContent.toString().contains("✅ Verbose mode enabled."));    
        }
    }

    /**
     * Tests whether the CLI correctly handles a valid filename.
     */
    @Test
    @DisplayName("✅ CLI should accept a valid filename")
    void testValidFilename() throws Exception {
        final Path tempFile = tempDir.resolve("test.txt");
        Files.createFile(tempFile);

        try (MockedStatic<GitValidator> mockGitValidator = mockStatic(GitValidator.class)) {
            mockGitValidator.when(GitValidator::validateGitRepo).thenAnswer(invocation -> null);
            mockGitValidator.when(GitValidator::isGitRepository).thenReturn(true);

            final int exitCode = cmd.execute("-f", tempFile.toString());
            assertEquals(0, exitCode);
            final String output = outContent.toString();
            assertTrue(output.contains("Welcome to the CI/CD CLI Tool"));
        }
    }

    @Test
    @DisplayName("🚨 CLI should handle missing filename")
    void testMissingFilename() {
        try (MockedStatic<GitValidator> mockGitValidator = mockStatic(GitValidator.class)) {
            mockGitValidator.when(GitValidator::validateGitRepo).thenAnswer(invocation -> null);
            mockGitValidator.when(GitValidator::isGitRepository).thenReturn(true);

            final int exitCode = cmd.execute();
            assertEquals(1, exitCode);
            assertTrue(errContent.toString().contains("Error: Filename must be specified"));
        }
    }

    @Test
    @DisplayName("🚨 CLI should handle non-existent file")
    void testNonExistentFile() {
        try (MockedStatic<GitValidator> mockGitValidator = mockStatic(GitValidator.class)) {
            mockGitValidator.when(GitValidator::validateGitRepo).thenAnswer(invocation -> null);
            mockGitValidator.when(GitValidator::isGitRepository).thenReturn(true);

            final int exitCode = cmd.execute("-f", "nonexistent.txt");
            assertEquals(1, exitCode);
            assertTrue(errContent.toString().contains("Error: File does not exist"));
        }
    }

    @Test
    @DisplayName("🚨 CLI should handle directory as filename")
    void testDirectoryAsFilename() throws Exception {
        final Path tempDirectory = tempDir.resolve("testDir");
        Files.createDirectory(tempDirectory);

        try (MockedStatic<GitValidator> mockGitValidator = mockStatic(GitValidator.class)) {
            mockGitValidator.when(GitValidator::validateGitRepo).thenAnswer(invocation -> null);
            mockGitValidator.when(GitValidator::isGitRepository).thenReturn(true);

            final int exitCode = cmd.execute("-f", tempDirectory.toString());
            assertEquals(1, exitCode);
            assertTrue(errContent.toString().contains("Error: Not a regular file"));
        }
    }

    @Test
    @DisplayName("✅ CLI should handle empty filename")
    void testEmptyFilename() {
        try (MockedStatic<GitValidator> mockGitValidator = mockStatic(GitValidator.class)) {
            mockGitValidator.when(GitValidator::validateGitRepo).thenAnswer(invocation -> null);
            mockGitValidator.when(GitValidator::isGitRepository).thenReturn(true);

            final int exitCode = cmd.execute("-f", "");
            assertEquals(1, exitCode);
            assertTrue(errContent.toString().contains("Error: Filename must be specified"));
        }
    }

    @Test
    @DisplayName("✅ CLI should handle run command with valid file")
    void testRunCommandWithValidFile() throws Exception {
        final Path tempFile = tempDir.resolve("test.txt");
        Files.createFile(tempFile);

        try (MockedStatic<GitValidator> mockGitValidator = mockStatic(GitValidator.class)) {
            mockGitValidator.when(GitValidator::validateGitRepo).thenAnswer(invocation -> null);
            mockGitValidator.when(GitValidator::isGitRepository).thenReturn(true);

            final int exitCode = cmd.execute("--run", "-f", tempFile.toString());
            assertEquals(0, exitCode);
            assertTrue(outContent.toString().contains("🚀 Running the CI/CD pipeline"));
        }
    }

    @Test
    @DisplayName("✅ CLI should handle version command")
    void testVersionCommand() {
        final int exitCode = cmd.execute("--version");
        assertEquals(0, exitCode);
        assertTrue(outContent.toString().contains("CI/CD CLI Tool"));
    }

}
