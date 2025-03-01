package edu.neu.cs6510.sp25.t1.common.model.execution;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.List;

import edu.neu.cs6510.sp25.t1.common.model.ExecutionState;
import edu.neu.cs6510.sp25.t1.common.model.definition.StageDefinition;

/**
 * Represents a stage execution instance in a CI/CD pipeline.
 */
public class StageExecution {
  private final StageDefinition stageDefinition;
  private ExecutionState stageStatus;
  private final Instant startTime;
  private Instant completionTime;
  private final List<JobExecution> jobExecutions;

  /**
   * Constructs a new StageExecution based on its definition.
   *
   * @param stageDefinition the stage definition
   * @param jobExecutions   the list of job executions
   */
  @JsonCreator
  public StageExecution(
          @JsonProperty("stageDefinition") StageDefinition stageDefinition,
          @JsonProperty("jobExecutions") List<JobExecution> jobExecutions) {
    this.stageDefinition = stageDefinition;
    this.jobExecutions = jobExecutions;
    this.stageStatus = ExecutionState.PENDING;
    this.startTime = Instant.now();
  }

  /**
   * Computes the stage execution status dynamically.
   * This method should be called whenever the status of any job changes.
   */
  public void updateStatus() {
    boolean hasFailed = jobExecutions.stream()
            .anyMatch(job -> job.getStatus().equals("FAILED") && job.isAllowFailure());

    boolean hasCanceled = jobExecutions.stream()
            .anyMatch(job -> job.getStatus().equals("CANCELED"));

    if (hasFailed) {
      this.stageStatus = ExecutionState.FAILED;
    } else if (hasCanceled) {
      this.stageStatus = ExecutionState.CANCELED;
    } else if (jobExecutions.stream().allMatch(job -> job.getStatus().equals("SUCCESS"))) {
      this.stageStatus = ExecutionState.SUCCESS;
    } else {
      this.stageStatus = ExecutionState.RUNNING;
    }
  }

  /**
   * Marks the stage as completed.
   */
  public void complete() {
    this.completionTime = Instant.now();
  }

  /**
   * Getter for stage name.
   *
   * @return stage name
   */
  public String getStageName() {
    return stageDefinition.getName();
  }

  /**
   * Getter for stage status.
   *
   * @return stage status
   */
  public ExecutionState getStageStatus() {
    return stageStatus;
  }

  /**
   * Getter for start time.
   *
   * @return start time
   */
  public Instant getStartTime() {
    return startTime;
  }

  /**
   * Getter for completion time.
   *
   * @return completion time
   */
  public Instant getCompletionTime() {
    return completionTime;
  }

  /**
   * Getter for job executions.
   *
   * @return the list of job executions
   */
  public List<JobExecution> getJobExecutions() {
    return jobExecutions;
  }
}
