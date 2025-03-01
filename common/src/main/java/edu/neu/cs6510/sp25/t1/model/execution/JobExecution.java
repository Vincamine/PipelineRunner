package edu.neu.cs6510.sp25.t1.model.execution;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.neu.cs6510.sp25.t1.model.definition.JobDefinition;
import java.time.Instant;
import java.util.List;

/**
 * Represents a job execution instance within a pipeline stage.
 */
public class JobExecution {
    private final JobDefinition jobDefinition;
    private String status;
    private boolean allowFailure;
    private Instant startTime;
    private Instant completionTime;
    @SuppressWarnings("unused")
    private List<String> dependencies;

    /**
     * Constructs a new JobExecution instance based on its definition.
     */
    @JsonCreator
    public JobExecution(
            @JsonProperty("jobDefinition") JobDefinition jobDefinition,
            @JsonProperty("status") String status,
            @JsonProperty("allowFailure") boolean allowFailure,
            @JsonProperty("dependencies") List<String> dependencies) {
        this.jobDefinition = jobDefinition;
        this.status = status;
        this.allowFailure = allowFailure;
        this.dependencies = dependencies;
        this.startTime = Instant.now();
    }

    /**
     * Marks the job as started.
     */
    public void start() {
        this.status = "RUNNING";
        this.startTime = Instant.now();
    }

    /**
     * Marks the job as completed.
     */
    public void complete(String finalStatus) {
        this.status = finalStatus;
        this.completionTime = Instant.now();
    }

    public String getJobName() { return jobDefinition.getName(); }
    public String getStatus() { return status; }
    public boolean isAllowFailure() { return allowFailure; }
    public Instant getStartTime() { return startTime; }
    public Instant getCompletionTime() { return completionTime; }
}
