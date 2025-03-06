package edu.neu.cs6510.sp25.t1.backend.service;

import edu.neu.cs6510.sp25.t1.backend.database.entity.PipelineExecutionEntity;
import edu.neu.cs6510.sp25.t1.backend.database.repository.PipelineExecutionRepository;
import edu.neu.cs6510.sp25.t1.backend.mapper.PipelineExecutionMapper;
import edu.neu.cs6510.sp25.t1.common.dto.PipelineExecutionDTO;

import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Service responsible for managing pipeline executions.
 */
@Service
@RequiredArgsConstructor
public class PipelineExecutionService {
  private final PipelineExecutionRepository pipelineExecutionRepository;
  private final PipelineExecutionMapper pipelineExecutionMapper;
  private final StageExecutionService stageExecutionService;

  /**
   * Retrieves a pipeline execution by ID.
   */
  public PipelineExecutionDTO getPipelineExecution(UUID pipelineExecutionId) {
    return pipelineExecutionRepository.findById(pipelineExecutionId)
            .map(pipelineExecutionMapper::toDTO)  // Explicit lambda
            .orElseThrow(() -> new IllegalArgumentException("Pipeline Execution not found"));
  }


  /**
   * Finalizes pipeline execution when all stages complete.
   */
  @Transactional
  public void finalizePipelineExecution(UUID pipelineExecutionId) {
    PipelineExecutionEntity pipelineExecution = pipelineExecutionRepository.findById(pipelineExecutionId)
            .orElseThrow(() -> new IllegalArgumentException("Pipeline Execution not found"));

    pipelineExecution.updateState(ExecutionStatus.SUCCESS);
    pipelineExecutionRepository.save(pipelineExecution);
  }
}
