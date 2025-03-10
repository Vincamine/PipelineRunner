package edu.neu.cs6510.sp25.t1.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import edu.neu.cs6510.sp25.t1.backend.database.entity.PipelineExecutionEntity;
import edu.neu.cs6510.sp25.t1.backend.database.repository.PipelineExecutionRepository;
import edu.neu.cs6510.sp25.t1.backend.mapper.PipelineExecutionMapper;
import edu.neu.cs6510.sp25.t1.common.api.request.PipelineExecutionRequest;
import edu.neu.cs6510.sp25.t1.common.api.response.PipelineExecutionResponse;
import edu.neu.cs6510.sp25.t1.common.dto.PipelineExecutionDTO;
import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;
import lombok.RequiredArgsConstructor;

/**
 * Service responsible for managing pipeline executions.
 */
@Service
@RequiredArgsConstructor
public class PipelineExecutionService {
  private final PipelineExecutionRepository pipelineExecutionRepository;
  private final PipelineExecutionMapper pipelineExecutionMapper;

  /**
   * Retrieves a pipeline execution by ID.
   *
   * @param pipelineExecutionId ID of the pipeline execution
   * @return response containing pipeline execution ID and status
   */
  public PipelineExecutionResponse getPipelineExecution(UUID pipelineExecutionId) {
    PipelineExecutionDTO dto = pipelineExecutionRepository.findById(pipelineExecutionId)
            .map(pipelineExecutionMapper::toDTO)
            .orElseThrow(() -> new IllegalArgumentException("Pipeline Execution not found"));
    return new PipelineExecutionResponse(dto.getId().toString(), dto.getStatus().toString());
  }

  /**
   * Starts a new run of a pipeline.
   *
   * @param request request containing pipeline ID and commit hash
   * @return response containing pipeline execution ID and status
   */
  @Transactional
  public PipelineExecutionResponse startPipelineExecution(PipelineExecutionRequest request) {
    PipelineExecutionEntity newExecution = PipelineExecutionEntity.builder()
            .pipelineId(request.getPipelineId())
            .commitHash(request.getCommitHash())
            .isLocal(request.isLocal())
            .status(ExecutionStatus.PENDING)
            .startTime(Instant.now())
            .build();
    newExecution = pipelineExecutionRepository.save(newExecution);
    return new PipelineExecutionResponse(newExecution.getId().toString(), "PENDING");
  }

  /**
   * Checks if a pipeline execution with the same pipeline ID and run number already exists.
   *
   * @param request request containing pipeline ID and run number
   * @return true if a duplicate execution exists, false otherwise
   */
  public boolean isDuplicateExecution(PipelineExecutionRequest request) {
    Optional<PipelineExecutionEntity> existingExecution = pipelineExecutionRepository.findByPipelineIdAndRunNumber(
            request.getPipelineId(), request.getRunNumber()
    );
    return existingExecution.isPresent();
  }

  /**
   * Finalizes a pipeline execution.
   * @param pipelineExecutionId ID of the pipeline execution
   */
  @Transactional
  public void finalizePipelineExecution(UUID pipelineExecutionId) {
    PipelineExecutionEntity pipelineExecution = pipelineExecutionRepository.findById(pipelineExecutionId)
            .orElseThrow(() -> new IllegalArgumentException("Pipeline Execution not found"));

    pipelineExecution.updateState(ExecutionStatus.SUCCESS);
    pipelineExecutionRepository.save(pipelineExecution);
  }

}
