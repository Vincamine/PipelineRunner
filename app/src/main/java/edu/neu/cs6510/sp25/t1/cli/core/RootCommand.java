package edu.neu.cs6510.sp25.t1.cli.core;

import edu.neu.cs6510.sp25.t1.cli.commands.CheckCommand;
import edu.neu.cs6510.sp25.t1.cli.commands.DryRunCommand;
import edu.neu.cs6510.sp25.t1.cli.commands.LogCommand;
import edu.neu.cs6510.sp25.t1.cli.commands.RunCommand;
import edu.neu.cs6510.sp25.t1.cli.commands.StatusCommand;
import edu.neu.cs6510.sp25.t1.util.GitValidator;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Root command for the CI/CD Command-Line Interface (CLI) tool.
 * 
 * This class defines the global CLI behavior, including general options
 * and available subcommands.
 */
@Command(name = "cli", version = "CI/CD CLI Tool 1.0", mixinStandardHelpOptions = true, description = "A CI/CD Command-Line Tool", subcommands = {
        LogCommand.class,
        StatusCommand.class,
        RunCommand.class,
        CheckCommand.class,
        DryRunCommand.class,
})
public class RootCommand implements Runnable {
    /**
     * Flag to enable verbose output.
     */
    @Option(names = { "-v", "--verbose" }, description = "Enable verbose output.")
    boolean verbose;

    /**
     * Flag to trigger CI/CD pipeline execution.
     */
    @Option(names = { "--run" }, description = "Trigger CI/CD pipeline execution")
    boolean run;

    /**
     * Filename for the CI/CD pipeline.
     */
    @Option(names = { "-f", "--filename" }, description = "Specify the filename for the CI/CD pipeline.")
    private String filename;

    /**
     * Main entry point for the CLI tool.
     */
    @Override
    public void run() {
        if (verbose) {
            System.out.println("Verbose mode enabled.");
        }

        // Ensure inside a Git repository
        if (!GitValidator.isGitRepository()) {
            System.err.println("Error: This command must be run inside a Git repository.");
            throw new IllegalStateException("This command must be run inside a Git repository.");
        }

        GitValidator.validateGitRepo();

        // Validate pipeline file
        if (!validateFilePath()) {
            throw new IllegalArgumentException("Invalid file path provided.");
        }

        if (run) {
            System.out.println("Running the CI/CD pipeline...");
            new RunCommand().run();
        } else {
            System.out.println("Welcome to the CI/CD CLI Tool!");
            System.out.println("Use 'cli --help' to view available commands.");
        }
    }

    /**
     * Validates the file path specified by the user through the -f or --filename
     * option.
     *
     * @return true if the file path is valid, false otherwise.
     */
    private boolean validateFilePath() {
        if (filename == null || filename.trim().isEmpty()) {
            System.err.println("Error: Filename must be specified with -f or --filename option");
            return false;
        }

        final Path path = Paths.get(filename).toAbsolutePath();

        if (!Files.exists(path)) {
            System.err.println("Error: File does not exist: " + filename);
            return false;
        }

        if (!Files.isRegularFile(path)) {
            System.err.println("Error: Not a regular file: " + filename);
            return false;
        }

        if (!Files.isReadable(path)) {
            System.err.println("Error: File is not readable: " + filename);
            return false;
        }

        return true;
    }
}
