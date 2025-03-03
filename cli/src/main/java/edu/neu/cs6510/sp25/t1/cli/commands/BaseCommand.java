package edu.neu.cs6510.sp25.t1.cli.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.Callable;

import edu.neu.cs6510.sp25.t1.common.model.Pipeline;
import edu.neu.cs6510.sp25.t1.common.validation.error.ValidationException;
import edu.neu.cs6510.sp25.t1.common.validation.parser.YamlParser;
import edu.neu.cs6510.sp25.t1.common.validation.validator.GitValidator;
import edu.neu.cs6510.sp25.t1.common.validation.validator.PipelineValidator;
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
  @CommandLine.Option(names = {"-co", "--commit"}, description = "Git commit hash to use", defaultValue = "latest")
  protected String commit;

  @SuppressWarnings("checkstyle:VisibilityModifier")
  @CommandLine.Option(names = {"-o",
          "--output"}, description = "Output format: plaintext, json, yaml", defaultValue = "plaintext")

  protected String outputFormat;

  private final ObjectMapper objectMapper = new ObjectMapper();
  private final YAMLMapper yamlMapper = new YAMLMapper();

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
      if (response instanceof String responseString) {
        if ("json".equalsIgnoreCase(outputFormat)) {
          return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(
                  objectMapper.readTree(responseString));
        } else if ("yaml".equalsIgnoreCase(outputFormat)) {
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

  /**
   * Logs messages based on the verbosity level.
   *
   * @param message The message to log.
   */
  protected void logInfo(String message) {
    if (verbose) {
      System.out.println("[INFO] " + message);
      logger.info(message);
    }
  }

  /**
   * Logs error messages and prints them to stderr.
   *
   * @param message The error message.
   */
  protected void logError(String message) {
    System.err.println("[ERROR] " + message);
    logger.error(message);
  }


  /**
   * Validates required CLI parameters, ensuring the CLI is inside a Git repository.
   *
   * @return `true` if all required parameters are provided and the CLI is inside a Git repository, `false` otherwise.
   */
  protected boolean validateInputs() {
    if (configFile == null || configFile.isEmpty()) {
      logError("Missing required parameter: --file <pipeline.yaml>");
      return true;
    }

    if (GitValidator.isGitRepository()) {
      logError("This CLI must be run from the root of a Git repository.");
      return true;
    }

    return false;
  }


  protected Pipeline loadAndValidatePipelineConfig() throws ValidationException {
    if (configFile == null || configFile.isEmpty()) {
      throw new ValidationException("Missing required parameter: --file <pipeline.yaml>");
    }

    File yamlFile = new File(configFile);
    if (!yamlFile.exists() || !yamlFile.isFile()) {
      throw new ValidationException("YAML file not found at " + configFile);
    }

    if (!Files.isReadable(Paths.get(configFile))) {
      throw new ValidationException("Unable to read file " + configFile);
    }

    // Parse and validate YAML
    Pipeline pipeline = YamlParser.parseYaml(yamlFile);
    PipelineValidator.validate(pipeline, configFile);

    return pipeline;
  }

}
