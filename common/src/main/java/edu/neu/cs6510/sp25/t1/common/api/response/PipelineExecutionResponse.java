package edu.neu.cs6510.sp25.t1.common.api.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a response to a pipeline execution request.
 */
public class PipelineExecutionResponse {
  private final String executionId;
  private final String status;

  /**
   * Constructs a new PipelineExecutionResponse based on its definition.
   * @param executionId The unique identifier for this pipeline execution
   * @param status The status of the pipeline execution
   */
  @JsonCreator
  public PipelineExecutionResponse(
          @JsonProperty String executionId,
          @JsonProperty String status) {
    this.executionId = executionId;
    this.status = status;
  }

  /**
   * Getter for executionId.
   *
   * @return the executionId
   */
  public String getExecutionId() {
    return executionId;
  }

  /**
   * Getter for status.
   *
   * @return the status
   */
  public String getStatus() {
    return status;
  }
}
