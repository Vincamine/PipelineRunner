package edu.neu.cs6510.sp25.t1.common.api.response;

import java.util.List;

/**
 * Represents a response to a stage report request.
 */
public class StageReportResponse {
  private final String stageName;
  private final List<ExecutionRecord> executions;

  /**
   * Constructs a new StageReportResponse based on its definition.
   *
   * @param stageName  The stage name
   * @param executions The list of job executions
   */
  public StageReportResponse(String stageName, List<ExecutionRecord> executions) {
    this.stageName = stageName;
    this.executions = executions;
  }

  /**
   * Getter for stageName.
   *
   * @return the stageName
   */
  public String getStageName() {
    return stageName;
  }

  /**
   * Getter for executions.
   *
   * @return the executions.
   */
  public List<ExecutionRecord> getExecutions() {
    return executions;
  }

  /**
   * Represents a record of a past execution of the stage.
   */
  public static class ExecutionRecord {
    private final String executionId;
    private final String status;

    /**
     * Constructs an ExecutionRecord.
     *
     * @param executionId The unique identifier of the stage execution.
     * @param status      The status of the stage execution (e.g., SUCCESS, FAILED, CANCELED).
     */
    public ExecutionRecord(String executionId, String status) {
      this.executionId = executionId;
      this.status = status;
    }

    /**
     * Gets the execution ID.
     *
     * @return execution ID.
     */
    public String getExecutionId() {
      return executionId;
    }

    /**
     * Gets the execution status.
     *
     * @return execution status.
     */
    public String getStatus() {
      return status;
    }
  }
}

