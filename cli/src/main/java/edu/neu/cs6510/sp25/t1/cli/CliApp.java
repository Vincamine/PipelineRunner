package edu.neu.cs6510.sp25.t1.cli;

import edu.neu.cs6510.sp25.t1.cli.commands.CheckCommand;
import edu.neu.cs6510.sp25.t1.cli.commands.DryRunCommand;
import edu.neu.cs6510.sp25.t1.cli.commands.RunCommand;
import picocli.CommandLine;

/**
 * Main class for the CI/CD CLI tool.
 */
@CommandLine.Command(name = "cli", version = "CI/CD CLI Tool 1.0", mixinStandardHelpOptions = true, description = "A CI/CD Command-Line Tool", subcommands = {
        RunCommand.class,
        CheckCommand.class,
        DryRunCommand.class
})
public class CliApp implements Runnable {
    @CommandLine.Option(names = { "-v", "--verbose" }, description = "Enable verbose output.")
    boolean verbose;

    /**
     * Main entry point for the CLI application.
     */
    @Override
    public void run() {
        System.out.println("CI/CD CLI Tool - Use --help for available commands.");
    }

    /**
     * Main method to run the CLI application.
     * 
     * @param args
     */
    public static void main(String[] args) {
        int exitCode = new CommandLine(new CliApp()).execute(args);
        System.exit(exitCode);
    }
}
