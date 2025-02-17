package edu.neu.cs6510.sp25.t1.model;

/**
 * Defines possible states for a pipeline execution.
 */
public enum PipelineState {
    PENDING("Pipeline is waiting to start"),
    RUNNING("Pipeline is currently executing"),
    SUCCEEDED("Pipeline completed successfully"),
    FAILED("Pipeline failed during execution"),
    CANCELLED("Pipeline was manually cancelled"),
    UNKNOWN("Status cannot be determined");

    private final String description;

    PipelineState(String description) {
        this.description = description;
    }

    /**
     * Retrieves the description of the pipeline state.
     *
     * @return A string describing the pipeline state.
     */
    public String getDescription() {
        return description;
    }
}
