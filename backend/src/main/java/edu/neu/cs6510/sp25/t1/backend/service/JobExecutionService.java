package edu.neu.cs6510.sp25.t1.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

import edu.neu.cs6510.sp25.t1.backend.api.client.WorkerClient;
import edu.neu.cs6510.sp25.t1.backend.data.dto.JobExecutionDTO;
import edu.neu.cs6510.sp25.t1.backend.data.entity.JobEntity;
import edu.neu.cs6510.sp25.t1.backend.data.entity.JobExecutionEntity;
import edu.neu.cs6510.sp25.t1.backend.data.repository.JobExecutionRepository;
import edu.neu.cs6510.sp25.t1.backend.model.JobExecution;
import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;
import edu.neu.cs6510.sp25.t1.worker.api.client.WorkerBackendClient;

/**
 * Service for tracking job execution results and updating stage completion status.
 */
@Service
public class JobExecutionService {

  private final JobExecutionRepository jobExecutionRepository;
  private final StageExecutionService stageExecutionService;
  private final WorkerClient workerClient;

  public JobExecutionService(JobExecutionRepository jobExecutionRepository,
                             StageExecutionService stageExecutionService,
                             WorkerClient workerClient) {
    this.jobExecutionRepository = jobExecutionRepository;
    this.stageExecutionService = stageExecutionService;
    this.workerClient = workerClient;
  }

  /**
   * Sends a job to the worker for execution.
   *
   * @param jobExecution JobExecutionEntity instance.
   */
  public void executeJob(JobExecutionEntity jobExecution) {
    // Convert JobExecutionEntity -> JobExecution Model
    JobExecution jobModel = new JobExecution(
            jobExecution.getJob().getName(),
            jobExecution.getJob().getImage(),
            jobExecution.getJob().getScript(),
            jobExecution.getJob().getDependencies() != null ?
                    jobExecution.getJob().getDependencies().stream()
                            .map(JobEntity::getName) // Convert dependencies to list of names
                            .collect(Collectors.toList()) :
                    Collections.emptyList(),
            jobExecution.getJob().isAllowFailure()
    );

    boolean success = workerClient.sendJob(jobModel);

    if (!success) {
      jobExecution.setStatus(ExecutionStatus.FAILED);
      jobExecution.setCompletionTime(Instant.now());
      jobExecutionRepository.save(jobExecution);
      stageExecutionService.checkStageCompletion(jobExecution.getStageExecution());
    }
  }


  /**
   * Marks a job as completed and updates its execution status.
   *
   * @param jobExecutionId The job execution ID.
   * @param status         The final job status.
   * @return The updated JobExecutionDTO.
   */
  @Transactional
  public JobExecutionDTO completeJobExecution(Long jobExecutionId, ExecutionStatus status) {
    JobExecutionEntity jobExecution = jobExecutionRepository.findById(jobExecutionId)
            .orElseThrow(() -> new IllegalArgumentException("Job Execution not found: " + jobExecutionId));

    jobExecution.setStatus(status);
    jobExecution.setCompletionTime(Instant.now());
    jobExecutionRepository.save(jobExecution);

    stageExecutionService.checkStageCompletion(jobExecution.getStageExecution());  // ✅ Move logic to StageExecutionService

    return JobExecutionDTO.fromEntity(jobExecution);
  }

  /**
   * Retrieves job execution details by ID.
   *
   * @param jobExecutionId The job execution ID.
   * @return The job execution DTO.
   */
  public JobExecutionDTO getJobExecution(Long jobExecutionId) {
    JobExecutionEntity entity = jobExecutionRepository.findById(jobExecutionId)
            .orElseThrow(() -> new IllegalArgumentException("Job Execution not found: " + jobExecutionId));
    return JobExecutionDTO.fromEntity(entity);
  }

  /**
   * Fetches logs for a specific job execution.
   *
   * @param jobExecutionId The job execution ID.
   * @return The logs for the job execution.
   */
  public Optional<String> getJobLogs(Long jobExecutionId) {
    return Optional.of("Logs for Job Execution " + jobExecutionId);  // Placeholder
  }

  /**
   * Retrieves the execution status of a specific job execution.
   *
   * @param jobExecutionId The job execution ID.
   * @return The job execution status.
   */
  public boolean updateJobStatus(Long jobExecutionId, ExecutionStatus status) {
    JobExecutionEntity jobExecution = jobExecutionRepository.findById(jobExecutionId)
            .orElseThrow(() -> new IllegalArgumentException("Job Execution not found: " + jobExecutionId));

    jobExecution.setStatus(status);
    jobExecutionRepository.save(jobExecution);
    return true;
  }

  public boolean updateJobResults(Long jobExecutionId, WorkerBackendClient.JobResponse jobResponse) {
    JobExecutionEntity jobExecution = jobExecutionRepository.findById(jobExecutionId)
            .orElseThrow(() -> new IllegalArgumentException("Job Execution not found: " + jobExecutionId));

    jobExecution.setStatus(jobResponse.getStatus());
    jobExecution.setCompletionTime(Instant.now());

    // Add additional processing if required
    jobExecutionRepository.save(jobExecution);
    return true;
  }


}
