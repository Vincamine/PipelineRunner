package edu.neu.cs6510.sp25.t1.common.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;

/**
 * Represents a stage execution instance in a CI/CD pipeline.
 */
public class StageExecution {
  private final UUID id;
  private final Stage stage;
  private ExecutionStatus status;
  private final Instant startTime;
  private Instant completionTime;
  private final List<JobExecution> jobs;
  private final String commitHash;
  private final boolean isLocal;

  /**
   * Constructs a new StageExecution based on its definition.
   *
   * @param id         The unique identifier for this stage execution.
   * @param stage      The stage definition.
   * @param jobs       The list of job executions.
   * @param commitHash The commit hash associated with this execution.
   * @param isLocal    Indicates whether the execution is local.
   */
  @JsonCreator
  public StageExecution(
          @JsonProperty("id") UUID id,
          @JsonProperty("stageDefinition") Stage stage,
          @JsonProperty("jobExecutions") List<JobExecution> jobs,
          @JsonProperty("commitHash") String commitHash,
          @JsonProperty("isLocal") boolean isLocal) {
    this.id = id != null ? id : UUID.randomUUID(); // Assigns a new UUID if not provided
    this.stage = stage;
    this.jobs = jobs;
    this.status = ExecutionStatus.PENDING;
    this.startTime = Instant.now();
    this.commitHash = commitHash;
    this.isLocal = isLocal;
  }

  /**
   * Updates the execution status of the stage based on the statuses of its jobs.
   * A stage can have one of the following statuses:
   * - SUCCESS: All jobs in the stage have status SUCCESS.
   * - FAILED: At least one job in the stage has status FAILED.
   * - CANCELED: At least one job in the stage has status CANCELED.
   * <p>
   * The status is determined with the following precedence:
   * - If any job has status FAILED, the stage status is set to FAILED.
   * - If all jobs are SUCCESS, the stage status is set to SUCCESS.
   * - Otherwise, if any job is CANCELED, the stage status is set to CANCELED.
   * <p>
   * The method also updates the completion time to reflect when the status was last updated.
   */
  public void updateStatus() {
    boolean hasFailed = jobs.stream().anyMatch(j -> j.getStatus() == ExecutionStatus.FAILED);
    boolean hasCanceled = jobs.stream().anyMatch(j -> j.getStatus() == ExecutionStatus.CANCELED);
    boolean allSucceeded = jobs.stream().allMatch(j -> j.getStatus() == ExecutionStatus.SUCCESS);

    if (hasFailed) {
      this.status = ExecutionStatus.FAILED;
    } else if (allSucceeded) {
      this.status = ExecutionStatus.SUCCESS;
    } else {
      this.status = ExecutionStatus.CANCELED;
    }

    this.completionTime = Instant.now();
  }

  // ========================
  // Getters for the fields
  // ========================

  /**
   * Gets the unique ID of this stage execution.
   *
   * @return The execution ID.
   */
  public UUID getId() {
    return id;
  }

  /**
   * Gets the stage definition.
   *
   * @return The stage.
   */
  public Stage getStage() {
    return stage;
  }

  /**
   * Gets the status of the stage execution.
   *
   * @return The execution status.
   */
  public ExecutionStatus getStatus() {
    return status;
  }

  /**
   * Gets the start time of the execution.
   *
   * @return The start time.
   */
  public Instant getStartTime() {
    return startTime;
  }

  /**
   * Gets the completion time of the execution.
   *
   * @return The completion time.
   */
  public Instant getCompletionTime() {
    return completionTime;
  }

  /**
   * Gets the list of job executions in this stage.
   *
   * @return The job executions.
   */
  public List<JobExecution> getJobs() {
    return jobs;
  }

  /**
   * Gets the name of the stage.
   *
   * @return The stage name.
   */
  public String getName() {
    return stage.getName();
  }

  /**
   * Gets the commit hash of this execution.
   *
   * @return The commit hash.
   */
  public String getCommitHash() {
    return commitHash;
  }

  /**
   * Checks if this execution is local.
   *
   * @return True if local, false otherwise.
   */
  public boolean isLocal() {
    return isLocal;
  }

  // ========================
  // Setters for the fields
  // ========================

  /**
   * Sets the stage status manually (for testing purposes).
   *
   * @param status The new execution status.
   */
  public void setStatus(ExecutionStatus status) {
    this.status = status;
  }
}
