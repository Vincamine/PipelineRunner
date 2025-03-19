package edu.neu.cs6510.sp25.t1.backend.api.client;

import org.springframework.stereotype.Component;
import java.util.UUID;
import java.util.Optional;
import edu.neu.cs6510.sp25.t1.common.logging.PipelineLogger;

/**
 * Stub client for job notifications.
 */
@Component
public class WorkerClient {
  /**
   * Simulates notifying about a job assignment.
   *
   * @param jobExecutionId The ID of the job execution.
   * @return Optional containing a success message
   */
  public Optional<String> notifyWorkerJobAssigned(UUID jobExecutionId) {
    PipelineLogger.info("Job notification simulation for job: " + jobExecutionId);
    return Optional.of("Job notification simulated successfully");
  }
}