package t1.cicd.cli.commands;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * The {@code LogCommand} class handles logging operations for the CI/CD CLI application.
 * 
 * This command allows users to log custom messages using the `--message` or `-m` option.
 * If no message is provided, the command outputs a default message indicating that no
 * log message was provided.
 * 
 * Usage examples:
 * - Log a custom message:
 *   cli log --message "Deployment started"
 * 
 * - Run without a message:
 *   cli log
 *   This will display "No message provided."
 * 
 */
@Command(
    name = "log",
    description = "Manage CI/CD logs."
)
public class LogCommand implements Runnable {

    /**
     * The log message to be displayed.
     * 
     * Users can specify a custom message using the `--message` or `-m` option when
     * executing the CLI command.
     * 
     * Example:
     * cli log --message "Build successful"
     */
    @Option(names = {"-m", "--message"}, description = "Log message to display.")
    private String message;

    /**
     * Executes the log command.
     * 
     * If a message is provided via the `--message` option, it will be displayed
     * in the format: "Log: <message>". If no message is provided, the output
     * will be "No message provided."
     */
    @Override
    public void run() {
        if (message != null && !message.isEmpty()) {
            System.out.println("Log: " + message);
        } else {
            System.out.println("No message provided.");
        }
    }
}
