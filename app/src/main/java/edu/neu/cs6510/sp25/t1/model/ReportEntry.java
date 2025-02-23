package edu.neu.cs6510.sp25.t1.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Represents a report entry in the CI/CD system.
 * Each report entry records information about a pipeline event,
 * including its severity level, message, timestamps, and status.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReportEntry {
    private final String pipelineId;
    private final ReportLevel level;
    private final String message;
    private final long timestamp;
    private final String status;
    private final List<StageInfo> stages;
    private final List<String> details;
    private final int runNumber; // Added
    private final String gitCommitHash; // Added
    private final long startTime; // Added
    private final long completionTime; // Added

    /**
     * Constructs a new ReportEntry instance.
     */
    @JsonCreator
    public ReportEntry(
            @JsonProperty("pipelineId") String pipelineId,
            @JsonProperty("level") String level,
            @JsonProperty("message") String message,
            @JsonProperty("timestamp") long timestamp,
            @JsonProperty("status") String status,
            @JsonProperty("stages") List<StageInfo> stages,
            @JsonProperty("details") List<String> details,
            @JsonProperty("runNumber") int runNumber,
            @JsonProperty("gitCommitHash") String gitCommitHash,
            @JsonProperty("startTime") long startTime,
            @JsonProperty("completionTime") long completionTime) {
        this.pipelineId = pipelineId;
        this.level = ReportLevel.fromString(level);
        this.message = message;
        this.timestamp = timestamp;
        this.status = status;
        this.stages = stages;
        this.details = details;
        this.runNumber = runNumber;
        this.gitCommitHash = gitCommitHash;
        this.startTime = startTime;
        this.completionTime = completionTime;
    }

    public String getPipelineId() { return pipelineId; }
    public ReportLevel getLevel() { return level != null ? level : ReportLevel.SUCCESS; }
    public String getMessage() { return message; }
    public long getTimestamp() { return timestamp; }
    public String getStatus() { return status; }
    public List<StageInfo> getStages() { return stages; }
    public List<String> getDetails() { return details; }
    public int getRunNumber() { return runNumber; }
    public String getGitCommitHash() { return gitCommitHash; }
    public long getStartTime() { return startTime; }
    public long getCompletionTime() { return completionTime; }
}
