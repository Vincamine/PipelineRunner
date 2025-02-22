package edu.neu.cs6510.sp25.t1.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.List;

/**
 * Represents a report entry in the CI/CD system.
 * Each report entry records information about a pipeline event,
 * including its severity level, message, timestamp, and status.
 */
public class ReportEntry {
    private final String pipelineId;
    private final ReportLevel level;
    private final String message;
    private final long timestamp;
    private final String status;
    private final List<StageInfo> stages;

    /**
     * Constructs a new ReportEntry instance.
     *
     * @param pipelineId The ID of the pipeline associated with this report entry.
     * @param level      The severity level of the report (e.g., SUCCESS, WARN, FAILED).
     * @param message    A description of the event being logged.
     * @param timestamp  The timestamp when the report entry was created (milliseconds).
     * @param status     The status of the report (e.g., SUCCESS, FAILED, CANCELED).
     * @param stages     The list of stages in the pipeline run.
     */
    @JsonCreator
    public ReportEntry(
        @JsonProperty("pipelineId") String pipelineId,
        @JsonProperty("level") ReportLevel level,
        @JsonProperty("message") String message,
        @JsonProperty("timestamp") long timestamp,
        @JsonProperty("status") String status,
        @JsonProperty("stages") List<StageInfo> stages) {
        this.pipelineId = pipelineId;
        this.level = level;
        this.message = message;
        this.timestamp = timestamp;
        this.status = status;
        this.stages = stages;
    }

    public String getPipelineId() {
        return pipelineId;
    }

    public ReportLevel getLevel() {
        return level;
    }

    public String getMessage() {
        return message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getStatus() {
        return status;
    }

    public List<StageInfo> getStages() {
        return stages;
    }
}
