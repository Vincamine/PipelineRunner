package edu.neu.cs6510.sp25.t1.backend.database.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents an execution log entry stored in the database.
 */
@Entity
@Table(name = "execution_logs")
public class ExecutionLogEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  @Column(name = "pipeline_execution_id")
  private UUID pipelineExecutionId;

  @Column(name = "stage_execution_id")
  private UUID stageExecutionId;

  @Column(name = "job_execution_id")
  private UUID jobExecutionId;

  @Column(name = "log_text", nullable = false, columnDefinition = "TEXT")
  private String logText;

  @Column(nullable = false, updatable = false)
  private LocalDateTime timestamp;

  /**
   * Default constructor required by JPA.
   */
  protected ExecutionLogEntity() {
    this.timestamp = LocalDateTime.now();
  }

  /**
   * Constructor for creating an ExecutionLogEntity.
   *
   * @param logText Log message text
   * @param pipelineExecutionId Pipeline execution ID (optional)
   * @param stageExecutionId Stage execution ID (optional)
   * @param jobExecutionId Job execution ID (optional)
   */
  public ExecutionLogEntity(String logText, UUID pipelineExecutionId, UUID stageExecutionId, UUID jobExecutionId) {
    this.logText = logText;
    this.pipelineExecutionId = pipelineExecutionId;
    this.stageExecutionId = stageExecutionId;
    this.jobExecutionId = jobExecutionId;
    this.timestamp = LocalDateTime.now();
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

  public LocalDateTime getTimestamp() {
    return timestamp;
  }

  // Setters
  public void setLogText(String logText) {
    this.logText = logText;
  }

  public void setPipelineExecutionId(UUID pipelineExecutionId) {
    this.pipelineExecutionId = pipelineExecutionId;
  }

  public void setStageExecutionId(UUID stageExecutionId) {
    this.stageExecutionId = stageExecutionId;
  }

  public void setJobExecutionId(UUID jobExecutionId) {
    this.jobExecutionId = jobExecutionId;
  }
}
