package edu.neu.cs6510.sp25.t1.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a stage within a pipeline run in the CI/CD system.
 */
public class StageInfo {
    private String stageName;
    private String stageStatus;
    private long startTime;
    private long completionTime;
    private List<String> jobs;

    /**
     * Default constructor required for Jackson deserialization.
     */
    public StageInfo() {
        this.jobs = new ArrayList<>();
    }

    /**
     * Constructs a new StageInfo instance.
     */
    @JsonCreator
    public StageInfo(
            @JsonProperty("stageName") String stageName,
            @JsonProperty("stageStatus") String stageStatus,
            @JsonProperty("startTime") long startTime,
            @JsonProperty("completionTime") long completionTime,
            @JsonProperty("jobs") List<String> jobs) {
        this.stageName = stageName;
        this.stageStatus = stageStatus;
        this.startTime = startTime;
        this.completionTime = completionTime;
        this.jobs = jobs != null ? jobs : new ArrayList<>();
    }

    public String getStageName() {
        return stageName;
    }

    public String getStageStatus() {
        return stageStatus;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getCompletionTime() {
        return completionTime;
    }

    public List<String> getJobs() {
        return jobs;
    }
}
