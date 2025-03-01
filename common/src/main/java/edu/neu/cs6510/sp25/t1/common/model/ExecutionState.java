package edu.neu.cs6510.sp25.t1.common.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents the execution state of a pipeline or job.
 */
public enum ExecutionState {
  PENDING("Execution is waiting to start"),
  RUNNING("Execution is currently in progress"),
  SUCCESS("Execution completed successfully"),
  FAILED("Execution failed"),
  CANCELED("Execution was manually canceled"),
  QUEUED("Execution is waiting for resources"),
  RETRYING("Execution is retrying after failure"),
  PAUSED("Execution is temporarily paused"),
  UNKNOWN("Execution status cannot be determined");

  private final String description;
  private static final Map<String, ExecutionState> STRING_TO_ENUM = new HashMap<>();

  // Populate the map with enum values
  static {
    for (ExecutionState state : values()) {
      STRING_TO_ENUM.put(state.name().toLowerCase(), state);
    }
  }

  /**
   * Constructor for ExecutionState enum.
   *
   * @param description A string describing the execution state.
   */
  ExecutionState(String description) {
    this.description = description;
  }

  /**
   * Retrieves the description of the execution state.
   *
   * @return A string describing the execution state.
   */
  public String getDescription() {
    return description;
  }

  /**
   * Converts a string to an `ExecutionState` enum value.
   * Uses a lookup map for better performance.
   *
   * @param value The string representation of the execution state.
   * @return The corresponding `ExecutionState` enum, or `UNKNOWN` if not found.
   */
  public static ExecutionState fromString(String value) {
    if (value == null) {
      return UNKNOWN;
    }
    return STRING_TO_ENUM.getOrDefault(value.toLowerCase(), UNKNOWN);
  }
}
