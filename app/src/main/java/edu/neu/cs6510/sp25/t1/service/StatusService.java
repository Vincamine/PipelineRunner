package edu.neu.cs6510.sp25.t1.service;

import java.time.Instant;

import edu.neu.cs6510.sp25.t1.model.PipelineState;
import edu.neu.cs6510.sp25.t1.model.PipelineStatus;

/**
 * Service class for retrieving pipeline status information.
 */
public class StatusService {
  /**
   * Retrieves the current status of a pipeline.
   *
   * @param pipelineId The ID of the pipeline to check.
   * @return The current status of the pipeline.
   * @throws RuntimeException if there's an error retrieving the status.
   */
  public PipelineStatus getPipelineStatus(String pipelineId) {
    // Validate pipeline ID
    if (pipelineId == null) {
      throw new IllegalArgumentException("Pipeline ID cannot be null");
    }
    if (pipelineId.trim().isEmpty()) {
      throw new IllegalArgumentException("Pipeline ID cannot be empty");
    }

    // TODO: Replace with actual API call to backend
    try {
      // Simulate API call delay
      Thread.sleep(1000);

      // Demo implementation - returns mock data
      final PipelineStatus status = new PipelineStatus(
          pipelineId,
          PipelineState.RUNNING,
          75,
          "Deploy to Staging",
          Instant.now().minusSeconds(300),
          Instant.now()
      );
      status.setMessage("Deploying to staging environment");
      return status;
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException("Error retrieving pipeline status: " + e.getMessage(), e);
    }
  }
}
