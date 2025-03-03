package edu.neu.cs6510.sp25.t1.worker.execution;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;
import edu.neu.cs6510.sp25.t1.common.enums.PipelineExecutionState;

/**
 * Represents an active execution of a CI/CD pipeline. Defines execution state & metadata.
 */
public class PipelineExecution {
  private final String pipelineName; // unique identifier for the pipeline; not pipeline ID
  private PipelineExecutionState state; // current state of the pipeline execution
  private final Instant startTime; // start time of the pipeline execution
  private Instant lastUpdated; // last updated time of the pipeline execution
  private final List<StageExecution> stages; // list of stages in the pipeline execution

  /**
   * Constructor for PipelineExecution.
   * Initializes the pipeline execution with the given pipeline name, stages, and jobs.
   *
   * @param pipelineName the name of the pipeline
   * @param stages       the list of stages in the pipeline
   */
  public PipelineExecution(String pipelineName, List<StageExecution> stages) {
    this.pipelineName = pipelineName;
    this.state = PipelineExecutionState.PENDING;
    this.startTime = Instant.now();
    this.lastUpdated = Instant.now();
    this.stages = stages != null ? stages : new ArrayList<>();
  }


  /**
   * Updates the execution state of the pipeline based on the states of its stages.
   * <p>
   * The pipeline execution can be in one of the following states:
   * - PENDING: The pipeline is scheduled but has not started execution.
   * - RUNNING: The pipeline has at least one stage that is not yet completed.
   * - SUCCESSFUL: The pipeline has completed execution without any failures.
   * - FAILED: The pipeline has completed execution with at least one failed stage.
   * <p>
   * This method determines the state of the pipeline as follows:
   * - If any stage has failed, the pipeline state is set to FAILED.
   * - If all stages have succeeded, the pipeline state is set to SUCCESSFUL.
   * - Otherwise, the pipeline is still executing, and its state is set to RUNNING.
   * <p>
   * The method also updates the last modified timestamp to reflect the time of the state update.
   */
  public void updateState() {
    // Check if any stage has failed
    boolean hasFailed = stages.stream().anyMatch(s -> s.getStatus() == ExecutionStatus.FAILED);
    // Check if all stages have succeeded
    boolean allSucceeded = stages.stream().allMatch(s -> s.getStatus() == ExecutionStatus.SUCCESS);
    // Check if any stage is running
    if (hasFailed) {
      this.state = PipelineExecutionState.FAILED;
    } else if (allSucceeded) {
      this.state = PipelineExecutionState.SUCCESSFUL;
    } else {
      this.state = PipelineExecutionState.RUNNING;
    }
    // Update the last updated timestamp
    this.lastUpdated = Instant.now();
  }

  /**
   * Get the pipeline name.
   *
   * @return the pipeline name
   */
  public String getPipelineName() {
    return pipelineName;
  }

  /**
   * Get the current execution state of the pipeline.
   *
   * @return the pipeline execution state
   */
  public PipelineExecutionState getState() {
    return state;
  }

  /**
   * Get the start time of the pipeline execution.
   *
   * @return the start time of the pipeline execution
   */
  public Instant getStartTime() {
    return startTime;
  }

  /**
   * Get the last updated time of the pipeline execution.
   *
   * @return the last updated time of the pipeline execution
   */
  public Instant getLastUpdated() {
    return lastUpdated;
  }

  /**
   * Set the pipeline execution state.
   *
   * @param state the new state of the pipeline execution
   */
  public void setState(PipelineExecutionState state) {
    this.state = state;
    this.lastUpdated = Instant.now();
  }

  /**
   * Get the list of stages in the pipeline execution.
   *
   * @return list of stage execution states
   */
  public List<StageExecution> getStages() {
    return stages;
  }

}
