package edu.neu.cs6510.sp25.t1.backend.api.request;

import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;

/**
 * Represents a request to update the execution state of a pipeline, stage, or job.
 * This request is used to log execution progress to the backend for tracking.
 */
public class UpdateExecutionStateRequest {
  private String name; // Pipeline, stage, or job name
  private ExecutionStatus state; // The execution state

  public UpdateExecutionStateRequest() {
  }

  public UpdateExecutionStateRequest(String name, ExecutionStatus state) {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Execution target name cannot be null or empty.");
    }
    this.name = name;
    this.state = state;
  }

  public String getName() {
    return name;
  }

  public ExecutionStatus getState() {
    return state;
  }

  @Override
  public String toString() {
    return "UpdateExecutionStateRequest{" +
            "name='" + name + '\'' +
            ", state=" + state +
            '}';
  }
}