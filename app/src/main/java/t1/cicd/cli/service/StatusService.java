package t1.cicd.cli.service;

import java.time.Instant;
import t1.cicd.cli.model.PipelineState;
import t1.cicd.cli.model.PipelineStatus;

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
    // TODO: Replace with actual API call to backend
    try {
      // Simulate API call delay
      Thread.sleep(1000);

      // Demo implementation - returns mock data
      PipelineStatus status = new PipelineStatus(
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
