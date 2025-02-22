package edu.neu.cs6510.sp25.t1.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a log entry in the CI/CD system.
 * Each log entry records information about a pipeline event,
 * including its severity level, message, and timestamp.
 */
public class ReportEntry {
    private final String pipelineId;
    private final ReportLevel level;
    private final String message;
    private final long timestamp;

    /**
     * Constructs a new LogEntry instance.
     *
     * @param pipelineId The ID of the pipeline associated with this log entry.
     * @param level      The severity level of the log (e.g., SUCCESS, WARN, FAILED).
     * @param message    A description of the event being logged.
     * @param timestamp  The timestamp when the log entry was created
     *                   (milliseconds).
     */
    @JsonCreator
    public ReportEntry(
            @JsonProperty("pipelineId") String pipelineId,
            @JsonProperty("level") ReportLevel level,
            @JsonProperty("message") String message,
            @JsonProperty("timestamp") long timestamp) {
        this.pipelineId = pipelineId;
        this.level = level;
        this.message = message;
        this.timestamp = timestamp;
    }

    /**
     * Retrieves the ID of the pipeline associated with this log entry.
     *
     * @return The pipeline ID.
     */
    public String getPipelineId() {
        return pipelineId;
    }

    /**
     * Retrieves the severity level of the log entry.
     *
     * @return The log level as an enum (e.g., LogLevel.SUCCESS, LogLevel.WARN).
     */
    public ReportLevel getLevel() {
        return level;
    }

    /**
     * Retrieves the message describing the log entry.
     *
     * @return The log message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Retrieves the timestamp of the log entry.
     *
     * @return The timestamp in milliseconds.
     */
    public long getTimestamp() {
        return timestamp;
    }
}
