package edu.neu.cs6510.sp25.t1.backend.data.dto;

import java.time.Instant;

import edu.neu.cs6510.sp25.t1.backend.data.entity.JobExecutionEntity;
import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;

/**
 * Data Transfer Object (DTO) for Job Execution.
 * Used for API responses to prevent direct exposure of JPA entities.
 */
public class JobExecutionDTO {
  private String runId;
  private String pipelineName;
  private String stageName;
  private String jobName;
  private ExecutionStatus status;
  private boolean allowFailure;
  private Instant startTime;
  private Instant completionTime;

  /**
   * Constructs a JobExecutionDTO.
   *
   * @param runId         The unique run ID.
   * @param pipelineName  The name of the pipeline.
   * @param stageName     The name of the stage.
   * @param jobName       The name of the job.
   * @param status        The execution status.
   * @param allowFailure  Whether this job allows failures.
   * @param startTime     The start time.
   * @param completionTime The completion time.
   */
  public JobExecutionDTO(String runId, String pipelineName, String stageName, String jobName, ExecutionStatus status,
                         boolean allowFailure, Instant startTime, Instant completionTime) {
    this.runId = runId;
    this.pipelineName = pipelineName;
    this.stageName = stageName;
    this.jobName = jobName;
    this.status = status;
    this.allowFailure = allowFailure;
    this.startTime = startTime;
    this.completionTime = completionTime;
  }

  /**
   * Converts a JobExecutionEntity to a DTO.
   *
   * @param jobExecutionEntity The entity to convert.
   * @return A JobExecutionDTO representation.
   */
  public static JobExecutionDTO fromEntity(JobExecutionEntity jobExecutionEntity) {
    return new JobExecutionDTO(
            jobExecutionEntity.getRunId(),
            jobExecutionEntity.getStageExecution().getPipelineExecution().getPipeline().getName(),
            jobExecutionEntity.getStageExecution().getStageName(),
            jobExecutionEntity.getJob().getName(),
            jobExecutionEntity.getStatus(),
            jobExecutionEntity.getJob().isAllowFailure(),
            jobExecutionEntity.getStartTime(),
            jobExecutionEntity.getCompletionTime()
    );
  }

  // Getters
  public String getRunId() { return runId; }
  public String getPipelineName() { return pipelineName; }
  public String getStageName() { return stageName; }
  public String getJobName() { return jobName; }
  public ExecutionStatus getStatus() { return status; }
  public boolean isAllowFailure() { return allowFailure; }
  public Instant getStartTime() { return startTime; }
  public Instant getCompletionTime() { return completionTime; }

  // Setters
  public void setRunId(String runId) { this.runId = runId; }
  public void setPipelineName(String pipelineName) { this.pipelineName = pipelineName; }
  public void setStageName(String stageName) { this.stageName = stageName; }
  public void setJobName(String jobName) { this.jobName = jobName; }
  public void setStatus(ExecutionStatus status) { this.status = status; }
  public void setAllowFailure(boolean allowFailure) { this.allowFailure = allowFailure; }
  public void setStartTime(Instant startTime) { this.startTime = startTime; }
  public void setCompletionTime(Instant completionTime) { this.completionTime = completionTime; }
}
