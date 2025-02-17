package edu.neu.cs6510.sp25.t1.cli.commands;

import picocli.CommandLine.Command;

/**
 * Command to display available CLI commands and their descriptions.
 */
@Command(name = "help", description = "Display available commands and their usage", mixinStandardHelpOptions = true)
public class HelpCommand implements Runnable {
    @Override
    public void run() {
        System.out.println("Use `cli --help` to view all available commands.");
    }
}
