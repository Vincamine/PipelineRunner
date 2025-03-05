package edu.neu.cs6510.sp25.t1.common.model;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;

/**
 * Represents a job execution instance within a pipeline stage.
 * This class tracks the execution status of a job, including its start time,
 * completion time, dependencies, and whether it should be executed in Docker
 * or locally in a non-containerized environment.
 */
public class JobExecution {
  private final UUID id;
  private final UUID stageExecutionId;
  private final UUID jobId;
  private final String commitHash;
  private final boolean isLocal;
  private ExecutionStatus status;
  private final Instant startTime;
  private Instant completionTime;
  private Instant lastUpdated;
  private final boolean allowFailure;
  private final List<String> script;
  private final List<String> dependencies;
  private String logs;

  /**
   * Constructs a new JobExecution instance.
   *
   * @param stageExecutionId The ID of the stage execution.
   * @param jobId            The ID of the job being executed.
   * @param commitHash       The commit hash of the repository state for execution.
   * @param isLocal          Whether this job execution is local or remote.
   * @param image            The Docker image used for execution.
   * @param script           The script to be executed.
   * @param dependencies     The dependencies required before execution.
   * @param allowFailure     Whether the job is allowed to fail.
   */
  public JobExecution(UUID stageExecutionId, UUID jobId, String commitHash, boolean isLocal,
                      String image, List<String> script, List<String> dependencies, boolean allowFailure) {
    this.id = UUID.randomUUID();  // Generate new unique ID
    this.stageExecutionId = stageExecutionId;
    this.jobId = jobId;
    this.commitHash = commitHash;
    this.isLocal = isLocal;
    this.status = ExecutionStatus.PENDING;
    this.startTime = Instant.now();
    this.completionTime = null;  // Initially null
    this.lastUpdated = Instant.now();
    this.allowFailure = allowFailure;
    this.script = script;
    this.dependencies = dependencies;
    this.logs = "";
  }

  /**
   * Updates the execution status of this entity.
   *
   * @param newState the new execution status to be set
   */
  public void updateState(ExecutionStatus newState) {
    this.status = newState;
    this.lastUpdated = Instant.now();
    if (newState == ExecutionStatus.SUCCESS || newState == ExecutionStatus.FAILED || newState == ExecutionStatus.CANCELED) {
      this.completionTime = Instant.now();
    }
  }

  /**
   * Appends logs to the job execution.
   *
   * @param logText The logs to append.
   */
  public void appendLogs(String logText) {
    this.logs += logText + "\n";
  }

  // ========================
  // Getters for the fields
  // ========================

  /**
   * Gets the unique identifier of the job execution.
   *
   * @return The job execution ID.
   */
  public UUID getId() {
    return id;
  }

  /**
   * Gets the ID of the stage execution.
   *
   * @return The stage execution ID.
   */
  public UUID getStageExecutionId() {
    return stageExecutionId;
  }

  /**
   * Gets the ID of the job being executed.
   *
   * @return The job ID.
   */
  public UUID getJobId() {
    return jobId;
  }

  /**
   * Gets the commit hash of the repository state for execution.
   *
   * @return The commit hash.
   */
  public String getCommitHash() {
    return commitHash;
  }

  /**
   * Gets whether this job execution is local or remote.
   *
   * @return True if local, false if remote.
   */
  public boolean isLocal() {
    return isLocal;
  }

  /**
   * Gets the status of the job.
   *
   * @return The job status.
   */
  public ExecutionStatus getStatus() {
    return status;
  }

  /**
   * Gets the time when the job execution was last updated.
   *
   * @return The last updated time.
   */
  public Instant getStartTime() {
    return startTime;
  }

  /**
   * Gets the time when the job execution was last updated.
   *
   * @return The last updated time.
   */
  public Instant getCompletionTime() {
    return completionTime;
  }

  /**
   * Gets the time when the job execution was last updated.
   *
   * @return The last updated time.
   */
  public boolean isAllowFailure() {
    return allowFailure;
  }

  /**
   * Gets the Docker image used for execution.
   *
   * @return The Docker image.
   */
  public List<String> getScript() {
    return script;
  }

  /**
   * Gets the dependencies required before execution.
   *
   * @return The dependencies.
   */
  public List<String> getDependencies() {
    return dependencies;
  }

  /**
   * Gets the logs of the job execution.
   *
   * @return The logs.
   */
  public String getLogs() {
    return logs;
  }
}
