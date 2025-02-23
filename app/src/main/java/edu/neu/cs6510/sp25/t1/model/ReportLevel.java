package edu.neu.cs6510.sp25.t1.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Defines report levels for the CI/CD system.
 * Used to categorize report entries based on severity.
 */
public enum ReportLevel {
    DEBUG, // Detailed debugging information.
    SUCCESS, // General operational messages.
    WARN, // Warnings that may require attention.
    FAILED, // Errors affecting functionality.
    CANCEL; // Critical errors causing termination.

    /**
     * Converts a string to a ReportLevel enum.
     *
     * @param value The string representation of the report level.
     * @return The corresponding ReportLevel, or null if invalid.
     */
    @JsonCreator
    public static ReportLevel fromString(String value) {
        if (value == null) {
            return SUCCESS;
        }
        try {
            return ReportLevel.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return SUCCESS;
        }
    }

    /**
     * Returns the string representation of the ReportLevel.
     *
     * @return The report level as a string.
     */
    @JsonValue
    public String toValue() {
        return this.name();
    }
}
