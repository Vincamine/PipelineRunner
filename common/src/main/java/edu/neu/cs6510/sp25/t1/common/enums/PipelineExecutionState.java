package edu.neu.cs6510.sp25.t1.common.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents the execution state of a pipeline.
 */
public enum PipelineExecutionState {
  PENDING,    // Pipeline is scheduled but not started
  RUNNING,    // Pipeline is actively executing
  SUCCESS,    // Pipeline completed all jobs/stages successfully (Renamed from SUCCESSFUL)
  FAILED;     // Pipeline execution failed due to at least one job failure

  private static final Map<String, PipelineExecutionState> STRING_TO_ENUM = new HashMap<>();

  // Populate the map with enum values
  static {
    for (PipelineExecutionState state : values()) {
      STRING_TO_ENUM.put(state.name().toLowerCase(), state);
    }
  }

  /**
   * Determines if the transition from the current state to the new state is valid.
   *
   * @param newState The target state to transition to.
   * @return true if the transition is valid, false otherwise.
   */
  public boolean canTransitionTo(PipelineExecutionState newState) {
    return switch (this) {
      case PENDING -> newState == RUNNING;
      case RUNNING -> newState == SUCCESS || newState == FAILED;
      case SUCCESS, FAILED -> false; // Terminal states (cannot transition further)
    };
  }

  /**
   * Converts a string to a `PipelineExecutionState` enum value.
   *
   * @param value The string to convert.
   * @return The corresponding `PipelineExecutionState` enum value.
   */
  public static PipelineExecutionState fromString(String value) {
    if (value == null || !STRING_TO_ENUM.containsKey(value.toLowerCase())) {
      throw new IllegalArgumentException("Invalid PipelineExecutionState: " + value);
    }
    return STRING_TO_ENUM.get(value.toLowerCase());
  }
}
