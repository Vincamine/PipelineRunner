package edu.neu.cs6510.sp25.t1.backend.data.dto;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import edu.neu.cs6510.sp25.t1.backend.data.entity.StageExecutionEntity;
import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;

/**
 * Data Transfer Object (DTO) for Stage Execution.
 * Used for API responses to prevent direct exposure of JPA entities.
 */
public class StageExecutionDTO {
  private String runId;
  private String pipelineName;
  private String stageName;
  private ExecutionStatus status;
  private Instant startTime;
  private Instant completionTime;
  private List<JobExecutionDTO> jobExecutions;

  /**
   * Constructs a StageExecutionDTO.
   *
   * @param runId        The unique run ID from CLI.
   * @param pipelineName The pipeline associated with this stage execution.
   * @param stageName    The name of the stage.
   * @param status       The execution status of the stage.
   * @param startTime    The start time of execution.
   * @param completionTime The completion time (if finished).
   * @param jobExecutions The list of job executions in this stage.
   */
  public StageExecutionDTO(String runId, String pipelineName, String stageName, ExecutionStatus status,
                           Instant startTime, Instant completionTime, List<JobExecutionDTO> jobExecutions) {
    this.runId = runId;
    this.pipelineName = pipelineName;
    this.stageName = stageName;
    this.status = status;
    this.startTime = startTime;
    this.completionTime = completionTime;
    this.jobExecutions = jobExecutions;
  }

  /**
   * Converts a StageExecutionEntity to a DTO.
   *
   * @param stageExecutionEntity The entity to convert.
   * @return A StageExecutionDTO representation.
   */
  public static StageExecutionDTO fromEntity(StageExecutionEntity stageExecutionEntity) {
    return new StageExecutionDTO(
            stageExecutionEntity.getRunId(),
            stageExecutionEntity.getPipelineExecution().getPipeline().getName(),
            stageExecutionEntity.getStageName(),
            stageExecutionEntity.getStatus(),
            stageExecutionEntity.getStartTime(),
            stageExecutionEntity.getCompletionTime(),
            stageExecutionEntity.getJobExecutions() != null
                    ? stageExecutionEntity.getJobExecutions().stream().map(JobExecutionDTO::fromEntity).collect(Collectors.toList())
                    : List.of()
    );
  }

  public String getRunId() { return runId; }
  public String getPipelineName() { return pipelineName; }
  public String getStageName() { return stageName; }
  public ExecutionStatus getStatus() { return status; }
  public Instant getStartTime() { return startTime; }
  public Instant getCompletionTime() { return completionTime; }
  public List<JobExecutionDTO> getJobExecutions() { return jobExecutions; }
}
