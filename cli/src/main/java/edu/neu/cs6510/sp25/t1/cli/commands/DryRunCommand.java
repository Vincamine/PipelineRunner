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
    System.out.println("====================================");
    System.out.println("Command Started: " + this.getClass().getSimpleName());
    System.out.println("====================================");
    if (validateInputs()) {
      return 2;
    }

    System.out.println("passed the inputs validation");
    System.out.println("====================================");
    try {
      logInfo("Loading and validating pipeline configuration...");
      Pipeline pipeline = loadAndValidatePipelineConfig();

      if (pipeline == null || pipeline.getStages().isEmpty()) {
        logError("Pipeline is empty or could not be loaded.");
        return 3;
      }

      logInfo("Pipeline successfully loaded. Generating execution plan...");
      System.out.println("====================================");
      generateExecutionPlan(pipeline);
      return 0;
    } catch (ValidationException e) {
      logError("Validation Error: " + e.getMessage());
      return 3;
    } catch (Exception e) {
      logError("Error processing pipeline: " + e.getMessage());
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
      logInfo("Processing Stage: " + stage.getName());

      Map<String, Object> stageDetails = new LinkedHashMap<>();

      // Process jobs within each stage
      for (Job job : stage.getJobs()) {
        logInfo("  - Found Job: " + job.getName() + " (Image: " + job.getImage() + ")");

        Map<String, Object> jobDetails = new LinkedHashMap<>();
        jobDetails.put("image", job.getImage());
        jobDetails.put("script", job.getScript()); // Keeps script as a list

        stageDetails.put(job.getName(), jobDetails);
      }

      executionPlan.put(stage.getName(), stageDetails);
    }

    if (executionPlan.isEmpty()) {
      logError("No stages or jobs found in the pipeline.");
      return;
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
