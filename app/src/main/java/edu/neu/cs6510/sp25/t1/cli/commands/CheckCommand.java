package edu.neu.cs6510.sp25.t1.cli.commands;

import edu.neu.cs6510.sp25.t1.cli.validation.PipelineValidator;
import edu.neu.cs6510.sp25.t1.cli.validation.ValidationResult;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.logging.Logger;
import java.util.logging.Level;

@Command(
    name = "check",
    description = "Validate YAML pipeline configuration without execution",
    mixinStandardHelpOptions = true
)
public class CheckCommand implements Callable<Integer> {
  private static final Logger LOGGER = Logger.getLogger(CheckCommand.class.getName());

  @Parameters(index = "0", description = "Path to the YAML pipeline configuration file")
  private File yamlFile;

  @Override
  public Integer call() {
    try {
      LOGGER.log(Level.INFO, "Validating pipeline configuration: {0}", yamlFile.getPath());

      PipelineValidator validator = new PipelineValidator();
      ValidationResult result = validator.validatePipelineConfig(yamlFile);

      if (result.isValid()) {
        LOGGER.log(Level.INFO, "Pipeline configuration is valid.");
        return 0; // Success
      } else {
        LOGGER.log(Level.SEVERE, "Pipeline configuration is invalid:");
        result.getErrors().forEach(error ->
            LOGGER.log(Level.SEVERE, "  - {0}", error));
        return 1; // Error
      }
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Error validating pipeline configuration", e);
      return 1; // Error
    }
  }
}
