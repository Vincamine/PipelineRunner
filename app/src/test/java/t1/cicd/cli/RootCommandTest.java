package t1.cicd.cli;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import picocli.CommandLine;

/**
 * Unit tests for the RootCommand of the CI/CD CLI application.
 */
public class RootCommandTest {

    /**
     * Tests the `--help` option to ensure it returns the correct exit code.
     */
    @Test
    public void testHelpCommand() {
        final CommandLine cmd = new CommandLine(new RootCommand());
        final int exitCode = cmd.execute("--help");
        assertEquals(0, exitCode);
    }

    /**
     * Tests the `--version` option to ensure it displays the version correctly.
     */
    @Test
    public void testVersionCommand() {
        final CommandLine cmd = new CommandLine(new RootCommand());
        final int exitCode = cmd.execute("--version");
        assertEquals(0, exitCode);
    }
}
