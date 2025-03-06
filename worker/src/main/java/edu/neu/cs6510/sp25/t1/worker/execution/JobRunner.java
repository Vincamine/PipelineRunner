package edu.neu.cs6510.sp25.t1.worker.execution;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import edu.neu.cs6510.sp25.t1.common.dto.JobExecutionDTO;
import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;
import edu.neu.cs6510.sp25.t1.worker.service.WorkerCommunicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Executes jobs in the worker, ensuring dependencies are met before execution.
 */
@Service
@RequiredArgsConstructor
@Slf4j  // ✅ Use SLF4J Logger for structured logging
public class JobRunner {
  private final DockerExecutor dockerExecutor;
  private final WorkerCommunicationService workerCommunicationService;

  private static final int MAX_RETRY_ATTEMPTS = 10;  // ✅ Number of retries before failing
  private static final int RETRY_DELAY_MS = 2000;    // ✅ Wait time between retries (2 seconds)

  /**
   * Runs a job execution, ensuring dependencies are met before execution.
   *
   * @param job The job execution details.
   */
  public void runJob(JobExecutionDTO job) {
    if (!checkAndWaitForDependencies(job)) {
      log.error("Job {} failed due to unresolved dependencies", job.getId());
      workerCommunicationService.reportJobStatus(job.getId(), ExecutionStatus.FAILED, "Dependency check failed.");
      return;
    }

    log.info("Executing job: {}", job.getId());
    ExecutionStatus status = dockerExecutor.execute(job);

    log.info("Job {} execution finished with status: {}", job.getId(), status);

    // ✅ If job fails but allows failure, report SUCCESS to not stop the pipeline
    if (status == ExecutionStatus.FAILED && job.isAllowFailure()) {
      log.warn("Job {} failed, but is allowed to fail. Reporting SUCCESS instead.");
      workerCommunicationService.reportJobStatus(job.getId(), ExecutionStatus.SUCCESS, "Job failed but marked as allowFailure.");
    } else {
      workerCommunicationService.reportJobStatus(job.getId(), status, "Job execution completed.");
    }
  }

  /**
   * Waits for all job dependencies to complete before execution.
   *
   * @param job The job execution details.
   * @return True if dependencies are resolved, False if dependencies failed or timeout occurred.
   */
  private boolean checkAndWaitForDependencies(JobExecutionDTO job) {
    List<UUID> dependencies = workerCommunicationService.getJobDependencies(job.getId());

    if (dependencies.isEmpty()) {
      log.info("Job {} has no dependencies. Proceeding with execution.", job.getId());
      return true;
    }

    log.info("Job {} waiting for dependencies to complete: {}", job.getId(), dependencies);

    int attempts = 0;
    while (attempts < MAX_RETRY_ATTEMPTS) {
      boolean allDependenciesCompleted = dependencies.stream()
              .allMatch(depId -> workerCommunicationService.getJobStatus(depId) == ExecutionStatus.SUCCESS);

      if (allDependenciesCompleted) {
        log.info("All dependencies resolved for job {}. Proceeding with execution.", job.getId());
        return true;
      }

      log.warn("Dependencies not met for job {}. Retrying in {} ms...", job.getId(), RETRY_DELAY_MS);
      attempts++;

      try {
        Thread.sleep(RETRY_DELAY_MS);  // ✅ Wait before retrying
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        log.error("Dependency check interrupted for job {}", job.getId(), e);
        return false;
      }
    }

    log.error("Timeout: Dependencies for job {} were not resolved in time", job.getId());
    return false;
  }
}
