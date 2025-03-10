package edu.neu.cs6510.sp25.t1.backend.database.entity;

import java.time.Instant;
import java.util.UUID;

import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a job execution instance within a pipeline stage.
 * Tracks execution status, timestamps, and job-related metadata.
 */
@Entity
@Table(name = "job_executions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobExecutionEntity {

  /**
   * Unique identifier for the job execution.
   */
  @Id
  @GeneratedValue
  private UUID id;

  /**
   * Foreign key reference to the stage execution this job belongs to.
   */
  @Column(name = "stage_execution_id", nullable = false)
  private UUID stageExecutionId;

  /**
   * Foreign key reference to the job being executed.
   */
  @Column(name = "job_id", nullable = false)
  private UUID jobId;

  /**
   * The name of the job being executed.
   */
  @Column(name = "job_name", nullable = false)
  private String jobName;

  /**
   * The commit hash of the repository state for execution.
   */
  @Column(name = "commit_hash", nullable = false, length = 40)
  private String commitHash;

  /**
   * Indicates if the job is executed locally or remotely.
   */
  @Column(name = "is_local", nullable = false)
  private boolean isLocal;

  /**
   * Execution status of the job (Pending, Running, Success, Failed, Canceled).
   */
  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private ExecutionStatus status;

  /**
   * Timestamp indicating when the job execution started.
   */
  @Column(name = "start_time")
  private Instant startTime;

  /**
   * Timestamp indicating when the job execution was completed.
   */
  @Column(name = "completion_time")
  private Instant completionTime;

  /**
   * Indicates whether this job execution is allowed to fail without affecting the pipeline.
   */
  @Column(name = "allows_failure", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
  private boolean allowFailure;

  /**
   * Updates the execution status and timestamps.
   *
   * @param newState the new execution status to set
   */
  public void updateState(ExecutionStatus newState) {
    this.status = newState;
    if (newState == ExecutionStatus.SUCCESS || newState == ExecutionStatus.FAILED || newState == ExecutionStatus.CANCELED) {
      this.completionTime = Instant.now();
    }
  }
}
