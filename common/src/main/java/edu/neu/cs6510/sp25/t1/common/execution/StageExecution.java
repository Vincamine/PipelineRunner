package edu.neu.cs6510.sp25.t1.common.execution;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.List;

import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;
import edu.neu.cs6510.sp25.t1.common.model.Stage;

/**
 * Represents a stage execution instance in a CI/CD pipeline.
 */
public class StageExecution {
  private final Stage stage;
  private ExecutionStatus status;
  private final Instant startTime;
  private Instant completionTime;
  private final List<JobExecution> jobs;

  /**
   * Constructs a new StageExecution based on its definition.
   *
   * @param stage the stage definition
   * @param jobs  the list of job executions
   */
  @JsonCreator
  public StageExecution(
          @JsonProperty("stageDefinition") Stage stage,
          @JsonProperty("jobExecutions") List<JobExecution> jobs) {
    this.stage = stage;
    this.jobs = jobs;
    this.status = ExecutionStatus.PENDING;
    this.startTime = Instant.now();
  }

  /**
   * Updates the execution status of the stage based on the statuses of its jobs.
   * <p>
   * A stage can have one of the following statuses:
   * - SUCCESS: All jobs in the stage have status SUCCESS.
   * - FAILED: At least one job in the stage has status FAILED.
   * - CANCELED: At least one job in the stage has status CANCELED.
   * <p>
   * The status is determined with the following precedence:
   * - If any job has status FAILED, the stage status is set to FAILED.
   * - If no jobs have failed but at least one job is CANCELED, the stage status is set to CANCELED.
   * - If all jobs are SUCCESS, the stage status is set to SUCCESS.
   * <p>
   * The method also updates the completion time to reflect when the status was last updated.
   */
  public void updateStatus() {
    // Determine the stage status based on the statuses of its jobs, with the following precedence:
    // - FAILED: At least one job has status FAILED.
    boolean hasFailed = jobs.stream().anyMatch(j -> j.getStatus() == ExecutionStatus.FAILED);
    // - CANCELED: At least one job has status CANCELED.
    boolean hasCanceled = jobs.stream().anyMatch(j -> j.getStatus() == ExecutionStatus.CANCELED);
    // - CANCELED: At least one job has status CANCELED.
    boolean allSucceeded = jobs.stream().allMatch(j -> j.getStatus() == ExecutionStatus.SUCCESS);

    // Set the stage status based on the precedence rules.
    if (hasFailed) {
      this.status = ExecutionStatus.FAILED;
    } else if (allSucceeded) {
      this.status = ExecutionStatus.SUCCESS;
    } else {
      this.status = ExecutionStatus.CANCELED;
    }

    // Update the completion time to reflect when the status was last updated.
    this.completionTime = Instant.now();
  }


  /**
   * Getter for stage status.
   *
   * @return stage status
   */
  public ExecutionStatus getStatus() {
    return status;
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
  public List<JobExecution> getJobs() {
    return jobs;
  }

  /**
   * Getter for stage name.
   *
   * @return the name of the stage
   */
  public String getName() {
    return stage.getName();
  }

  /**
   * Setter for stage status for testing purposes.
   *
   * @param status the new status of the stage
   */
  public void setStatus(ExecutionStatus status) {
    this.status = status;
  }
}
