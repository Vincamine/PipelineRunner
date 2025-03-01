package edu.neu.cs6510.sp25.t1.common.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents the state of a pipeline execution
 */
public enum PipelineState {
    PENDING("Pipeline Execution is waiting to start"),
    RUNNING("Pipeline Execution is currently executing"),
    SUCCESS("Pipeline Execution completed successfully"),
    FAILED("Pipeline Execution failed during execution"),
    CANCELED("Pipeline Execution was manually canceled"),
    UNKNOWN("Execution Status cannot be determined");

    private final String description;
    private static final Map<String, PipelineState> STRING_TO_ENUM = new HashMap<>();

    // Populate the map with enum values
    static {
        for (PipelineState state : values()) {
            STRING_TO_ENUM.put(state.name().toLowerCase(), state);
        }
    }

    /**
     * Constructor for PipelineState enum.
     * 
     * @param description A string describing the pipeline state.
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
     * Uses a lookup map for better performance.
     *
     * @param value The string representation of the pipeline state.
     * @return The corresponding `PipelineState` enum, or `UNKNOWN` if not found.
     */
    public static PipelineState fromString(String value) {
        if (value == null) {
            return UNKNOWN;
        }
        return STRING_TO_ENUM.getOrDefault(value.toLowerCase(), UNKNOWN);
    }
}
