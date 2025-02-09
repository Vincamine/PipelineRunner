package edu.neu.cs6510.sp25.t1;

import edu.neu.cs6510.sp25.t1.cli.RootCommand;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import picocli.CommandLine;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class AppTest {

    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    private ByteArrayOutputStream outContent;
    private ByteArrayOutputStream errContent;

    @BeforeEach
    void setUp() {
        outContent = new ByteArrayOutputStream();
        errContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    @DisplayName("✅ App should display help when no arguments are passed")
    void testAppDisplaysHelp() {
        final String[] args = {};
        App.main(args);

        final String output = outContent.toString();
        assertTrue(output.contains("Usage") || output.contains("--help"), 
            "Expected CLI help output when no arguments are passed.");
    }

    @Test
    @DisplayName("🚀 App should execute RootCommand successfully")
    void testAppExecutesRootCommand() {
        final RootCommand mockRootCommand = Mockito.spy(new RootCommand());
        final CommandLine cmd = new CommandLine(mockRootCommand);

        final int exitCode = cmd.execute("--help");
        assertEquals(0, exitCode, "Expected CLI to return exit code 0 for --help command.");
    }
}
