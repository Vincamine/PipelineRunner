package edu.neu.cs6510.sp25.t1.cli;

import org.junit.jupiter.api.*;
import picocli.CommandLine;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    @DisplayName("✅ CLI should display help message")
    void testHelpCommand() {
        final int exitCode = cmd.execute("--help");
        assertEquals(0, exitCode, "Expected CLI to return exit code 0 for --help.");

        final String output = outContent.toString();
        assertTrue(output.contains("Usage"), "Expected CLI help output to contain 'Usage'.");
    }

    @Test
    @DisplayName("🚀 CLI should accept --verbose flag")
    void testVerboseFlag() {
        final int exitCode = cmd.execute("--verbose");
        assertEquals(0, exitCode, "Expected CLI to return exit code 0 when --verbose is passed.");

        assertTrue(rootCommand.verbose, "Expected verbose flag to be enabled.");
    }
}
