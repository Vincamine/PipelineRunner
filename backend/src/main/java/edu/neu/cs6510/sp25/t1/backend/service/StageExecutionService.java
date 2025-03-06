package edu.neu.cs6510.sp25.t1.backend.service;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import edu.neu.cs6510.sp25.t1.backend.database.entity.JobExecutionEntity;
import edu.neu.cs6510.sp25.t1.backend.database.entity.StageExecutionEntity;
import edu.neu.cs6510.sp25.t1.backend.database.repository.StageExecutionRepository;
import edu.neu.cs6510.sp25.t1.backend.mapper.StageExecutionMapper;
import edu.neu.cs6510.sp25.t1.common.dto.StageExecutionDTO;
import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;
import lombok.RequiredArgsConstructor;

/**
 * Service responsible for managing stage executions.
 */
@Service
@RequiredArgsConstructor
public class StageExecutionService {
  private final StageExecutionRepository stageExecutionRepository;
  private final StageExecutionMapper stageExecutionMapper;
  private final JobExecutionService jobExecutionService;

  /**
   * Retrieves a stage execution by ID.
   */
  public StageExecutionDTO getStageExecution(UUID stageExecutionId) {
    return stageExecutionRepository.findById(stageExecutionId)
            .map(stageExecutionMapper::toDTO)
            .orElseThrow(() -> new IllegalArgumentException("Stage Execution not found"));
  }

  /**
   * Finalizes stage execution if all jobs have succeeded.
   */
  @Transactional
  public void finalizeStageExecution(UUID stageExecutionId) {
    List<JobExecutionEntity> jobExecutions = jobExecutionService.getJobsByStageExecution(stageExecutionId);
    boolean allSuccess = jobExecutions.stream().allMatch(j -> j.getStatus() == ExecutionStatus.SUCCESS);

    if (allSuccess) {
      StageExecutionEntity stageExecution = stageExecutionRepository.findById(stageExecutionId)
              .orElseThrow(() -> new IllegalArgumentException("Stage Execution not found"));
      stageExecution.updateState(ExecutionStatus.SUCCESS);
      stageExecutionRepository.save(stageExecution);
    }
  }
}
