package edu.neu.cs6510.sp25.t1.cli.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

import picocli.CommandLine;

/**
 * Base class for CLI commands.
 * Provides common options and methods for command-line interaction.
 */
public abstract class BaseCommand implements Callable<Integer> {
  @SuppressWarnings("checkstyle:VisibilityModifier")
  protected final Logger logger = LoggerFactory.getLogger(getClass());

  @SuppressWarnings("checkstyle:VisibilityModifier")
  @CommandLine.Option(names = {"-v", "--verbose"}, description = "Enable verbose output.")
  protected boolean verbose;

  @SuppressWarnings("checkstyle:VisibilityModifier")
  @CommandLine.Option(names = {"-f", "--file"}, description = "Path to the pipeline configuration file")
  protected String configFile;

  @SuppressWarnings("checkstyle:VisibilityModifier")
  @CommandLine.Option(names = {"-r", "--repo"}, description = "Path or URL to the repository")
  protected String repo;

  @SuppressWarnings("checkstyle:VisibilityModifier")
  @CommandLine.Option(names = {"-br", "--branch"}, description = "Git branch to use", defaultValue = "main")
  protected String branch;

  @SuppressWarnings("checkstyle:VisibilityModifier")
  @CommandLine.Option(names = {"-co", "--commit"}, description = "Git commit hash to use")
  protected String commit;

  @SuppressWarnings("checkstyle:VisibilityModifier")
  @CommandLine.Option(names = {"-o",
          "--output"}, description = "Output format: plaintext, json, yaml", defaultValue = "plaintext")
  protected String outputFormat;

  /**
   * Default constructor for unit testing.
   */
  public BaseCommand() {
  }

  /**
   * Set the config file - for unit testing.
   *
   * @param configFile The path to the pipeline configuration file.
   */
  public void setConfigFile(String configFile) {
    this.configFile = configFile;
  }

  /**
   * Set the repo - for unit testing.
   *
   * @param repo The path or URL to the repository.
   */
  public void setRepo(String repo) {
    this.repo = repo;
  }

  /**
   * Set the branch - for unit testing.
   *
   * @param branch The git branch to use.
   */
  public void setBranch(String branch) {
    this.branch = branch;
  }

  /**
   * Set the commit - for unit testing.
   *
   * @param commit The git commit hash to use.
   */
  public void setCommit(String commit) {
    this.commit = commit;
  }

  /**
   * Set the output format - for unit testing.
   *
   * @param outputFormat The output format: plaintext, json, yaml.
   */
  public void setOutputFormat(String outputFormat) {
    this.outputFormat = outputFormat;
  }

  /**
   * Formats the output based on the specified format.
   *
   * @param response The API response object.
   * @return The formatted output string.
   */
  protected String formatOutput(Object response) {
    if (response == null) {
      return "Error: No response received.";
    }
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      YAMLMapper yamlMapper = new YAMLMapper();

      if (response instanceof String responseString) {
        if (outputFormat.equalsIgnoreCase("json")) {
          // Ensure the response is properly formatted as JSON
          return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(
                  objectMapper.readTree(responseString));
        } else if (outputFormat.equalsIgnoreCase("yaml")) {
          // Fix: Ensure we parse YAML correctly
          String yaml = yamlMapper.writeValueAsString(yamlMapper.readTree(responseString));
          return yaml.startsWith("---") ? yaml.substring(4) : yaml;
        }
      }
      return response.toString();
    } catch (Exception e) {
      logger.error("Failed to format output", e);
      return "Error formatting output.";
    }
  }
}
