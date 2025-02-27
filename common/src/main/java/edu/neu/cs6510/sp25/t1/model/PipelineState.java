package edu.neu.cs6510.sp25.t1.model;

/**
 * Defines possible states for a pipeline execution. All excutestatus changed into this file. 
 */
public enum PipelineState {
    PENDING("Pipeline Execution is waiting to start"),
    RUNNING("Pipeline Execution is currently executing"),
    SUCCESS("Pipeline Execution completed successfully"),
    FAILED("Pipeline Execution failed during execution"),
    CANCELED("Pipeline Execution was manually canceled"), 
    UNKNOWN("Execution Status cannot be determined");

    private final String description;

    /**
     * Constructor to initialize the pipeline state with a description.
     * @param description
     */
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

    /**
     * Converts a string status to a `PipelineState` enum value.
     *
     * @param value The string representation of the pipeline state.
     * @return The corresponding `PipelineState` enum.
     */
    public static PipelineState fromString(String value) {
        if (value == null) {
            return UNKNOWN;
        }
        switch (value.toLowerCase()) {
            case "pending": return PENDING;
            case "running": return RUNNING;
            case "success": return SUCCESS;
            case "failed": return FAILED;
            case "canceled": return CANCELED;
            default: return UNKNOWN;
        }
    }
}
