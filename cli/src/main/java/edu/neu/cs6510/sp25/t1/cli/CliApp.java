package edu.neu.cs6510.sp25.t1.cli;

import java.util.concurrent.Callable;

import edu.neu.cs6510.sp25.t1.cli.commands.CheckCommand;
import edu.neu.cs6510.sp25.t1.cli.commands.DryRunCommand;
import edu.neu.cs6510.sp25.t1.cli.commands.ReportCommand;
import edu.neu.cs6510.sp25.t1.cli.commands.RunCommand;
import edu.neu.cs6510.sp25.t1.cli.commands.StatusCommand;
import picocli.CommandLine;

/**
 * The root command for the CI/CD CLI tool.
 * Manages global options and subcommands.
 */
@CommandLine.Command(
    name = "cicd-cli",
    description = "A command-line interface for running and managing CI/CD pipelines.",
    mixinStandardHelpOptions = true,
    subcommands = {
        CheckCommand.class,
        RunCommand.class,
        ReportCommand.class,
        DryRunCommand.class, StatusCommand.class
    }
)
public class CliApp implements Callable<Integer> {

  @CommandLine.Option(names = {"--repo", "-r"}, description = "Specify the repository (local path or HTTPS URL). Defaults to the current directory.", defaultValue = ".")
  public String repo;

  @CommandLine.Option(names = {"--branch", "-br"}, description = "Specify the Git branch. Defaults to 'main'.", defaultValue = "main")
  public String branch;

  @CommandLine.Option(names = {"--commit", "-co"}, description = "Specify the commit hash. Defaults to the latest commit.", defaultValue = "")
  public String commit;

  @CommandLine.Option(names = {"--pipeline", "-p"}, description = "Specify the pipeline name to run.")
  public String pipeline;

  @CommandLine.Option(names = {"--local"}, description = "Run the pipeline locally.")
  public boolean localRun;

  @CommandLine.Option(names = {"--vv", "--verbose"}, description = "Enable verbose output (detailed logs).")
  private boolean verbose;

  @Override
  public Integer call() {
    // Check if the current directory is a Git repository
    try {
      System.out.println("CI/CD CLI - Ready! Use `--help` for available commands.");
      return 0;
    } catch (Exception e) {
      System.err.println("Failed to initialize CLI: " + e.getMessage());
      return 1;
    }
  }

  public static void main(String[] args) {
    // Create an instance of the CLI application
    CliApp app = new CliApp();
    CommandLine cmd = new CommandLine(app);

    // Configure custom execution strategy to capture exit code
    int exitCode = new CommandLine(new CliApp()).execute(args);

    // Output clean success/failure message based on exit code
    if (args.length > 0 && args[0].equals("run")) {
      if (exitCode == 0) {
        System.out.println("success");
      } else {
        System.out.println("failed");
      }
    }

    // Exit with the captured exit code
    System.exit(exitCode);
  }
}
