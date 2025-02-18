package edu.neu.cs6510.sp25.t1.cli.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import static org.junit.jupiter.api.Assertions.*;

class RootCommandTest {
    private RootCommand rootCommand;

    @BeforeEach
    void setUp() {
        rootCommand = new RootCommand();
    }

    /** Ensures `--help` prints the help message successfully */
    @Test
    void testHelpCommand() {
        final CommandLine cmd = new CommandLine(rootCommand);
        final int exitCode = cmd.execute("--help");
        assertEquals(0, exitCode, "Help command should execute successfully.");
    }

    /** Ensures `--verbose` does not cause errors */
    @Test
    void testVerboseFlag() {
        final CommandLine cmd = new CommandLine(rootCommand);
        final int exitCode = cmd.execute("--verbose");
        assertEquals(0, exitCode, "Verbose mode should not cause errors.");
    }

    /** Ensures all subcommands are properly registered */
    @Test
    void testSubcommandsExist() {
        final CommandLine cmd = new CommandLine(rootCommand);
        assertNotNull(cmd.getSubcommands().get("run"), "RunCommand should be registered.");
        assertNotNull(cmd.getSubcommands().get("check"), "CheckCommand should be registered.");
        assertNotNull(cmd.getSubcommands().get("logs"), "LogCommand should be registered.");
        assertNotNull(cmd.getSubcommands().get("status"), "StatusCommand should be registered.");
        assertNotNull(cmd.getSubcommands().get("dry-run"), "DryRunCommand should be registered.");
    }
}
