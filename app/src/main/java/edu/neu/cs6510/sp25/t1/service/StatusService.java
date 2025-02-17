package edu.neu.cs6510.sp25.t1.service;

import java.time.Instant;
import edu.neu.cs6510.sp25.t1.model.PipelineState;
import edu.neu.cs6510.sp25.t1.model.PipelineStatus;

/**
 * Service class responsible for retrieving pipeline execution status.
 * <p>
 * 🚀 **Mock Implementation** for demo purposes.
 * - This service **simulates** retrieving the pipeline status from an API.
 * - Once the backend API is available, **replace the mock logic with an actual API call**.
 * </p>
 */
public class StatusService {

    /**
     * Retrieves the current status of a pipeline.
     *
     * @param pipelineId The ID of the pipeline to check.
     * @return The current status of the pipeline (mock response).
     * @throws RuntimeException if there's an error retrieving the status.
     */
    public PipelineStatus getPipelineStatus(String pipelineId) {
        if (pipelineId == null || pipelineId.trim().isEmpty()) {
            throw new IllegalArgumentException("Pipeline ID cannot be null or empty.");
        }

        // 🚀 TODO: Replace this with an actual API call when available
        return new PipelineStatus(
            pipelineId,
            PipelineState.RUNNING,
            75,
            "Deploy to Staging",
            Instant.now().minusSeconds(300),
            Instant.now()
        );
    }
}
