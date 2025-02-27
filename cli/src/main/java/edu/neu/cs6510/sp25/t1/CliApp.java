package edu.neu.cs6510.sp25.t1;

import edu.neu.cs6510.sp25.t1.cli.core.RootCommand;
import picocli.CommandLine;

/**
 * The CI/CD Command-Line Interface (CLI) Tool.
 * 
 * This project is a custom Continuous Integration and Continuous Deployment
 * (CI/CD) system designed to allow developers to automate the build, test,
 * and deployment process. Unlike standard CI/CD tools like GitHub Actions
 * or GitLab CI/CD, this system enables:
 * 
 * - Local execution of CI/CD pipelines (without requiring a cloud-based
 * runner).
 * - Full control over pipeline configuration, stored within the repository.
 * - Execution of jobs and stages based on dependencies, defined in a YAML file.
 * - Command-line interaction for validation, execution, and debugging of
 * pipelines.
 * 
 * How This Application Works:
 * 1. Developers define their CI/CD pipelines in a YAML file
 * (`.pipelines/pipeline.yaml`).
 * 2. The CLI provides commands to validate, execute, and monitor pipeline
 * execution.
 * 3. The system parses and validates the pipeline configuration before
 * execution.
 * 4. Jobs are executed sequentially or in parallel, depending on their
 * dependencies.
 * 5. Execution logs and statuses are stored and retrievable through CLI
 * commands.
 * 
 * Key CLI Commands:
 * 
 * xx check -f pipeline.yaml // Validate the pipeline configuration
 * xx run -f pipeline.yaml // Execute the pipeline locally
 * xx status // View execution history
 * xx log // Retrieve past logs
 * 
 * @see RootCommand
 * @see edu.neu.cs6510.sp25.t1.cli.commands.RunCommand
 * @see edu.neu.cs6510.sp25.t1.cli.commands.CheckCommand
 */
public class CliApp {

    /**
     * Main entry point for the CI/CD CLI tool.
     * 
     * This method initializes the Picocli command-line interface and delegates
     * execution to the appropriate subcommands.
     *
     * @param args Command-line arguments provided by the user.
     */
    public static void main(String[] args) {
        // Initialize the CLI with RootCommand and execute the given arguments.
        final int exitCode = new CommandLine(new RootCommand()).execute(args);

        // Exit the application with the returned exit code.
        System.exit(exitCode);
    }
}
