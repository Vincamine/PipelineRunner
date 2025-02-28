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
    private List<String> dependencies;

    /**
     * Constructs a new JobExecution instance based on its definition.
     * @param jobDefinition the job definition
     * @param status the initial status of the job
     * @param allowFailure whether the job can fail without failing the pipeline
     * @param dependencies the list of job names that this job depends on
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
     * Marks the job as completed by setting the completion time.
     */
    public void complete() {
        this.completionTime = Instant.now();
    }

    /**
     * Getter for job name.
     * @return job name
     */
    public String getJobName() { return jobDefinition.getName(); }

    /**
     * Getter for stage name.
     * @return stage name
     */
    public String getStageName() { return jobDefinition.getStageName(); }

    /**
     * Getter for job execution status.
     * @return job status
     */
    public String getStatus() { return status; }

    /**
     * Checks if the job allows failure.
     * @return true if failure is allowed, false otherwise
     */
    public boolean isAllowFailure() { return allowFailure; }

    /**
     * Getter for job start time.
     * @return job start time
     */
    public Instant getStartTime() { return startTime; }

    /**
     * Getter for job completion time.
     * @return job completion time
     */
    public Instant getCompletionTime() { return completionTime; }

    /**
     * Getter for dependencies.
     * @return list of job dependencies
     */
    public List<String> getDependencies() { return dependencies; }

    /**
     * Getter for job definition.
     * @return job definition
     */
    public JobDefinition getJobDefinition() {
        return jobDefinition;
    }
    
}
