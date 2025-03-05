package edu.neu.cs6510.sp25.t1.common.api.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a request to update the status of a job execution.
 */
public class JobStatusUpdate {
  private final String jobExecutionId;
  private final String status;
  private final String logs;

  /**
   * Constructor for JobStatusUpdate.
   *
   * @param jobExecutionId The unique identifier of the job execution.
   * @param status         The new status of the job execution.
   * @param logs           The logs to update.
   */
  @JsonCreator
  public JobStatusUpdate(
          @JsonProperty("jobExecutionId") String jobExecutionId,
          @JsonProperty("status") String status,
          @JsonProperty("logs") String logs) {
    this.jobExecutionId = jobExecutionId;
    this.status = status;
    this.logs = logs;
  }

  /**
   * Getter for jobExecutionId.
   *
   * @return the jobExecutionId
   */
  public String getJobExecutionId() {
    return jobExecutionId;
  }

  /**
   * Getter for status.
   *
   * @return the status
   */
  public String getStatus() {
    return status;
  }

  /**
   * Getter for logs.
   *
   * @return the logs
   */
  public String getLogs() {
    return logs;
  }
}
