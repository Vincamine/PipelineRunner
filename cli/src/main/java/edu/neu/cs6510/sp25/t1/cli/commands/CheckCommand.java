package edu.neu.cs6510.sp25.t1.cli.commands;

import edu.neu.cs6510.sp25.t1.cli.validation.validator.YamlPipelineValidator;
import edu.neu.cs6510.sp25.t1.common.logging.PipelineLogger;
import picocli.CommandLine;

import java.io.File;
import java.util.concurrent.Callable;

/**
 * Command to validate a pipeline YAML file without executing it.
 */
@CommandLine.Command(name = "check", description = "Validates a pipeline YAML file.")
public class CheckCommand implements Callable<Integer> {

  @CommandLine.Option(names = {"-f", "--file"}, description = "Path to the pipeline configuration file", required = true)
  private String filePath;

  @Override
  public Integer call() {
    File file = new File(filePath);

    if (!file.exists()) {
      System.err.println("Error: Pipeline file " + filePath + " does not exist.");
      return 1;
    }

    try {
      YamlPipelineValidator.validatePipeline(filePath);
      PipelineLogger.info("Pipeline file is valid: " + filePath);
      return 0;
    } catch (Exception e) {
      System.err.println("Error: " + e.getMessage());
      return 1;
    }
  }
}
