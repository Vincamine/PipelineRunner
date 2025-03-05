package edu.neu.cs6510.sp25.t1.common.api.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Represents the response structure for pipeline execution reports.
 */
public class PipelineReportResponse {
  private String executionId;
  private String status;
  private String commitHash;
  private List<StageReportSummary> stages;

  /**
   * Constructor for PipelineReportResponse.
   *
   * @param executionId Unique execution identifier.
   * @param status      Execution status (PENDING, RUNNING, SUCCESS, FAILED, CANCELED).
   * @param commitHash  Commit hash associated with this execution.
   */
  @JsonCreator
  public PipelineReportResponse(
          @JsonProperty String executionId,
          @JsonProperty String status,
          @JsonProperty String commitHash) {
    this.executionId = executionId;
    this.status = status;
    this.commitHash = commitHash;
  }

  /**
   * Constructor with stage summary.
   *
   * @param executionId Unique execution identifier.
   * @param status      Execution status.
   * @param commitHash  Commit hash.
   * @param stages      Summary of stage executions.
   */
  public PipelineReportResponse(String executionId, String status, String commitHash, List<StageReportSummary> stages) {
    this.executionId = executionId;
    this.status = status;
    this.commitHash = commitHash;
    this.stages = stages;
  }

  /**
   * Gets the execution ID.
   *
   * @return Execution ID.
   */
  public String getExecutionId() {
    return executionId;
  }

  /**
   * Gets the execution status.
   *
   * @return Execution status.
   */
  public String getStatus() {
    return status;
  }

  /**
   * Gets the commit hash.
   *
   * @return Commit hash.
   */
  public String getCommitHash() {
    return commitHash;
  }

  /**
   * Gets the list of stage summaries.
   *
   * @return List of stage execution summaries.
   */
  public List<StageReportSummary> getStages() {
    return stages;
  }

  /**
   * Represents a summary of a stage execution within a pipeline.
   */
  public static class StageReportSummary {
    private String stageName;
    private String status;

    public StageReportSummary(String stageName, String status) {
      this.stageName = stageName;
      this.status = status;
    }

    public String getStageName() {
      return stageName;
    }

    public String getStatus() {
      return status;
    }
  }
}
