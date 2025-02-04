package t1.cicd.cli.commands;


import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import picocli.CommandLine;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * Unit tests for the LogCommand of the CI/CD CLI application.
 */
public class LogCommandTest {

    /**
     * Tests the log command with a message to ensure proper output.
     */
    @Test
    public void testLogMessage() {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        final CommandLine cmd = new CommandLine(new LogCommand());
        final int exitCode = cmd.execute("--message", "Hello, CI/CD!");

        assertEquals(0, exitCode);
        assertEquals("Log: Hello, CI/CD!" + System.lineSeparator(), outputStream.toString());
    }

    /**
     * Tests the log command without a message to check default behavior.
     */
    @Test
    public void testLogWithoutMessage() {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        final CommandLine cmd = new CommandLine(new LogCommand());
        final int exitCode = cmd.execute();

        assertEquals(0, exitCode);
        assertEquals("No message provided." + System.lineSeparator(), outputStream.toString());
    }
}
