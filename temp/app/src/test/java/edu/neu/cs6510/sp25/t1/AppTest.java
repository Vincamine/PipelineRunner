package edu.neu.cs6510.sp25.t1;

import edu.neu.cs6510.sp25.t1.cli.core.RootCommand;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import picocli.CommandLine;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AppTest {

    @Test
    void testMain_ValidCommand() {
        // Simulate CLI execution with a mock RootCommand
        final RootCommand rootCommand = Mockito.mock(RootCommand.class);
        final CommandLine cmd = new CommandLine(rootCommand);

        // Ensure execution does not throw errors
        final int exitCode = cmd.execute("--help");
        assertEquals(0, exitCode, "Expected successful execution of --help command.");
    }
}
