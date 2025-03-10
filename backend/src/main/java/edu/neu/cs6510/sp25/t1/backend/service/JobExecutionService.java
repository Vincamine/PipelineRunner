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
   *
   * @param jobExecutionId the job execution ID
   * @return the job execution DTO
   */
  public JobExecutionDTO getJobExecution(UUID jobExecutionId) {
    return jobExecutionRepository.findById(jobExecutionId)
            .map(jobExecutionMapper::toDTO)
            .orElseThrow(() -> new IllegalArgumentException("Job Execution not found"));
  }

  /**
   * Starts a new job execution.
   *
   * @param request the job execution request
   * @return the job execution response
   */
  @Transactional
  public JobExecutionResponse startJobExecution(JobExecutionRequest request) {
    JobExecutionEntity newJobExecution = JobExecutionEntity.builder()
            .jobId(request.getJobId())
            .stageExecutionId(request.getStageExecutionId())
            .commitHash(request.getCommitHash())
            .isLocal(request.isLocal())
            .status(ExecutionStatus.PENDING)
            .build();
    newJobExecution = jobExecutionRepository.save(newJobExecution);
    return new JobExecutionResponse(newJobExecution.getId().toString(), "QUEUED");
  }

  /**
   * Updates the status of a job execution.
   *
   * @param jobExecutionId the job execution ID
   * @param newStatus      the new status
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
   *
   * @param jobExecutionId the job execution ID
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


  @Transactional(readOnly = true)
  public List<UUID> getJobDependencies(UUID jobId) {
    return jobExecutionRepository.findDependenciesByJobId(jobId);
  }

  // Not used in the current implementation
//  @Transactional
//  public void saveArtifactPaths(UUID jobExecutionId, List<String> artifactPaths) {
//    JobExecutionEntity jobExecution = jobExecutionRepository.findById(jobExecutionId)
//            .orElseThrow(() -> new IllegalArgumentException("Job Execution not found"));
//
//    jobExecution.setArtifacts(artifactPaths);
//    jobExecutionRepository.save(jobExecution);
//  }

}
