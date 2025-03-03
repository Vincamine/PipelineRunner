package edu.neu.cs6510.sp25.t1.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import edu.neu.cs6510.sp25.t1.backend.data.dto.PipelineExecutionDTO;
import edu.neu.cs6510.sp25.t1.backend.data.entity.PipelineEntity;
import edu.neu.cs6510.sp25.t1.backend.data.entity.PipelineExecutionEntity;
import edu.neu.cs6510.sp25.t1.backend.data.entity.StageEntity;
import edu.neu.cs6510.sp25.t1.backend.data.repository.PipelineExecutionRepository;
import edu.neu.cs6510.sp25.t1.backend.data.repository.PipelineRepository;
import edu.neu.cs6510.sp25.t1.backend.data.repository.StageExecutionRepository;
import edu.neu.cs6510.sp25.t1.backend.data.repository.StageRepository;
import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;

/**
 * Service for managing pipeline execution, including stage execution.
 */
@Service
public class PipelineExecutionService {

  private final PipelineExecutionRepository pipelineExecutionRepository;
  private final PipelineRepository pipelineRepository;
  private final StageRepository stageRepository;
  private final StageExecutionService stageExecutionService;
  private final StageExecutionRepository stageExecutionRepository;

  public PipelineExecutionService(
          PipelineExecutionRepository pipelineExecutionRepository,
          PipelineRepository pipelineRepository,
          StageRepository stageRepository,
          StageExecutionService stageExecutionService,
          StageExecutionRepository stageExecutionRepository) {
    this.pipelineExecutionRepository = pipelineExecutionRepository;
    this.pipelineRepository = pipelineRepository;
    this.stageRepository = stageRepository;
    this.stageExecutionService = stageExecutionService;
    this.stageExecutionRepository = stageExecutionRepository;
  }

  /**
   * Starts execution of a pipeline, including its stages.
   *
   * @param pipelineName The name of the pipeline.
   * @param commitHash   The Git commit hash for this run.
   * @return The execution instance as DTO.
   */
  @Transactional
  public PipelineExecutionDTO startPipelineExecution(String pipelineName, String commitHash) {
    PipelineEntity pipeline = pipelineRepository.findByName(pipelineName)
            .orElseThrow(() -> new IllegalArgumentException("Pipeline not found: " + pipelineName));

    String runId = UUID.randomUUID().toString();
    PipelineExecutionEntity pipelineExecution = new PipelineExecutionEntity(
            pipeline, runId, commitHash, ExecutionStatus.RUNNING, Instant.now()
    );
    pipelineExecutionRepository.save(pipelineExecution);

    // Start execution of first stage
    List<StageEntity> stages = stageRepository.findByPipelineName(pipelineName);
    if (stages.isEmpty()) {
      throw new IllegalStateException("No stages defined for pipeline: " + pipelineName);
    }

    stageExecutionService.startStageExecution(pipelineExecution, stages.getFirst());

    return PipelineExecutionDTO.fromEntity(pipelineExecution);
  }

  // Fetch specific pipeline execution by pipeline name and run ID
  public PipelineExecutionEntity getPipelineExecution(String pipelineName, String runId) {
    return pipelineExecutionRepository.findByPipelineNameAndRunId(pipelineName, runId)
            .orElseThrow(() -> new IllegalArgumentException("Pipeline execution not found"));
  }

  // Fetch all pipeline executions for a specific pipeline
  public List<PipelineExecutionEntity> getPipelineExecutions(String pipelineName) {
    return pipelineExecutionRepository.findByPipelineName(pipelineName);
  }

  // Fetch the latest pipeline execution by pipeline name
  public PipelineExecutionEntity getLatestPipelineExecution(String pipelineName) {
    return pipelineExecutionRepository.findTopByPipelineNameOrderByStartTimeDesc(pipelineName)
            .orElseThrow(() -> new IllegalArgumentException("No executions found for pipeline"));
  }
}
