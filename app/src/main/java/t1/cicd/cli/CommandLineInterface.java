package t1.cicd.cli;

import picocli.CommandLine.Command;

/**
 * CommandLineInterface handles parsing and execution of CLI commands.
 * 
 * It defines global options such as `--help` and `--version` and provides
 * a default message guiding the user to available commands.
 * 
 * Supported options:
 * - `--help` or `-h`: Displays the help message.
 * - `--version` or `-v`: Shows the current version of the CLI tool.
 */
@Command(
    name = "cli",
    version = {"CI/CD CLI Tool 1.0"},  
    mixinStandardHelpOptions = true,  
    description = "A CI/CD Command-Line Tool"
)
public class CommandLineInterface implements Runnable {

    /**
     * The default action when no subcommands or options are provided.
     * Prints a guide message to the user.
     */
    @Override
    public void run() {
        System.out.println("Use 'cli --help' to view available commands.");
    }
}
