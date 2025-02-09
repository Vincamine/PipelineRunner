package edu.neu.cs6510.sp25.t1.cli;

import edu.neu.cs6510.sp25.t1.cli.commands.LogCommand;
import edu.neu.cs6510.sp25.t1.cli.commands.RunCommand;
import edu.neu.cs6510.sp25.t1.cli.commands.StatusCommand;
import edu.neu.cs6510.sp25.t1.cli.util.GitValidator;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Root command for the CI/CD CLI tool.
 * <p>
 * Handles global options such as {@code --verbose} and executes appropriate subcommands.
 * </p>
 */
@Command(
    name = "cli",
    version = "CI/CD CLI Tool 1.0",
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
     * Enables verbose mode.
     */
    @Option(names = {"-v", "--verbose"}, description = "Enable verbose output.")
    boolean verbose;

    /**
     * Triggers the execution of the CI/CD pipeline.
     */
    @Option(names = {"--run"}, description = "Trigger CI/CD pipeline execution")
    boolean run;

    @Override
    public void run() {
        // ✅ Ensure verbose message is printed FIRST before Git validation
        if (verbose) {
            System.out.println("✅ Verbose mode enabled.");
        }

        // 🚨 Perform Git validation AFTER verbose flag handling
        GitValidator.validateGitRepo();

        if (run) {
            System.out.println("🚀 Running the CI/CD pipeline...");
            new RunCommand().run();
        } else {
            System.out.println("Welcome to the CI/CD CLI Tool!");
            System.out.println("Use 'cli --help' to view available commands.");
        }
    }
}
