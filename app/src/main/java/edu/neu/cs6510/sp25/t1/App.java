package edu.neu.cs6510.sp25.t1;

import edu.neu.cs6510.sp25.t1.cli.core.RootCommand;
import picocli.CommandLine;

/**
 * Entry point for the CI/CD Command-Line Interface (CLI) application.
 */
public class App {
    public static void main(String[] args) {
        final int exitCode = new CommandLine(new RootCommand()).execute(args);
        System.exit(exitCode);
    }
}
