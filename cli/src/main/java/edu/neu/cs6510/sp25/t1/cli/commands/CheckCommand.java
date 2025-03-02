package edu.neu.cs6510.sp25.t1.cli.commands;

import edu.neu.cs6510.sp25.t1.common.config.PipelineConfig;
import edu.neu.cs6510.sp25.t1.common.validation.error.ValidationException;
import picocli.CommandLine;

/**
 * Command to check the validity of a pipeline YAML file.
 * - Parses and validates the pipeline YAML locally.
 * - Does NOT interact with the CI/CD system backend.
 */
@CommandLine.Command(name = "check", description = "Validate the pipeline configuration file.")
public class CheckCommand extends BaseCommand {

  /**
   * Default constructor.
   */
  public CheckCommand() {
  }

  /**
   * Validates the pipeline configuration file.
   *
   * @return Exit code:
   * - 0: Validation successful
   * - 1: General error
   * - 2: File not found or unreadable
   * - 3: Validation failed
   */
  @Override
  public Integer call() {
    if (validateInputs()) {// the git repo check is done here
      return 2; // Exit code for missing file or wrong directory
    }
    try {
      // Use shared method from BaseCommand
      PipelineConfig pipelineConfig = loadAndValidatePipelineConfig();

      logInfo("Pipeline configuration is valid: " + configFile);
      return 0;

    } catch (ValidationException e) {
      logError(String.format("%s: Validation Error: %s", configFile, e.getMessage()));
      return 3;

    } catch (Exception e) {
      logError(String.format("%s: Error processing YAML file: %s", configFile, e.getMessage()));
      return 1;
    }
  }
}
