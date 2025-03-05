package edu.neu.cs6510.sp25.t1.common.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;

/**
 * Represents an active execution of a CI/CD pipeline. Defines execution state & metadata.
 */
public class PipelineExecution {
  private final UUID id;
  private final UUID pipelineId;
  private final int runNumber; // Tracks how many times this pipeline has been executed
  private final String commitHash;
  private final boolean isLocal;
  private ExecutionStatus status;
  private final Instant startTime;
  private Instant completionTime;
  private final List<StageExecution> stages;

  /**
   * Constructs a new PipelineExecution instance.
   *
   * @param id         The unique execution ID.
   * @param pipelineId The ID of the pipeline being executed.
   * @param runNumber  The sequential run number (starting from 1).
   * @param commitHash The commit hash associated with this execution.
   * @param isLocal    Indicates whether this execution is local.
   * @param stages     The list of stages in the pipeline execution.
   */
  public PipelineExecution(UUID id, UUID pipelineId, int runNumber, String commitHash, boolean isLocal, List<StageExecution> stages) {
    this.id = id != null ? id : UUID.randomUUID();  // Generate UUID if not provided
    this.pipelineId = pipelineId;
    this.runNumber = runNumber;  // Make sure this is set correctly in the database
    this.commitHash = commitHash;
    this.isLocal = isLocal;
    this.status = ExecutionStatus.PENDING;
    this.startTime = Instant.now();
    this.completionTime = null;
    this.stages = stages != null ? stages : new ArrayList<>();
  }

  // ========================
  // Getters for the fields
  // ========================

  /**
   * Gets the unique execution ID.
   *
   * @return The execution ID.
   */
  public UUID getId() {
    return id;
  }

  /**
   * Gets the pipeline ID.
   *
   * @return The pipeline ID.
   */
  public UUID getPipelineId() {
    return pipelineId;
  }

  /**
   * Gets the run number.
   *
   * @return The run number.
   */
  public int getRunNumber() {
    return runNumber;
  }

  /**
   * Gets the commit hash associated with this execution.
   *
   * @return The commit hash.
   */
  public String getCommitHash() {
    return commitHash;
  }

  /**
   * Indicates whether this execution is local.
   *
   * @return True if the execution is local; false otherwise.
   */
  public boolean isLocal() {
    return isLocal;
  }

  /**
   * Gets the status of the pipeline execution.
   *
   * @return The execution status.
   */
  public ExecutionStatus getStatus() {
    return status;
  }

  /**
   * Gets the start
   *
   * @return The start time.
   */
  public Instant getStartTime() {
    return startTime;
  }

  /**
   * Gets the completion time of the pipeline execution
   *
   * @return The completion time.
   */
  public Instant getCompletionTime() {
    return completionTime;
  }

  /**
   * Gets the list of stages in the pipeline execution
   *
   * @return The list of stages.
   */
  public List<StageExecution> getStages() {
    return stages;
  }

  // ========================
  // Setters for the fields
  // ========================

  /**
   * Sets the status of the pipeline execution.
   *
   * @param status The new status of the pipeline execution.
   */
  public void setStatus(ExecutionStatus status) {
    this.status = status;
    if (status == ExecutionStatus.SUCCESS || status == ExecutionStatus.FAILED) {
      this.completionTime = Instant.now();
    }
  }

}
