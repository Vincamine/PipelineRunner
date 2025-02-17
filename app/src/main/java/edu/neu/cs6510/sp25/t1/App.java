package edu.neu.cs6510.sp25.t1;

import edu.neu.cs6510.sp25.t1.cli.core.RootCommand;
import picocli.CommandLine;

/**
 * <h1>CI/CD Command-Line Interface (CLI) Tool</h1>
 *
 * This project is a **custom Continuous Integration and Continuous Deployment (CI/CD) system** 
 * designed to allow developers to automate the build, test, and deployment process.
 * Unlike standard CI/CD tools like GitHub Actions or GitLab CI/CD, this system enables:
 * <ul>
 *     <li>**Local execution of CI/CD pipelines** (without requiring a cloud-based runner).</li>
 *     <li>**Full control over pipeline configuration**, stored within the repository.</li>
 *     <li>**Execution of jobs and stages based on dependencies**, defined in a YAML file.</li>
 *     <li>**Command-line interaction for validation, execution, and debugging of pipelines**.</li>
 * </ul>
 *
 * <h2>How This Application Works:</h2>
 * <ol>
 *     <li>Developers define their CI/CD pipelines in a YAML file (`.pipelines/pipeline.yaml`).</li>
 *     <li>The CLI provides commands to validate, execute, and monitor pipeline execution.</li>
 *     <li>The system parses and validates the pipeline configuration before execution.</li>
 *     <li>Jobs are executed sequentially or in parallel, depending on their dependencies.</li>
 *     <li>Execution logs and statuses are stored and retrievable through CLI commands.</li>
 * </ol>
 *
 * <h2>Key CLI Commands:</h2>
 * <pre>
 *   xx check -f pipeline.yaml   // Validate the pipeline configuration
 *   xx run -f pipeline.yaml     // Execute the pipeline locally
 *   xx status                   // View execution history
 *   xx log                      // Retrieve past logs
 * </pre>
 *
 * @see RootCommand
 * @see edu.neu.cs6510.sp25.t1.cli.commands.RunCommand
 * @see edu.neu.cs6510.sp25.t1.cli.commands.CheckCommand
 */
public class App {
    /**
     * Main entry point for the CI/CD CLI tool.
     * <p>
     * This method initializes the Picocli command-line interface and delegates execution 
     * to the appropriate subcommands.
     * </p>
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
