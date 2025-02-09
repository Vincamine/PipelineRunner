package edu.neu.cs6510.sp25.t1.cli;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import edu.neu.cs6510.sp25.t1.cli.util.GitValidatorTest;
import picocli.CommandLine;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

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
     * ✅ Tests whether the CLI displays a help message when `--help` is used.
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
     * 🚀 Tests whether the `--verbose` flag is correctly recognized.
     * ✅ Uses Mockito to bypass Git repository validation.
     */
    @Test
    @DisplayName("🚀 CLI should accept --verbose flag")
    void testVerboseFlag() {
        try (MockedStatic<GitValidatorTest> mockGitValidator = mockStatic(GitValidatorTest.class)) {
            // Mock the GitValidator to prevent it from failing the test
            mockGitValidator.when(GitValidatorTest::validateGitRepo).thenAnswer(invocation -> null);

            final int exitCode = cmd.execute("--verbose");
            assertEquals(0, exitCode, "Expected CLI to return exit code 0 when --verbose is passed.");

            final String output = outContent.toString();
            assertTrue(output.contains("✅ Verbose mode enabled."), "Expected verbose message to be printed.");
        }
    }
}
