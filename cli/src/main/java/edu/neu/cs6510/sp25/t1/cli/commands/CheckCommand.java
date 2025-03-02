package edu.neu.cs6510.sp25.t1.cli.commands;

import java.io.File;

import edu.neu.cs6510.sp25.t1.common.config.PipelineConfig;
import edu.neu.cs6510.sp25.t1.common.parser.YamlParser;
import edu.neu.cs6510.sp25.t1.common.validation.PipelineValidator;
import edu.neu.cs6510.sp25.t1.common.validation.ValidationException;
import picocli.CommandLine;

/**
 * Command to check the validity of a pipeline YAML file.
 * Only parses and validates YAML locally.
 * Validate YAML without running it.
 * does not interact with the CI/CD system backend.
 */
@CommandLine.Command(name = "check", description = "Validate the pipeline configuration file")
public class CheckCommand extends BaseCommand {

  /**
   * Constructor for dependency injection (used for unit testing).
   */
  public CheckCommand() {
  }


  /**
   * Validates the pipeline configuration file.
   *
   * @return 0 if successful, 1 if an error occurred, 2 if no file provided, 3 if validation failed; required by picocli.
   */
  @Override
  public Integer call() {
    if (configFile == null || configFile.isEmpty()) {
      System.err.println("Error: No pipeline configuration file provided.");
      return 2;
    }

    try {
      File yamlFile = new File(configFile);
      if (!yamlFile.exists()) {
        System.err.println("Error: YAML file not found at " + configFile);
        return 2;
      }

      // Parse YAML file
      PipelineConfig pipelineConfig = YamlParser.parseYaml(yamlFile);

      // Validate pipeline structure
      PipelineValidator.validate(pipelineConfig);

      System.out.println("Pipeline configuration is valid!");
      return 0;

    } catch (ValidationException e) {
      System.err.println("Validation Error: " + e.getMessage());
      return 3;
    } catch (Exception e) {
      System.err.println("Error processing YAML file: " + e.getMessage());
      return 1;
    }
  }
}
