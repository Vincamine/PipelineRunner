package edu.neu.cs6510.sp25.t1.cli.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

import java.util.LinkedHashMap;
import java.util.Map;

import edu.neu.cs6510.sp25.t1.common.model.Job;
import edu.neu.cs6510.sp25.t1.common.model.Pipeline;
import edu.neu.cs6510.sp25.t1.common.model.Stage;
import edu.neu.cs6510.sp25.t1.common.validation.error.ValidationException;
import picocli.CommandLine;

/**
 * CLI command to simulate pipeline execution.
 * <p>
 * Purpose: Prints the execution plan of a pipeline without actually executing any jobs.
 * <p>
 * Output: The execution plan is printed in a structured YAML format.
 */
@CommandLine.Command(name = "dry-run", description = "Simulate pipeline execution without running jobs.")
public class DryRunCommand extends BaseCommand {

  private final ObjectMapper yamlMapper = new YAMLMapper();

  /**
   * Executes the dry-run command to simulate pipeline execution order.
   *
   * @return Exit code:
   * - 0 - Success
   * - 1 - General failure
   * - 2 - Missing file or incorrect directory
   * - 3 - Validation failure
   */
  @Override
  public Integer call() {
    if (!validateInputs()) {
      return 2;
    }

    try {
      // Load and validate pipeline configuration
      Pipeline pipeline = loadAndValidatePipelineConfig();
      generateExecutionPlan(pipeline);
      return 0;
    } catch (ValidationException e) {
      logError(String.format("Validation Error: %s", e.getMessage()));
      return 3;
    } catch (Exception e) {
      logError(String.format("Error processing pipeline: %s", e.getMessage()));
      return 1;
    }
  }

  /**
   * Generates and prints the pipeline execution plan in YAML format.
   *
   * @param pipeline The pipeline configuration.
   */
  private void generateExecutionPlan(Pipeline pipeline) {
    logInfo("🔍 Generating execution plan for pipeline: " + pipeline.getName());

    Map<String, Object> executionPlan = new LinkedHashMap<>();

    // Process stages in order
    for (Stage stage : pipeline.getStages()) {
      Map<String, Object> stageDetails = new LinkedHashMap<>();

      // Process jobs within each stage
      for (Job job : stage.getJobs()) {
        Map<String, Object> jobDetails = new LinkedHashMap<>();
        jobDetails.put("image", job.getImage());
        jobDetails.put("script", job.getScript()); // Keeps script as a list

        stageDetails.put(job.getName(), jobDetails);
      }

      executionPlan.put(stage.getName(), stageDetails);
    }

    try {
      String yamlOutput = yamlMapper.writerWithDefaultPrettyPrinter().writeValueAsString(executionPlan);
      System.out.println("\nPipeline Execution Plan:\n" + yamlOutput);
    } catch (Exception e) {
      logError("Failed to generate YAML output: " + e.getMessage());
    }

    logInfo("Simulation Complete.");
  }
}
