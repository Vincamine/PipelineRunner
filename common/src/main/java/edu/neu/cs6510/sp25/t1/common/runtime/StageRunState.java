package edu.neu.cs6510.sp25.t1.common.runtime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.List;

import edu.neu.cs6510.sp25.t1.common.execution.ExecutionState;
import edu.neu.cs6510.sp25.t1.common.config.StageConfig;

/**
 * Represents a stage execution instance in a CI/CD pipeline.
 */
public class StageRunState {
  private final StageConfig stageConfig;
  private ExecutionState stageStatus;
  private final Instant startTime;
  private Instant completionTime;
  private final List<JobRunState> jobRunStates;

  /**
   * Constructs a new StageExecution based on its definition.
   *
   * @param stageConfig the stage definition
   * @param jobRunStates   the list of job executions
   */
  @JsonCreator
  public StageRunState(
          @JsonProperty("stageDefinition") StageConfig stageConfig,
          @JsonProperty("jobExecutions") List<JobRunState> jobRunStates) {
    this.stageConfig = stageConfig;
    this.jobRunStates = jobRunStates;
    this.stageStatus = ExecutionState.PENDING;
    this.startTime = Instant.now();
  }

  /**
   * Computes the stage execution status dynamically.
   * This method should be called whenever the status of any job changes.
   */
  public void updateStatus() {
    boolean hasFailed = jobRunStates.stream()
            .anyMatch(job -> job.getStatus().equals("FAILED") && job.isAllowFailure());

    boolean hasCanceled = jobRunStates.stream()
            .anyMatch(job -> job.getStatus().equals("CANCELED"));

    if (hasFailed) {
      this.stageStatus = ExecutionState.FAILED;
    } else if (hasCanceled) {
      this.stageStatus = ExecutionState.CANCELED;
    } else if (jobRunStates.stream().allMatch(job -> job.getStatus().equals("SUCCESS"))) {
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
    return stageConfig.getName();
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
  public List<JobRunState> getJobExecutions() {
    return jobRunStates;
  }
}
