package edu.neu.cs6510.sp25.t1.cli.validation.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.logging.Logger;

import edu.neu.cs6510.sp25.t1.common.logging.PipelineLogger;

public class GitUtils {
  private static final Logger LOGGER = Logger.getLogger(GitUtils.class.getName());
  private static final String PIPELINES_DIR = ".pipelines/";

  /**
   * Checks if the current directory is inside a valid Git repository.
   *
   * @return true if inside a Git repository, false otherwise.
   */
  public static boolean isInsideGitRepo() {
    String result = executeShellCommand("git rev-parse --is-inside-work-tree");
    boolean isGitRepo = "true".equalsIgnoreCase(result);

    if (!isGitRepo) {
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

    if (!exists) {
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
      throw new IllegalStateException("Error: Missing .pipelines directory in the repository root.");
    }

    if (filename == null || filename.isEmpty()) {
      // No filename provided → Check if at least one .yaml file exists
      File[] yamlFiles = pipelinesDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".yaml") || name.toLowerCase().endsWith(".yml"));

      if (yamlFiles == null || yamlFiles.length == 0) {
        throw new IllegalStateException("Error: No pipeline configuration files found inside .pipelines/.");
      }

      PipelineLogger.info("Validated .pipelines/: Found " + yamlFiles.length + " pipeline configuration file(s).");
    } else {
      // A filename is provided → Check if that file exists
      File targetFile = new File(PIPELINES_DIR + filename);

      if (!targetFile.exists() || !targetFile.isFile()) {
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
    return Optional.ofNullable(executeShellCommand("git symbolic-ref --short HEAD"))
            .filter(branch -> !branch.isEmpty())
            .orElse("main");
  }

  /**
   * Gets the latest commit hash from the current branch.
   *
   * @return The latest commit hash or an empty string if undetectable.
   */
  public static String getLatestCommitHash() {
    return Optional.ofNullable(executeShellCommand("git rev-parse HEAD"))
            .orElseThrow(() -> new IllegalStateException("Error: Unable to fetch the latest commit hash."));
  }

  /**
   * Gets the remote repository URL from Git config.
   *
   * @return The remote Git URL or an empty string if not set.
   */
  public static String getRemoteUrl() {
    return Optional.ofNullable(executeShellCommand("git config --get remote.origin.url"))
            .orElse("");
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

      return result != null ? result.trim() : "";
    } catch (IOException | InterruptedException e) {
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
    if (!isInsideGitRepo()) {
      throw new IllegalStateException("Error: Not inside a valid Git repository.");
    }

    validatePipelineFile(filename);
  }
}
