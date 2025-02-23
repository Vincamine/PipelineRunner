package edu.neu.cs6510.sp25.t1.model;

/**
 * Defines possible states for a pipeline execution.
 */
public enum PipelineState {
    PENDING("Pipeline is waiting to start"),
    RUNNING("Pipeline is currently executing"),
    SUCCESS("Pipeline completed successfully"),
    FAILED("Pipeline failed during execution"),
    CANCELED("Pipeline was manually canceled"), 
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
