package edu.neu.cs6510.sp25.t1.backend.service;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import edu.neu.cs6510.sp25.t1.backend.database.entity.JobExecutionEntity;
import edu.neu.cs6510.sp25.t1.backend.database.repository.JobExecutionRepository;
import edu.neu.cs6510.sp25.t1.backend.mapper.JobExecutionMapper;
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
   * Retrieves all job executions for a given stage execution.
   *
   * @param stageExecutionId the stage execution ID
   * @return List of JobExecutionEntities
   */
  public List<JobExecutionEntity> getJobsByStageExecution(UUID stageExecutionId) {
    return jobExecutionRepository.findByStageExecutionId(stageExecutionId);
  }
}
