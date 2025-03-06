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
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents an execution instance of a CI/CD pipeline.
 * Tracks the execution status, metadata, and timing details.
 */
@Entity
@Table(name = "pipeline_executions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PipelineExecutionEntity {

  /**
   * Unique identifier for the pipeline execution.
   */
  @Id
  @GeneratedValue
  private UUID id;

  /**
   * Foreign key reference to the pipeline being executed.
   */
  @Column(name = "pipeline_id", nullable = false)
  private UUID pipelineId;

  /**
   * Unique run number for tracking executions of the pipeline.
   */
  @Column(name = "run_number", nullable = false)
  private int runNumber;

  /**
   * Git commit hash associated with this pipeline execution.
   */
  @Column(name = "commit_hash", nullable = false, length = 40)
  private String commitHash;

  /**
   * Indicates whether this execution is local.
   */
  @Column(name = "is_local", nullable = false)
  private boolean isLocal;

  /**
   * The execution status of the pipeline.
   */
  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private ExecutionStatus status;

  /**
   * Timestamp indicating when the pipeline execution started.
   */
  @Column(name = "start_time")
  private Instant startTime;

  /**
   * Timestamp indicating when the pipeline execution was completed.
   */
  @Column(name = "completion_time")
  private Instant completionTime;

  /**
   * Lifecycle hook to set default timestamps before persisting.
   */
  @PrePersist
  protected void onCreate() {
    this.startTime = Instant.now();
    this.status = ExecutionStatus.PENDING;
  }

  /**
   * Updates the execution status and completion timestamp.
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
