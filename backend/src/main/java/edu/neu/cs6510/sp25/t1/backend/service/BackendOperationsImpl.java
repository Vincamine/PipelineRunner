package edu.neu.cs6510.sp25.t1.backend.service;

import org.springframework.stereotype.Service;
import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;
import edu.neu.cs6510.sp25.t1.common.service.BackendOperations;

/**
 * Backend implementation of BackendOperations interface.
 * This is used by the backend module to provide operation implementations
 * for other modules that need to communicate with the backend.
 */
@Service
public class BackendOperationsImpl implements BackendOperations {

  private final JobStatusCacheService jobStatusCacheService;
  private final JobExecutionTrackingService jobExecutionTrackingService;

  public BackendOperationsImpl(
          JobStatusCacheService jobStatusCacheService,
          JobExecutionTrackingService jobExecutionTrackingService) {
    this.jobStatusCacheService = jobStatusCacheService;
    this.jobExecutionTrackingService = jobExecutionTrackingService;
  }

  @Override
  public void sendJobStatus(String jobName, ExecutionStatus state) {
    // Use existing services to log job status
    jobStatusCacheService.cacheJobStatus(jobName, state);
    jobExecutionTrackingService.logJobExecution(jobName, state.name());
  }

  @Override
  public ExecutionStatus getJobStatus(String jobName) {
    // Use existing service to get status
    return jobStatusCacheService.getCachedJobStatus(jobName)
            .orElse(ExecutionStatus.FAILED);
  }
}