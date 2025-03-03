package edu.neu.cs6510.sp25.t1.backend.data.entity;

import java.time.Instant;
import jakarta.persistence.*;
import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;

/**
 * Tracks execution details of a job.
 */
@Entity
@Table(name = "job_executions")
public class JobExecutionEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String runId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "stage_execution_id", nullable = false)
  private StageExecutionEntity stageExecution;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "job_id", nullable = false)
  private JobEntity job;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ExecutionStatus status;

  @Column(nullable = false, updatable = false)
  private Instant startTime;

  @Column
  private Instant completionTime;

  public JobExecutionEntity() {}

  public JobExecutionEntity(StageExecutionEntity stageExecution, JobEntity job, ExecutionStatus status, Instant startTime) {
    this.stageExecution = stageExecution;
    this.job = job;
    this.status = status;
    this.startTime = startTime;
  }

  public Long getId() { return id; }

  public ExecutionStatus getStatus() { return status; }

  public Instant getStartTime() { return startTime; }

  public Instant getCompletionTime() { return completionTime; }

  public void setStatus(ExecutionStatus status) { this.status = status; }

  public void setCompletionTime(Instant completionTime) { this.completionTime = completionTime; }

  public String getRunId() { return runId; }  // Ensure CLI report can query by runId

  public StageExecutionEntity getStageExecution() {
    return stageExecution;
  }
  public JobEntity getJob() {
    return job;
  }

  public void setStartTime(Instant now) {
  }
}
