package edu.neu.cs6510.sp25.t1.worker.service;

import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;
import edu.neu.cs6510.sp25.t1.worker.api.client.WorkerBackendClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Service to communicate job execution results back to the backend.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WorkerCommunicationService {
  private final WorkerBackendClient backendClient;

  public List<UUID> getJobDependencies(UUID jobId) {
    return backendClient.getJobDependencies(jobId);
  }

  public ExecutionStatus getJobStatus(UUID jobId) {
    return backendClient.getJobStatus(jobId);
  }

  public void reportJobStatus(UUID jobExecutionId, ExecutionStatus status, String logs) {
    backendClient.updateJobStatus(jobExecutionId, status, logs);
    log.info("Reported job {} status: {}", jobExecutionId, status);
  }
}
