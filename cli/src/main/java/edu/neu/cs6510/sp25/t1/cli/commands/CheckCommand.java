package edu.neu.cs6510.sp25.t1.cli.commands;

import java.io.File;
import java.util.concurrent.Callable;

import edu.neu.cs6510.sp25.t1.common.logging.PipelineLogger;
import edu.neu.cs6510.sp25.t1.common.validation.error.ValidationException;
import edu.neu.cs6510.sp25.t1.common.validation.utils.GitUtils;
import edu.neu.cs6510.sp25.t1.common.validation.validator.YamlPipelineValidator;
import picocli.CommandLine;

/**
 * Implements the `check` command to validate a pipeline YAML file.
 */
@CommandLine.Command(
        name = "check",
        description = "Validates a pipeline configuration file without running it."
)
public class CheckCommand implements Callable<Integer> {

  @CommandLine.Option(
          names = {"--file", "-f"},
          description = "Path to the pipeline YAML configuration file.",
          required = true
  )
  private String filePath;

  /**
   * Validates a pipeline configuration file.
   *
   * @return 0 if the pipeline is valid, 1 if validation fails.
   */
  @Override
  public Integer call() {
    GitUtils.isGitRootDirectory();
    if (filePath == null || filePath.trim().isEmpty()) {
      PipelineLogger.error("File path cannot be null or empty.");
      return 1;
    }

    File yamlFile = new File(filePath);
    if (!yamlFile.exists() || !yamlFile.isFile()) {
      PipelineLogger.error("File not found: " + filePath);
      return 1;
    }

    try {
      PipelineLogger.info("Checking pipeline configuration: " + filePath);
      YamlPipelineValidator.validatePipeline(filePath);
      PipelineLogger.info("Pipeline configuration is valid!");
      return 0;
    } catch (ValidationException e) {
      PipelineLogger.error("Pipeline validation failed!");
      for (String line : e.getMessage().split("\n")) {
        PipelineLogger.error("  ➜ " + line);
      }
      return 1;
    }
  }
}
