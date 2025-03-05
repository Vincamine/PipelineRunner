package edu.neu.cs6510.sp25.t1.common.api.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Represents a response to a job report request.
 */
public class JobReportResponse {
  private final String jobName;
  private final List<ExecutionRecord> executions;

  /**
   * Constructs a new JobReportResponse based on its definition.
   *
   * @param jobName    The job name.
   * @param executions The list of past job execution records.
   */
  @JsonCreator
  public JobReportResponse(
          @JsonProperty("jobName") String jobName,
          @JsonProperty("executions") List<ExecutionRecord> executions) {
    this.jobName = jobName;
    this.executions = executions;
  }

  /**
   * Getter for jobName.
   *
   * @return the job name.
   */
  public String getJobName() {
    return jobName;
  }

  /**
   * Getter for executions.
   *
   * @return the list of job execution records.
   */
  public List<ExecutionRecord> getExecutions() {
    return executions;
  }

  /**
   * Represents a record of a past execution of the job.
   */
  public static class ExecutionRecord {
    private final String executionId;
    private final String status;
    private final String logs;

    /**
     * Constructs an ExecutionRecord.
     *
     * @param executionId The unique identifier of the job execution.
     * @param status      The status of the job execution (e.g., SUCCESS, FAILED).
     * @param logs        The logs from the job execution.
     */
    @JsonCreator
    public ExecutionRecord(
            @JsonProperty("executionId") String executionId,
            @JsonProperty("status") String status,
            @JsonProperty("logs") String logs) {
      this.executionId = executionId;
      this.status = status;
      this.logs = logs;
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

    /**
     * Gets the execution logs.
     *
     * @return execution logs.
     */
    public String getLogs() {
      return logs;
    }
  }
}
