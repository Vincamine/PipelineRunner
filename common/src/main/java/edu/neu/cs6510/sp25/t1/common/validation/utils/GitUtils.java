package edu.neu.cs6510.sp25.t1.common.validation.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

import edu.neu.cs6510.sp25.t1.common.logging.PipelineLogger;

/**
 * Utility class for Git-related operations and validations.
 */
public class GitUtils {
  private static final String PIPELINES_DIR = ".pipelines/";

  /**
   * Checks if the current directory is inside a valid Git repository.
   *
   * @return true if inside a Git repository, false otherwise.
   */
  public static boolean isInsideGitRepo() {
    String result = executeShellCommand("git rev-parse --is-inside-work-tree");
    boolean isGitRepo = "true".equalsIgnoreCase(result);

    if (isGitRepo) {
      PipelineLogger.info("Inside a valid Git repository.");
    } else {
      PipelineLogger.error("Not inside a valid Git repository.");
    }

    return isGitRepo;
  }

  /**
   * Checks if the .pipelines directory exists in the repository root.
   *
   * @return true if .pipelines folder exists, false otherwise.
   */
  public static boolean hasPipelinesFolder() {
    boolean exists = Files.exists(Paths.get(PIPELINES_DIR));

    if (exists) {
      PipelineLogger.info(".pipelines/ directory exists.");
    } else {
      PipelineLogger.error("Missing .pipelines/ directory in the repository root.");
    }

    return exists;
  }

  /**
   * Validates if at least one YAML file exists inside .pipelines/ (if no filename is provided).
   * If a filename is provided, checks if that specific file exists inside .pipelines/.
   *
   * @param filename The name of the pipeline configuration file (optional).
   * @throws IllegalStateException if validation fails.
   */
  public static void validatePipelineFile(String filename) {
    File pipelinesDir = new File(PIPELINES_DIR);

    // Ensure .pipelines directory exists first
    if (!pipelinesDir.exists() || !pipelinesDir.isDirectory()) {
      PipelineLogger.error("Missing .pipelines directory in the repository root.");
      throw new IllegalStateException("Error: Missing .pipelines directory in the repository root.");
    }

    if (filename == null || filename.isEmpty()) {
      // No filename provided → Check if at least one .yaml file exists
      File[] yamlFiles = pipelinesDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".yaml") || name.toLowerCase().endsWith(".yml"));

      if (yamlFiles == null || yamlFiles.length == 0) {
        PipelineLogger.error("No pipeline configuration files found inside .pipelines/.");
        throw new IllegalStateException("Error: No pipeline configuration files found inside .pipelines/.");
      }

      PipelineLogger.info("Validated .pipelines/: Found " + yamlFiles.length + " pipeline configuration file(s).");
    } else {
      // A filename is provided → Check if that file exists
      File targetFile = new File(PIPELINES_DIR + filename);

      if (!targetFile.exists() || !targetFile.isFile()) {
        PipelineLogger.error("Specified pipeline file '" + filename + "' does not exist inside .pipelines/.");
        throw new IllegalStateException("Error: Specified pipeline file '" + filename + "' does not exist inside .pipelines/.");
      }

      PipelineLogger.info("Validated .pipelines/: Pipeline file '" + filename + "' found.");
    }
  }

  /**
   * Gets the current active Git branch. Defaults to 'main' if detection fails.
   *
   * @return The current Git branch name or "main" if undetectable.
   */
  public static String getCurrentBranch() {
    String branch = Optional.of(executeShellCommand("git symbolic-ref --short HEAD"))
            .filter(b -> !b.isEmpty())
            .orElse("main");

    PipelineLogger.debug("Current Git branch: " + branch);
    return branch;
  }

  /**
   * Gets the latest commit hash from the current branch.
   *
   * @return The latest commit hash or an empty string if undetectable.
   */
  public static String getLatestCommitHash() {
    String commitHash = Optional.of(executeShellCommand("git rev-parse HEAD"))
            .orElseThrow(() -> new IllegalStateException("Error: Unable to fetch the latest commit hash."));

    PipelineLogger.debug("Latest commit hash: " + commitHash);
    return commitHash;
  }

  /**
   * Gets the remote repository URL from Git config.
   *
   * @return The remote Git URL or an empty string if not set.
   */
  public static String getRemoteUrl() {
    String remoteUrl = Optional.of(executeShellCommand("git config --get remote.origin.url"))
            .orElse("");

    PipelineLogger.debug("Remote repository URL: " + (remoteUrl.isEmpty() ? "Not set" : remoteUrl));
    return remoteUrl;
  }

  /**
   * Executes a shell command and returns the output.
   *
   * @param command The shell command to execute.
   * @return The output of the command or an empty string if execution fails.
   */
  private static String executeShellCommand(String command) {
    try {
      Process process = new ProcessBuilder("bash", "-c", command)
              .redirectErrorStream(true) // Capture stderr in stdout
              .start();

      BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
      String result = reader.readLine(); // Read first line output

      process.waitFor(); // Ensure process completion

      if (result != null) {
        PipelineLogger.debug("Command executed successfully: " + command);
      } else {
        PipelineLogger.error("Command execution failed: " + command);
      }

      return result != null ? result.trim() : "";
    } catch (IOException | InterruptedException e) {
      PipelineLogger.error("Shell command failed: " + command + " - Error: " + e.getMessage());
      Thread.currentThread().interrupt(); // Restore interrupted state
      return "";
    }
  }

  /**
   * Validates that the CLI is running inside a valid Git repository with a .pipelines directory.
   * If no file is provided, ensures at least one YAML file exists inside .pipelines/.
   * If a filename is provided, checks for its existence.
   *
   * @param filename The name of the pipeline configuration file (optional).
   * @throws IllegalStateException if validation fails.
   */
  public static void validateRepo(String filename) {
    if (isInsideGitRepo()) {
      PipelineLogger.info("Inside a valid Git repository.");
      validatePipelineFile(filename);
    } else {
      PipelineLogger.error("Not inside a valid Git repository.");
      throw new IllegalStateException("Error: Not inside a valid Git repository.");
    }
  }

  /**
   * Checks if the current directory is the root of a Git repository.
   *
   * @return true if in the Git root directory, false otherwise.
   */
  public static boolean isGitRootDirectory() {
    String gitRoot = executeShellCommand("git rev-parse --show-toplevel");
    String currentDir = Paths.get(System.getProperty("user.dir")).toAbsolutePath().toString();

    if (gitRoot.isEmpty()) {
      PipelineLogger.warn("Not inside a Git repository or unable to determine root.");
      return false;
    }

    if (!gitRoot.equals(currentDir)) {
      PipelineLogger.warn("CLI is not running from the Git repository root. Current: " + currentDir + ", Git Root: " + gitRoot);
      return false;
    }

    PipelineLogger.info("Running inside the Git repository root: " + gitRoot);
    return true;
  }

  public static void pullLatest(File cloned, String branch) {

  }
}

