package edu.neu.cs6510.sp25.t1.cli.commands;

import java.io.File;
import java.util.concurrent.Callable;


import edu.neu.cs6510.sp25.t1.common.validation.error.ValidationException;
import edu.neu.cs6510.sp25.t1.common.logging.PipelineLogger;
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

  @CommandLine.Option(
      names = {"--verbose"},
      description = "Enable verbose output."
  )
  private boolean checkVerbose;

  /**
   * Validates a pipeline configuration file.
   *
   * @return 0 if the pipeline is valid, 1 if validation fails.
   */
  @Override
  public Integer call() {
    if (checkVerbose) {
      PipelineLogger.setVerbose(true);
    }
    if (filePath == null) {
      System.err.println("[Error] File path cannot be null");
      return 1;
    }

    // Check if the file path is valid
    File file = new File(filePath);

    if (!file.exists()) {
      System.err.println("[ERROR] Pipeline file " + filePath + " does not exist.");
      return 1;
    }

    File yamlFile = new File(filePath);

    if (!file.canRead()) {
      System.err.println("[ERROR] Cannot read pipeline file: " + filePath);
      return 1;
    }

    if (!yamlFile.exists() || !yamlFile.isFile()) {
      System.err.println("[Error] File not found: " + filePath);
      return 1;
    }

    try {
      System.out.println("Checking pipeline configuration: " + filePath);

      if (checkVerbose) {
        System.out.println("[DEBUG] Checking pipeline file: " + filePath);
      }

      YamlPipelineValidator.validatePipeline(filePath);

      System.out.println("Pipeline configuration is valid!");
      return 0;
    } catch (ValidationException e) {
      System.err.println("[ERROR] Invalid pipeline: " + e.getMessage());
      return 1;
    } catch (Exception e) {
      System.err.println("[ERROR] Unexpected error: " + e.getMessage());
      return 1;
    }
  }
}
