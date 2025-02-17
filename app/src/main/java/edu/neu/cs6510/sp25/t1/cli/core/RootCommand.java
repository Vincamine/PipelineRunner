package edu.neu.cs6510.sp25.t1.cli.core;

import edu.neu.cs6510.sp25.t1.cli.commands.CheckCommand;
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
 * <p>
 * This class defines the global CLI behavior, including general options 
 * (like {@code --verbose}) and available subcommands (like {@code run}, {@code check}, etc.).
 * </p>
 *
 * <h2>Key Responsibilities:</h2>
 * <ul>
 *     <li>Registers CLI subcommands: {@link RunCommand}, {@link CheckCommand}, {@link LogCommand}, {@link StatusCommand}.</li>
 *     <li>Provides global options such as {@code --verbose} and {@code --filename}.</li>
 *     <li>Performs preliminary checks before executing subcommands (e.g., verifying Git repository).</li>
 * </ul>
 *
 * <h2>Execution Flow:</h2>
 * <ol>
 *     <li>User runs a CLI command (e.g., {@code xx run -f pipeline.yaml}).</li>
 *     <li>{@code RootCommand} parses options and determines the requested action.</li>
 *     <li>Validates environment (e.g., Git repo check).</li>
 *     <li>Calls the corresponding subcommand.</li>
 * </ol>
 *
 * <h2>Example Usage:</h2>
 * <pre>
 *     xx --help               // Show available commands
 *     xx --verbose run -f pipeline.yaml   // Run pipeline in verbose mode
 *     xx check -f pipeline.yaml  // Validate pipeline YAML file
 * </pre>
 *
 * @see RunCommand
 * @see CheckCommand
 * @see StatusCommand
 * @see LogCommand
 */
@Command(
    name = "cli",
    version = "CI/CD CLI Tool 1.0",
    mixinStandardHelpOptions = true,
    description = "A CI/CD Command-Line Tool",
    subcommands = {
        LogCommand.class,
        StatusCommand.class,
        RunCommand.class,
        CheckCommand.class
    }
)
public class RootCommand implements Runnable {

    /**
     * Enables verbose mode, providing detailed logging output.
     */
    @Option(names = {"-v", "--verbose"}, description = "Enable verbose output.")
    boolean verbose;

    /**
     * Triggers the execution of the CI/CD pipeline.
     * If specified, the application will attempt to execute a pipeline run.
     */
    @Option(names = {"--run"}, description = "Trigger CI/CD pipeline execution")
    boolean run;

    /**
     * Specifies the filename of the CI/CD pipeline YAML configuration file.
     */
    @Option(names = {"-f", "--filename"}, description = "Specify the filename for the CI/CD pipeline.")
    private String filename;

    /**
     * Entry point for the CLI tool.  
     * This method:
     * <ul>
     *     <li>Prints verbose mode information if enabled.</li>
     *     <li>Validates that the command is executed in a Git repository.</li>
     *     <li>Validates the specified pipeline file.</li>
     *     <li>Executes the requested command (e.g., {@code run}).</li>
     * </ul>
     */
    @Override
    public void run() {
        if (verbose) {
            System.out.println("✅ Verbose mode enabled.");
        }
        
        // Ensure the command is being executed inside a Git repository.
        GitValidator.validateGitRepo();
        
        // Validate the provided pipeline file path.
        if (!validateFilePath()) {
            return;
        }
        
        // If the `--run` option is specified, execute the pipeline.
        if (run) {
            System.out.println("🚀 Running the CI/CD pipeline...");
            new RunCommand().run();
        } else {
            System.out.println("Welcome to the CI/CD CLI Tool!");
            System.out.println("Use 'cli --help' to view available commands.");
        }
    }

    /**
     * Validates the file path specified by the user through the {@code -f} or {@code --filename} option.
     * <p>
     * This method checks if:
     * <ul>
     *     <li>The filename is provided.</li>
     *     <li>The file exists.</li>
     *     <li>The file is a regular file (not a directory).</li>
     *     <li>The file is readable.</li>
     * </ul>
     * </p>
     *
     * @return {@code true} if the file path is valid; {@code false} otherwise.
     */
    private boolean validateFilePath() {
        if (filename == null || filename.trim().isEmpty()) {
            System.err.println("Error: Filename must be specified with -f or --filename option");
            return false;
        }

        final Path path = Paths.get(filename);
        
        // Check if file exists
        if (!Files.exists(path)) {
            System.err.println("Error: File does not exist: " + filename);
            return false;
        }
        
        // Check if it's a regular file (not a directory)
        if (!Files.isRegularFile(path)) {
            System.err.println("Error: Not a regular file: " + filename);
            return false;
        }
        
        // Check if file is readable
        if (!Files.isReadable(path)) {
            System.err.println("Error: File is not readable: " + filename);
            return false;
        }

        return true;
    }
}
