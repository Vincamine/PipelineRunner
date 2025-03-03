package edu.neu.cs6510.sp25.t1.worker.executor;

import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;
import edu.neu.cs6510.sp25.t1.common.model.Pipeline;
import edu.neu.cs6510.sp25.t1.common.validation.error.ValidationException;
import edu.neu.cs6510.sp25.t1.common.validation.parser.YamlParser;
import edu.neu.cs6510.sp25.t1.common.validation.validator.YamlPipelineValidator;
import edu.neu.cs6510.sp25.t1.worker.api.client.WorkerBackendClient;
import edu.neu.cs6510.sp25.t1.worker.execution.JobExecution;
import edu.neu.cs6510.sp25.t1.worker.execution.PipelineExecution;
import edu.neu.cs6510.sp25.t1.worker.execution.StageExecution;
import edu.neu.cs6510.sp25.t1.worker.manager.DockerManager;

public class PipelineExecutor {
  private static final Logger LOGGER = Logger.getLogger(PipelineExecutor.class.getName());
  private final StageExecutor stageExecutor;

  /**
   * Constructor for PipelineExecutor.
   *
   * @param dockerManager Docker manager instance.
   * @param backendClient Backend client for reporting execution status.
   */
  public PipelineExecutor(DockerManager dockerManager, WorkerBackendClient backendClient) {
    this.stageExecutor = new StageExecutor(dockerManager, backendClient);
  }

  /**
   * Executes a pipeline from a YAML configuration file.
   *
   * @param pipelineConfigFile The YAML file defining the pipeline configuration.
   */
  public void execute(File pipelineConfigFile) {
    // Step 1: Validate the pipeline before execution
    Pipeline pipeline;
    try {
      YamlPipelineValidator.validatePipeline(pipelineConfigFile.getAbsolutePath());
      pipeline = YamlParser.parseYaml(pipelineConfigFile);
      LOGGER.info("Pipeline validation successful.");
    } catch (ValidationException e) {
      LOGGER.log(Level.SEVERE, "Pipeline validation failed: \n" + e.getMessage());
      return;
    }

    // Step 2: Convert Pipeline Model to Execution Object
    List<StageExecution> stageExecutions = pipeline.getStages().stream()
            .map(stage -> new StageExecution(
                    stage,
                    stage.getJobs().stream()  // Convert Job -> JobExecution
                            .map(job -> new JobExecution(
                                    job.getName(),
                                    job.getImage(),
                                    job.getScript(),
                                    job.getNeeds(),
                                    job.isAllowFailure()
                            ))
                            .collect(Collectors.toList())  // Collect as List<JobExecution>
            ))
            .collect(Collectors.toList());


    PipelineExecution pipelineExecution = new PipelineExecution(pipeline.getName(), stageExecutions);

    // Step 3: Execute Stages in Sequence
    LOGGER.info("Starting execution of pipeline: " + pipelineExecution.getPipelineName());
    pipelineExecution.updateState();

    for (StageExecution stageExecution : pipelineExecution.getStages()) {
      ExecutionStatus stageState = stageExecutor.execute(stageExecution);

      if (stageState == ExecutionStatus.FAILED) {
        LOGGER.warning("Pipeline execution failed at stage: " + stageExecution.getName());
        pipelineExecution.updateState();
        return;
      }
    }

    // Step 4: Mark pipeline as successful
    LOGGER.info("Pipeline execution completed successfully.");
    pipelineExecution.updateState();
  }
}
