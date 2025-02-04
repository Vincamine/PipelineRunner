package t1.cicd;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import picocli.CommandLine;
import t1.cicd.cli.CommandLineInterface;

/**
 * Unit tests for the CI/CD Command-Line Interface (CLI) application.
 */
public class AppTest {

    /**
     * Tests the `--help` option to ensure it returns the correct exit code.
     */
    @Test
    public void testHelpCommand() {
        final CommandLine cmd = new CommandLine(new CommandLineInterface());
        final int exitCode = cmd.execute("--help");

        // Debug output to confirm behavior
        System.out.println("Exit Code: " + exitCode);

        assertEquals(0, exitCode); 
    }
}
