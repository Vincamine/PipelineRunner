package edu.neu.cs6510.sp25.t1.cli;

import java.util.concurrent.Callable;

import edu.neu.cs6510.sp25.t1.cli.commands.CheckCommand;
import edu.neu.cs6510.sp25.t1.cli.commands.ReportCommand;
import edu.neu.cs6510.sp25.t1.cli.commands.RunCommand;
import edu.neu.cs6510.sp25.t1.cli.validation.utils.GitUtils;
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
                ReportCommand.class
        }
)
public class CliApp implements Callable<Integer> {

  @CommandLine.Option(names = {"--repo", "-r"}, description = "Specify the repository (local path or HTTPS URL). Defaults to the current directory.", defaultValue = ".")
  public String repo;

  @CommandLine.Option(names = {"--branch", "-br"}, description = "Specify the Git branch. Defaults to 'main'.", defaultValue = "main")
  public String branch;

  @CommandLine.Option(names = {"--commit", "-co"}, description = "Specify the commit hash. Defaults to the latest commit.", defaultValue = "")
  public String commit;

  @CommandLine.Option(names = {"--filename", "-f"}, description = "Specify the pipeline config file. Defaults to '.pipelines/pipeline.yaml'.", defaultValue = ".pipelines/pipeline.yaml")
  public String filePath;

  @CommandLine.Option(names = {"--pipeline", "-p"}, description = "Specify the pipeline name to run.")
  public String pipeline;

  @CommandLine.Option(names = {"--local"}, description = "Run the pipeline locally.")
  public boolean localRun;

  @CommandLine.Option(names = {"--vv"}, description = "Enable verbose output (detailed logs).")
  private boolean verbose;

  @Override
  public Integer call() {
    // Validate execution inside a Git repository
    if (!GitUtils.isInsideGitRepo()) {
      System.err.println("[Error] Not inside a valid Git repository.");
      return 1;
    }

    // Ensure --pipeline and --file are not used together
    if (pipeline != null && !filePath.equals(".pipelines/pipeline.yaml")) {
      System.err.println("[Error] --pipeline and --file are mutually exclusive. Please provide only one.");
      return 1;
    }

    // Set commit hash if not specified
    if (commit.isEmpty()) {
      commit = GitUtils.getLatestCommitHash();
    }

    System.out.println("CI/CD CLI - Ready! Use `--help` for available commands.");
    return 0;
  }

  public static void main(String[] args) {
    int exitCode = new CommandLine(new CliApp()).execute(args);
    System.exit(exitCode);
  }
}
