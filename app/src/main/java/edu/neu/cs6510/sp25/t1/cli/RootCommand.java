package edu.neu.cs6510.sp25.t1.cli;

import edu.neu.cs6510.sp25.t1.cli.commands.LogCommand;
import edu.neu.cs6510.sp25.t1.cli.commands.RunCommand;
import edu.neu.cs6510.sp25.t1.cli.commands.StatusCommand;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * RootCommand serves as the main entry point for the CI/CD CLI application.
 * 
 * This command registers all subcommands and handles global options such as
 * `--help` and `--version`.
 */
@Command(
    name = "cli",
    version = {"CI/CD CLI Tool 1.0"},       // ✅ CLI version
    mixinStandardHelpOptions = true,        // ✅ Adds --help and --version automatically
    description = "A CI/CD Command-Line Tool",
    subcommands = {                         // ✅ Register subcommands here
        LogCommand.class,                   // Example subcommand
        CommandLineInterface.class,         // Main CLI command
        StatusCommand.class,                 // Check pipeline status
        RunCommand.class                    // Run pipeline
    }
)
public class RootCommand implements Runnable {

    @Option(names = {"-v", "--verbose"}, description = "Enable verbose output.")
    boolean verbose;

    @Option(names = {"--run"}, description = "Trigger CI/CD pipeline execution")
    boolean run;

    /**
     * The default action when no subcommands or options are provided.
     */
    @Override
    public void run() {
        if (run) {
            new RunCommand().run();
        } else {
            System.out.println("Welcome to the CI/CD CLI Tool!");
            System.out.println("Use 'cli --help' to view available commands.");
        }
    }
}

