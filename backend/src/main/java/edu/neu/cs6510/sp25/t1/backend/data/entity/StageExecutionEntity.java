package edu.neu.cs6510.sp25.t1.backend.data.entity;

import java.time.Instant;
import java.util.List;

import jakarta.persistence.*;

import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;

/**
 * Represents the execution state of a stage in a pipeline run.
 */
@Entity
@Table(name = "stage_executions")
public class StageExecutionEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String runId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "pipeline_execution_id", nullable = false)
  private PipelineExecutionEntity pipelineExecution;

  @Column(nullable = false)
  private String stageName; // Store name directly instead of linking to StageEntity

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ExecutionStatus status;

  @Column
  private Instant startTime;

  @Column
  private Instant completionTime;

  @OneToMany(mappedBy = "stageExecution", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<JobExecutionEntity> jobExecutions;

  public StageExecutionEntity() {}

  public StageExecutionEntity(PipelineExecutionEntity pipelineExecution, String stageName) {
    this.pipelineExecution = pipelineExecution;
    this.stageName = stageName;
    this.status = ExecutionStatus.PENDING;
  }

  public Long getId() {
    return id;
  }

  public String getRunId() { return runId; }  // Required for CLI queries


  public String getStageName() {
    return stageName;
  }

  public PipelineExecutionEntity getPipelineExecution() {
    return pipelineExecution;
  }

  public ExecutionStatus getStatus() {
    return status;
  }

  public void setStatus(ExecutionStatus status) {
    this.status = status;
  }

  public Instant getStartTime() {
    return startTime;
  }

  public void setStartTime(Instant startTime) {
    this.startTime = startTime;
  }

  public Instant getCompletionTime() {
    return completionTime;
  }

  public void setCompletionTime(Instant completionTime) {
    this.completionTime = completionTime;
  }

  public List<JobExecutionEntity> getJobExecutions() {
    return jobExecutions;
  }

}
