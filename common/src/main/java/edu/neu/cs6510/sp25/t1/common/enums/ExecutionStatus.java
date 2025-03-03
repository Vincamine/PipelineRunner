package edu.neu.cs6510.sp25.t1.common.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents the execution state of a pipeline or job.
 */
public enum ExecutionStatus {
  SUCCESS("Execution completed successfully"),
  FAILED("Execution failed"),
  CANCELED("Execution was manually canceled"),
  PENDING("Execution is scheduled but not started"),
  QUEUED("Execution is waiting for resources"),
  RUNNING("Execution is actively running");

  private final String description;
  private static final Map<String, ExecutionStatus> STRING_TO_ENUM = new HashMap<>();

  // Populate the map with enum values
  static {
    for (ExecutionStatus state : values()) {
      STRING_TO_ENUM.put(state.name().toLowerCase(), state);
    }
  }

  /**
   * Constructor for ExecutionState enum.
   *
   * @param description A string describing the execution state.
   */
  ExecutionStatus(String description) {
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
   * Converts a string to an `ExecutionStatus` enum value.
   * Uses a lookup map for better performance.
   *
   * @param value The string to convert.
   *              Must be one of the enum values in lowercase.
   * @return The corresponding `ExecutionStatus` enum value.
   */
  public static ExecutionStatus fromString(String value) {
    if (value == null || !STRING_TO_ENUM.containsKey(value.toLowerCase())) {
      throw new IllegalArgumentException("Invalid ExecutionStatus: " + value);
    }
    return STRING_TO_ENUM.get(value.toLowerCase());
  }
}
