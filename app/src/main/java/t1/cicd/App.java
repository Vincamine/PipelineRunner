package t1.cicd;

import picocli.CommandLine;
import t1.cicd.cli.RootCommand;

/**
 * Entry point for the CI/CD Command-Line Interface (CLI) application.
 */
public class App {
    public static void main(String[] args) {
        final int exitCode = new CommandLine(new RootCommand()).execute(args);
        System.exit(exitCode);
    }
}
