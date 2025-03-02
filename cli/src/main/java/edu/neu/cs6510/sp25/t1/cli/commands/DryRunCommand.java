package edu.neu.cs6510.sp25.t1.cli.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

import java.util.LinkedHashMap;
import java.util.Map;

import edu.neu.cs6510.sp25.t1.common.config.JobConfig;
import edu.neu.cs6510.sp25.t1.common.config.PipelineConfig;
import edu.neu.cs6510.sp25.t1.common.config.StageConfig;
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
   * <p>
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
      // Load pipeline configuration
      PipelineConfig pipelineConfig = loadAndValidatePipelineConfig();
      simulateExecution(pipelineConfig);
      return 0;
    } catch (ValidationException e) {
      logError(String.format("%s: Validation Error: %s", configFile, e.getMessage()));
      return 3;
    } catch (Exception e) {
      logError(String.format("%s: Error processing pipeline: %s", configFile, e.getMessage()));
      return 1;
    }
  }

  /**
   * Simulates pipeline execution by printing the planned execution order in YAML format.
   *
   * @param pipelineConfig The pipeline configuration.
   */
  private void simulateExecution(PipelineConfig pipelineConfig) {
    logInfo("🔍 Simulating execution for pipeline: " + pipelineConfig.getName());

    Map<String, Object> executionPlan = new LinkedHashMap<>();

    for (StageConfig stageConfig : pipelineConfig.getStages()) {
      Map<String, Object> stageDetails = new LinkedHashMap<>();

      for (JobConfig job : stageConfig.getJobs()) {
        Map<String, Object> jobDetails = new LinkedHashMap<>();
        jobDetails.put("image", job.getImage());
        jobDetails.put("script", job.getScript());

        if (!job.getArtifacts().isEmpty()) {
          jobDetails.put("artifacts", job.getArtifacts());
        }

        stageDetails.put(job.getName(), jobDetails);
      }

      executionPlan.put(stageConfig.getName(), stageDetails);
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
