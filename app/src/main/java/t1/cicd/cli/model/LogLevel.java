package t1.cicd.cli.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Defines log levels for logging in the CI/CD system.
 */
public enum LogLevel {
  DEBUG,  // Detailed debugging information
  INFO,   // General operational messages
  WARN,   // Warnings that may require attention
  ERROR,  // Errors affecting functionality
  FATAL;   // Critical errors causing termination

  @JsonCreator
  public static LogLevel fromString(String value) {
    return value == null ? null : LogLevel.valueOf(value.toUpperCase());
  }

  @JsonValue
  public String toValue() {
    return this.name();
  }
}
