package edu.neu.cs6510.sp25.t1.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Defines log levels for the CI/CD system.
 * Used to categorize log entries based on severity.
 */
public enum LogLevel {
    DEBUG, // Detailed debugging information.
    INFO, // General operational messages.
    WARN, // Warnings that may require attention.
    ERROR, // Errors affecting functionality.
    FATAL; // Critical errors causing termination.

    /**
     * Converts a string to a LogLevel enum.
     *
     * @param value The string representation of the log level.
     * @return The corresponding LogLevel, or null if invalid.
     */
    @JsonCreator
    public static LogLevel fromString(String value) {
        return value == null ? null : LogLevel.valueOf(value.toUpperCase());
    }

    /**
     * Returns the string representation of the LogLevel.
     *
     * @return The log level as a string.
     */
    @JsonValue
    public String toValue() {
        return this.name();
    }
}
