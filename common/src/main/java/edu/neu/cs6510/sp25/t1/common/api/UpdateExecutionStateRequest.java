package edu.neu.cs6510.sp25.t1.common.api;

import edu.neu.cs6510.sp25.t1.common.execution.ExecutionState;
import java.io.Serializable;

/**
 * Represents a request to update the execution state of a pipeline, stage, or job.
 * <p>
 * This request is used to log execution progress to the backend for tracking
 * even when execution occurs locally.
 */
public class UpdateExecutionStateRequest implements Serializable {
  private final String name; // Pipeline, stage, or job name
  private final ExecutionState state; // The execution state

  /**
   * Constructor for UpdateExecutionStateRequest.
   *
   * @param name  The name of the pipeline, stage, or job
   * @param state The new execution state
   * @throws IllegalArgumentException if name is null or empty
   */
  public UpdateExecutionStateRequest(String name, ExecutionState state) {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Execution target name cannot be null or empty.");
    }
    this.name = name;
    this.state = state;
  }

  /**
   * Get the execution target name.
   *
   * @return The name of the pipeline, stage, or job.
   */
  public String getName() {
    return name;
  }

  /**
   * Get the execution state.
   *
   * @return The execution state.
   */
  public ExecutionState getState() {
    return state;
  }

  /**
   * String representation of the request.
   *
   * @return String representation.
   */
  @Override
  public String toString() {
    return "UpdateExecutionStateRequest{" +
            "name='" + name + '\'' +
            ", state=" + state +
            '}';
  }
}
