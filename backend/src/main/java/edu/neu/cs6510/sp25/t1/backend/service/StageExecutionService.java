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
   * Retrieves all stage executions for a
   *
   * @param stageExecutionId stage execution ID
   * @return list of stage executions
   */
  public StageExecutionDTO getStageExecution(UUID stageExecutionId) {
    return stageExecutionRepository.findById(stageExecutionId)
            .map(stageExecutionMapper::toDTO)
            .orElseThrow(() -> new IllegalArgumentException("Stage Execution not found"));
  }

  /**
   * Retrieves all stage executions for a
   *
   * @param pipelineExecutionId pipeline Execution ID
   * @param stageId             stage ID
   * @return list of stage executions
   */
  public String getStageStatus(UUID pipelineExecutionId, UUID stageId) {
    return stageExecutionRepository.findByPipelineExecutionIdAndStageId(pipelineExecutionId, stageId)
            .map(stageExecution -> stageExecution.getStatus().toString())
            .orElse("Stage not found");
  }

  /**
   * finalizes a stage execution
   *
   * @param stageExecutionId stage execution ID
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
