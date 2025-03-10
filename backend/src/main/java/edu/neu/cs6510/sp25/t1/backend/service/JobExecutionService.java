package edu.neu.cs6510.sp25.t1.backend.service;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import edu.neu.cs6510.sp25.t1.backend.database.entity.JobExecutionEntity;
import edu.neu.cs6510.sp25.t1.backend.database.repository.JobExecutionRepository;
import edu.neu.cs6510.sp25.t1.backend.mapper.JobExecutionMapper;
import edu.neu.cs6510.sp25.t1.common.api.request.JobExecutionRequest;
import edu.neu.cs6510.sp25.t1.common.api.response.JobExecutionResponse;
import edu.neu.cs6510.sp25.t1.common.dto.JobExecutionDTO;
import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;
import lombok.RequiredArgsConstructor;

/**
 * Service responsible for managing job executions.
 */
@Service
@RequiredArgsConstructor
public class JobExecutionService {
  private final JobExecutionRepository jobExecutionRepository;
  private final JobExecutionMapper jobExecutionMapper;

  /**
   * Retrieves a job execution by ID.
   */
  public JobExecutionDTO getJobExecution(UUID jobExecutionId) {
    return jobExecutionRepository.findById(jobExecutionId)
            .map(jobExecutionMapper::toDTO)
            .orElseThrow(() -> new IllegalArgumentException("Job Execution not found"));
  }

  /**
   * Starts a new job execution.
   */
  @Transactional
  public JobExecutionResponse startJobExecution(JobExecutionRequest request) {

    if (request.getJobName() == null || request.getJobName().isEmpty()) {
      throw new IllegalArgumentException("Job name cannot be null or empty");
    }

    JobExecutionEntity newJobExecution = JobExecutionEntity.builder()
            .jobId(request.getJobId())
            .stageExecutionId(request.getStageExecutionId())
            .commitHash(request.getCommitHash())
            .isLocal(request.isLocal())
            .status(ExecutionStatus.PENDING)
            .jobName(request.getJobName())
            .build();
    newJobExecution = jobExecutionRepository.save(newJobExecution);
    return new JobExecutionResponse(newJobExecution.getId().toString(), "QUEUED");
  }

  /**
   * Updates job execution status (called when worker reports back).
   */
  @Transactional
  public void updateJobExecutionStatus(UUID jobExecutionId, ExecutionStatus newStatus) {
    JobExecutionEntity jobExecution = jobExecutionRepository.findById(jobExecutionId)
            .orElseThrow(() -> new IllegalArgumentException("Job Execution not found"));

    jobExecution.updateState(newStatus);
    jobExecutionRepository.save(jobExecution);
  }

  /**
   * Cancels a job execution.
   */
  @Transactional
  public void cancelJobExecution(UUID jobExecutionId) {
    JobExecutionEntity jobExecution = jobExecutionRepository.findById(jobExecutionId)
            .orElseThrow(() -> new IllegalArgumentException("Job Execution not found"));

    if (jobExecution.getStatus() == ExecutionStatus.PENDING || jobExecution.getStatus() == ExecutionStatus.RUNNING) {
      jobExecution.updateState(ExecutionStatus.CANCELED);
      jobExecutionRepository.save(jobExecution);
    }
  }

  /**
   * Retrieves all job executions for a given stage execution.
   *
   * @param stageExecutionId the stage execution ID
   * @return List of JobExecutionEntities
   */
  public List<JobExecutionEntity> getJobsByStageExecution(UUID stageExecutionId) {
    return jobExecutionRepository.findByStageExecutionId(stageExecutionId);
  }
}
