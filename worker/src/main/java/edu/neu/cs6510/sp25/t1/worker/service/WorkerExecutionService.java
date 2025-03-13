package edu.neu.cs6510.sp25.t1.worker.service;

import edu.neu.cs6510.sp25.t1.common.dto.JobExecutionDTO;
import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;
import edu.neu.cs6510.sp25.t1.worker.execution.DockerExecutor;
import edu.neu.cs6510.sp25.t1.worker.api.client.WorkerBackendClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service for executing jobs and reporting status to the backend.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WorkerExecutionService {
  private final DockerExecutor dockerExecutor;
  private final WorkerBackendClient backendClient;

  /**
   * Executes a job and reports the status to the backend.
   *
   * @param job The job execution details.
   */
  public void executeJob(JobExecutionDTO job) {
    log.info("Starting execution for job: {}", job.getId());

    backendClient.updateJobStatus(job.getId(), ExecutionStatus.RUNNING, "Job execution started.");

    ExecutionStatus status = dockerExecutor.execute(job);
    String logs = "Job execution completed.";

    // ✅ If job fails but allows failure, report SUCCESS to not stop the pipeline
    if (status == ExecutionStatus.FAILED && job.isAllowFailure()) {
      log.warn("Job {} failed, but is allowed to fail. Reporting SUCCESS instead.", job.getId());
      backendClient.updateJobStatus(job.getId(), ExecutionStatus.SUCCESS, "Job failed but marked as allowFailure.");
    } else {
      backendClient.updateJobStatus(job.getId(), status, logs);
    }
  }
}
