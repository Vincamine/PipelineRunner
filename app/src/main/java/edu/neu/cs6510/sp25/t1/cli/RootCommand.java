package edu.neu.cs6510.sp25.t1.cli;

import edu.neu.cs6510.sp25.t1.cli.commands.LogCommand;
import edu.neu.cs6510.sp25.t1.cli.commands.RunCommand;
import edu.neu.cs6510.sp25.t1.cli.commands.StatusCommand;
import edu.neu.cs6510.sp25.t1.cli.util.GitValidator;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * RootCommand serves as the primary command for the CI/CD CLI application.
 * 
 * This class provides:
 * <ul>
 *     <li>Global CLI options (e.g., {@code --verbose}, {@code --run}).</li>
 *     <li>Automatic help and version management using {@code picocli}.</li>
 *     <li>Registration of all available CLI subcommands:
 *         <ul>
 *             <li>{@link LogCommand} - Handles logging-related commands.</li>
 *             <li>{@link StatusCommand} - Checks the status of CI/CD pipelines.</li>
 *             <li>{@link RunCommand} - Executes the CI/CD pipeline.</li>
 *         </ul>
 *     </li>
 *     <li>Validation to ensure that the CLI runs inside a valid Git repository.</li>
 * </ul>
 * 
 * The CLI is built using the {@code picocli} framework and supports standard command-line 
 * arguments and subcommands.
 * 
 * <h2>Usage:</h2>
 * <pre>
 * cli --help            # Displays the help message
 * cli --version         # Shows the CLI version
 * cli --verbose         # Enables verbose output
 * cli run               # Executes the CI/CD pipeline
 * cli status            # Checks the pipeline status
 * cli log               # Displays log entries
 * </pre>
 * 
 * @author Your Name
 * @version 1.0
 */
@Command(
    name = "cli",
    version = {"CI/CD CLI Tool 1.0"},
    mixinStandardHelpOptions = true,
    description = "A CI/CD Command-Line Tool",
    subcommands = {
        LogCommand.class,  
        StatusCommand.class,
        RunCommand.class  
    }
)
public class RootCommand implements Runnable {

    /**
     * Enables verbose output mode for the CLI.
     * When enabled, additional debug information will be printed during execution.
     */
    @Option(names = {"-v", "--verbose"}, description = "Enable verbose output.")
    boolean verbose;

    /**
     * Triggers the execution of the CI/CD pipeline.
     * When this option is provided, the system will attempt to run the pipeline.
     */
    @Option(names = {"--run"}, description = "Trigger CI/CD pipeline execution")
    boolean run;

    /**
     * Default action executed when no subcommands or options are provided.
     * <p>
     * This method performs the following steps:
     * <ol>
     *     <li>Ensures the CLI is running inside a valid Git repository.</li>
     *     <li>If {@code --run} is provided, it executes the {@link RunCommand}.</li>
     *     <li>Otherwise, it displays a welcome message with usage instructions.</li>
     * </ol>
     */
    @Override
    public void run() {
        // 🔹 Ensure the CLI runs inside a Git repository
        GitValidator.validateGitRepo();

        if (run) {
            new RunCommand().run();
        } else {
            System.out.println("Welcome to the CI/CD CLI Tool!");
            System.out.println("Use 'cli --help' to view available commands.");
        }
    }
}
