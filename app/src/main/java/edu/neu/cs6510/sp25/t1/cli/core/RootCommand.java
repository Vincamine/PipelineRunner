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
        RunCommand.class,
        CheckCommand.class
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

    @Option(names = {"-f", "--filename"}, description = "Specify the filename for the CI/CD pipeline.")
    private String filename;

    @Override
    public void run() {
        if (verbose) {
            System.out.println("✅ Verbose mode enabled.");
        }
        
        GitValidator.validateGitRepo();
        
        if (!validateFilePath()) {
            return;
        }
        
        if (run) {
            System.out.println("🚀 Running the CI/CD pipeline...");
            new RunCommand().run();
        } else {
            System.out.println("Welcome to the CI/CD CLI Tool!");
            System.out.println("Use 'cli --help' to view available commands.");
        }
    }

    /**
     * Validates the file path specified by -f or --filename option.
     * 
     * @return true if the file path is valid, false otherwise
     */
    private boolean validateFilePath() {
        if (filename == null || filename.trim().isEmpty()) {
            System.err.println("Error: Filename must be specified with -f or --filename option");
            return false;
        }

        final Path path = Paths.get(filename);
        
        // Check if path exists
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
