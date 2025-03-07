package edu.neu.cs6510.sp25.t1.cli;

import edu.neu.cs6510.sp25.t1.cli.commands.CheckCommand;
import edu.neu.cs6510.sp25.t1.cli.commands.DryRunCommand;
import edu.neu.cs6510.sp25.t1.cli.commands.ReportCommand;
import edu.neu.cs6510.sp25.t1.cli.commands.RunCommand;
import picocli.CommandLine;

/**
 * Main class for the CI/CD CLI tool.
 */
@CommandLine.Command(
        name = "xx",
        version = "CI/CD CLI Tool 1.0",
        mixinStandardHelpOptions = true,
        description = "A CI/CD Command-Line Tool for executing and managing pipelines.",
        subcommands = {
                RunCommand.class,
                CheckCommand.class,
                DryRunCommand.class,
                ReportCommand.class
        }
)
public class CliApp implements Runnable {

  @CommandLine.Option(
          names = {"-v", "--verbose"},
          description = "Enable verbose output for debugging."
  )
  boolean verbose;

  /**
   * Entry point when no command is provided.
   */
  @Override
  public void run() {
    System.out.println("CI/CD CLI Tool - Use --help for available commands.");
  }

  /**
   * Main method to execute the CLI application.
   *
   * @param args Command-line arguments.
   */
  public static void main(String[] args) {
    int exitCode = new CommandLine(new CliApp()).execute(args);
    System.exit(exitCode);
  }
}
