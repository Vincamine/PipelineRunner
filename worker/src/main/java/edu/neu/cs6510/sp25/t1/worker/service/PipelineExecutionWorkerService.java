package edu.neu.cs6510.sp25.t1.worker.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

  @Value("${backend.api.url:http://backend-service/api}")
  private String backendApiUrl;

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

    // Pre-fetch and cache all dependencies to reduce redundant service calls
    Map<UUID, List<UUID>> dependenciesMap = new HashMap<>();
    jobExecutions.forEach(job -> {
      List<UUID> dependencies = workerCommunicationService.getJobDependencies(job.getId());
      dependenciesMap.put(job.getId(), dependencies);
    });

    // Use cached dependencies for filtering
    List<JobExecutionDTO> independentJobs = jobExecutions.stream()
            .filter(job -> dependenciesMap.get(job.getId()).isEmpty())
            .toList();

    List<JobExecutionDTO> dependentJobs = jobExecutions.stream()
            .filter(job -> !dependenciesMap.get(job.getId()).isEmpty())
            .toList();

    log.info("Starting independent jobs...");
    independentJobs.forEach(jobRunner::runJob);

    log.info("Processing dependent jobs...");
    // Pass cached dependencies to avoid redundant service calls
    dependentJobs.forEach(job -> waitAndRunDependentJob(job, dependenciesMap.get(job.getId())));
  }

  /**
   * Executes a single job execution request from the backend.
   *
   * @param job The job execution details.
   */
  public void executeJob(JobExecutionDTO job) {
    log.info("Executing single job request: {}", job.getId());
    jobRunner.runJob(job);
  }

  /**
   * Fetches all jobs for a pipeline execution.
   *
   * @param pipelineExecutionId The ID of the pipeline execution.
   * @return List of job executions.
   */
  private List<JobExecutionDTO> fetchJobsForPipelineExecution(UUID pipelineExecutionId) {
    String url = backendApiUrl + "/pipeline-executions/" + pipelineExecutionId + "/jobs";
    return restTemplate.getForObject(url, List.class);
  }

  /**
   * Waits for job dependencies to be completed before running the job.
   *
   * @param job The job to run.
   * @param dependencies The pre-fetched dependencies for the job.
   */
  private void waitAndRunDependentJob(JobExecutionDTO job, List<UUID> dependencies) {
    log.info("Waiting for dependencies of job {}", job.getId());

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