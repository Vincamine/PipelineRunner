package t1.cicd.cli;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import t1.cicd.cli.commands.LogCommand;
import t1.cicd.cli.commands.StatusCommand;

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
        StatusCommand.class                 // Check pipeline status
    }
)
public class RootCommand implements Runnable {

    @Option(names = {"-v", "--verbose"}, description = "Enable verbose output.")
    boolean verbose;

    /**
     * The default action when no subcommands or options are provided.
     */
    @Override
    public void run() {
        System.out.println("Welcome to the CI/CD CLI Tool!");
        System.out.println("Use 'cli --help' to view available commands.");
    }
}

