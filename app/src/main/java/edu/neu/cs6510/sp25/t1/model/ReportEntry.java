package edu.neu.cs6510.sp25.t1.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Represents a report entry in the CI/CD system.
 * Each report entry records information about a pipeline event,
 * including its severity level, message, timestamp, and status.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReportEntry {
    private final String pipelineId;
    private final ReportLevel level;
    private final String message;
    private final long timestamp;
    private final String status;
    private final List<String> stages;
    private final List<String> details;

    /**
     * Constructs a new ReportEntry instance.
     *
     * @param pipelineId The ID of the pipeline associated with this report entry.
     * @param level      The severity level of the report (e.g., SUCCESS, WARN, FAILED).
     * @param message    A description of the event being logged.
     * @param timestamp  The timestamp when the report entry was created (milliseconds).
     * @param status     The status of the report (e.g., SUCCESS, FAILED, CANCELED).
     * @param stages     The list of stages in the pipeline run.
     * @param details    Additional details about the report entry.
     */
    @JsonCreator
    public ReportEntry(
            @JsonProperty("pipelineId") String pipelineId,
            @JsonProperty("level") String level,
            @JsonProperty("message") String message,
            @JsonProperty("timestamp") long timestamp,
            @JsonProperty("status") String status,
            @JsonProperty("stages") List<String> stages,
            @JsonProperty("details") List<String> details) {
        this.pipelineId = pipelineId;
        this.level = ReportLevel.fromString(level);
        this.message = message;
        this.timestamp = timestamp;
        this.status = status;
        this.stages = stages;
        this.details = details;
    }

    /**
     * Gets the pipeline identifier.
     *
     * @return The pipeline ID.
     */
    public String getPipelineId() {
        return pipelineId;
    }

    /**
     * Gets the report severity level.
     *
     * @return The report level.
     */
    public ReportLevel getLevel() {
        return level != null ? level : ReportLevel.SUCCESS;
    }

    /**
     * Gets the report message.
     *
     * @return The message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Gets the report timestamp.
     *
     * @return The timestamp in milliseconds.
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Gets the report status.
     *
     * @return The status.
     */
    public String getStatus() {
        return status;
    }

    /**
     * Gets the list of stages.
     *
     * @return The list of stages.
     */
    public List<String> getStages() {
        return stages;
    }

    /**
     * Gets the additional details.
     *
     * @return The list of details.
     */
    public List<String> getDetails() {
        return details;
    }
}