package edu.neu.cs6510.sp25.t1.model;

/**
 * Represents the response containing the status of a pipeline.
 */
public class PipelineStatusResponse {
    private String pipelineRunId;
    private String status;

    public PipelineStatusResponse(String pipelineRunId, String status) {
        this.pipelineRunId = pipelineRunId;
        this.status = status;
    }

    public String getPipelineRunId() {
        return pipelineRunId;
    }

    public String getStatus() {
        return status;
    }
}
