package edu.neu.cs6510.sp25.t1.backend.service;

import edu.neu.cs6510.sp25.t1.backend.data.entity.*;
import edu.neu.cs6510.sp25.t1.backend.data.repository.*;
import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for managing pipeline execution, including stage execution.
 */
@Service
public class PipelineExecutionService {

  private final PipelineExecutionRepository pipelineExecutionRepository;
  private final PipelineRepository pipelineRepository;
  private final StageExecutionRepository stageExecutionRepository;
  private final StageRepository stageRepository;
  private final JobExecutionService jobExecutionService;

  public PipelineExecutionService(
          PipelineExecutionRepository pipelineExecutionRepository,
          PipelineRepository pipelineRepository,
          StageExecutionRepository stageExecutionRepository,
          StageRepository stageRepository,
          JobExecutionService jobExecutionService) {
    this.pipelineExecutionRepository = pipelineExecutionRepository;
    this.pipelineRepository = pipelineRepository;
    this.stageExecutionRepository = stageExecutionRepository;
    this.stageRepository = stageRepository;
    this.jobExecutionService = jobExecutionService;
  }

  /**
   * Starts execution of a pipeline, including its stages.
   *
   * @param pipelineName The name of the pipeline.
   * @param commitHash   The Git commit hash for this run.
   * @return The execution instance.
   */
  @Transactional
  public PipelineExecutionEntity startPipelineExecution(String pipelineName, String commitHash) {
    PipelineEntity pipeline = pipelineRepository.findByName(pipelineName);
    if (pipeline == null) {
      throw new IllegalArgumentException("Pipeline not found: " + pipelineName);
    }

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

    startNextStageExecution(pipelineExecution, stages.get(0));

    return pipelineExecution;
  }

  /**
   * Starts execution of a stage.
   *
   * @param pipelineExecution The pipeline execution instance.
   * @param stage             The stage to execute.
   */
  @Transactional
  public void startNextStageExecution(PipelineExecutionEntity pipelineExecution, StageEntity stage) {
    StageExecutionEntity stageExecution = new StageExecutionEntity(pipelineExecution, stage.getName());
    stageExecution.setStatus(ExecutionStatus.RUNNING);
    stageExecution.setStartTime(Instant.now());
    stageExecutionRepository.save(stageExecution);

    // Start execution of jobs in this stage
    jobExecutionService.startJobsForStage(stageExecution);
  }

  /**
   * Marks a pipeline execution as completed if all stages are done.
   *
   * @param pipelineExecution The execution instance.
   */
  @Transactional
  public void completePipelineExecution(PipelineExecutionEntity pipelineExecution) {
    List<StageExecutionEntity> stageExecutions = stageExecutionRepository.findByPipelineExecutionId(pipelineExecution.getId());

    ExecutionStatus pipelineStatus = calculateOverallStatus(stageExecutions.stream()
            .map(StageExecutionEntity::getStatus)
            .collect(Collectors.toList()));

    pipelineExecution.setStatus(pipelineStatus);
    pipelineExecution.setCompletionTime(Instant.now());
    pipelineExecutionRepository.save(pipelineExecution);
  }

  /**
   * Determines the overall status of a pipeline/stage.
   *
   * @param statuses List of statuses.
   * @return The computed status.
   */
  private ExecutionStatus calculateOverallStatus(List<ExecutionStatus> statuses) {
    if (statuses.contains(ExecutionStatus.FAILED)) {
      return ExecutionStatus.FAILED;
    }
    if (statuses.contains(ExecutionStatus.CANCELED)) {
      return ExecutionStatus.CANCELED;
    }
    return ExecutionStatus.SUCCESS;
  }
}
