package edu.neu.cs6510.sp25.t1.common.model;

import java.time.Instant;
import java.util.UUID;

/**
 * Represents execution logs for pipelines, stages, and jobs.
 */
public class ExecutionLog {
  private final UUID id;
  private final UUID pipelineExecutionId;
  private final UUID stageExecutionId;
  private final UUID jobExecutionId;
  private final String logText;
  private final Instant timestamp;

  /**
   * Constructor for ExecutionLog.
   *
   * @param id                 Unique identifier of the log entry.
   * @param pipelineExecutionId Reference to pipeline execution (nullable).
   * @param stageExecutionId   Reference to stage execution (nullable).
   * @param jobExecutionId     Reference to job execution (nullable).
   * @param logText            The log content.
   * @param timestamp          Log creation timestamp.
   */
  public ExecutionLog(UUID id, UUID pipelineExecutionId, UUID stageExecutionId,
                      UUID jobExecutionId, String logText, Instant timestamp) {
    this.id = id;
    this.pipelineExecutionId = pipelineExecutionId;
    this.stageExecutionId = stageExecutionId;
    this.jobExecutionId = jobExecutionId;
    this.logText = logText;
    this.timestamp = timestamp;
  }

  // Getters
  public UUID getId() {
    return id;
  }

  public UUID getPipelineExecutionId() {
    return pipelineExecutionId;
  }

  public UUID getStageExecutionId() {
    return stageExecutionId;
  }

  public UUID getJobExecutionId() {
    return jobExecutionId;
  }

  public String getLogText() {
    return logText;
  }

  public Instant getTimestamp() {
    return timestamp;
  }
}
