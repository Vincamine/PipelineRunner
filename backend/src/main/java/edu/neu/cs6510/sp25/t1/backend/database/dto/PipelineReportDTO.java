package edu.neu.cs6510.sp25.t1.backend.database.dto;

import java.util.List;
import java.util.Map;

/**
 * Data Transfer Object (DTO) for a complete Pipeline Execution Report.
 * Used for API responses to prevent direct exposure of JPA entities.
 */
public class PipelineReportDTO {
  private final PipelineExecutionDTO pipelineExecution;
  private final List<StageExecutionDTO> stageExecutions;
  private final List<JobExecutionDTO> jobExecutions;
  private final Map<String, Object> metrics;

  /**
   * Constructs a complete pipeline report.
   *
   * @param pipelineExecution The pipeline execution data.
   * @param stageExecutions The list of stage executions.
   * @param jobExecutions The list of job executions.
   * @param metrics Various performance and status metrics.
   */
  public PipelineReportDTO(
          PipelineExecutionDTO pipelineExecution,
          List<StageExecutionDTO> stageExecutions,
          List<JobExecutionDTO> jobExecutions,
          Map<String, Object> metrics) {
    this.pipelineExecution = pipelineExecution;
    this.stageExecutions = stageExecutions;
    this.jobExecutions = jobExecutions;
    this.metrics = metrics;
  }

  // Getters
  public PipelineExecutionDTO getPipelineExecution() {
    return pipelineExecution;
  }

  public List<StageExecutionDTO> getStageExecutions() {
    return stageExecutions;
  }

  public List<JobExecutionDTO> getJobExecutions() {
    return jobExecutions;
  }

  public Map<String, Object> getMetrics() {
    return metrics;
  }
}