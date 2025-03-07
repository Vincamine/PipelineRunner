package edu.neu.cs6510.sp25.t1.worker.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

import edu.neu.cs6510.sp25.t1.common.dto.JobExecutionDTO;
import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;
import edu.neu.cs6510.sp25.t1.worker.execution.JobRunner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Worker service for handling job execution with dependency resolution.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PipelineExecutionWorkerService {
  private final JobRunner jobRunner;
  private final WorkerCommunicationService workerCommunicationService;
  private final RestTemplate restTemplate;

  private static final String BACKEND_API_URL = "http://backend-service/api";

  /**
   * Executes all jobs for a given pipeline execution.
   *
   * @param pipelineExecutionId The ID of the pipeline execution.
   */
  public void executePipeline(UUID pipelineExecutionId) {
    log.info("Fetching job execution list for pipeline: {}", pipelineExecutionId);

    // Fetch all job executions for the given pipeline execution
    List<JobExecutionDTO> jobExecutions = fetchJobsForPipelineExecution(pipelineExecutionId);

    if (jobExecutions == null || jobExecutions.isEmpty()) {
      log.warn("No jobs found for pipeline execution: {}", pipelineExecutionId);
      return;
    }

    List<JobExecutionDTO> independentJobs = jobExecutions.stream()
            .filter(job -> workerCommunicationService.getJobDependencies(job.getId()).isEmpty())
            .toList();

    List<JobExecutionDTO> dependentJobs = jobExecutions.stream()
            .filter(job -> !workerCommunicationService.getJobDependencies(job.getId()).isEmpty())
            .toList();

    log.info("Starting independent jobs...");
    independentJobs.forEach(jobRunner::runJob);

    log.info("Processing dependent jobs...");
    dependentJobs.forEach(this::waitAndRunDependentJob);
  }

  /**
   * ✅ **NEW METHOD: Executes a single job execution request from the backend.**
   *
   * @param job The job execution details.
   */
  public void executeJob(JobExecutionDTO job) {
    log.info("Executing single job request: {}", job.getId());
    jobRunner.runJob(job);
  }

  private List<JobExecutionDTO> fetchJobsForPipelineExecution(UUID pipelineExecutionId) {
    String url = BACKEND_API_URL + "/pipeline-executions/" + pipelineExecutionId + "/jobs";
    return restTemplate.getForObject(url, List.class);
  }

  private void waitAndRunDependentJob(JobExecutionDTO job) {
    log.info("Waiting for dependencies of job {}", job.getId());

    List<UUID> dependencies = workerCommunicationService.getJobDependencies(job.getId());

    int attempts = 0;
    while (attempts < 10) {
      boolean allDependenciesCompleted = dependencies.stream()
              .allMatch(depId -> workerCommunicationService.getJobStatus(depId) == ExecutionStatus.SUCCESS);

      if (allDependenciesCompleted) {
        log.info("All dependencies resolved for job {}. Executing now.", job.getId());
        jobRunner.runJob(job);
        return;
      }

      log.warn("Dependencies not met for job {}. Retrying in 2 seconds...", job.getId());
      attempts++;
      try {
        Thread.sleep(2000);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        log.error("Interrupted while waiting for job dependencies: {}", job.getId(), e);
        return;
      }
    }
    log.error("Timeout: Dependencies for job {} were not resolved in time", job.getId());
  }
}
