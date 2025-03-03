package edu.neu.cs6510.sp25.t1.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import edu.neu.cs6510.sp25.t1.backend.data.dto.PipelineExecutionDTO;
import edu.neu.cs6510.sp25.t1.backend.data.dto.StageExecutionDTO;
import edu.neu.cs6510.sp25.t1.backend.data.entity.PipelineExecutionEntity;
import edu.neu.cs6510.sp25.t1.backend.data.entity.StageEntity;
import edu.neu.cs6510.sp25.t1.backend.data.entity.StageExecutionEntity;
import edu.neu.cs6510.sp25.t1.backend.data.repository.PipelineExecutionRepository;
import edu.neu.cs6510.sp25.t1.backend.data.repository.StageExecutionRepository;
import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;

/**
 * Service for managing stage execution, including job execution.
 */
@Service
public class StageExecutionService {

  private final StageExecutionRepository stageExecutionRepository;
  private final JobExecutionService jobExecutionService;
  private final PipelineExecutionRepository pipelineExecutionRepository ;


  public StageExecutionService(
          StageExecutionRepository stageExecutionRepository,
          JobExecutionService jobExecutionService,
          PipelineExecutionRepository pipelineExecutionRepository) {
    this.stageExecutionRepository = stageExecutionRepository;
    this.jobExecutionService = jobExecutionService;
    this.pipelineExecutionRepository = pipelineExecutionRepository;
  }

  /**
   * Starts execution of a stage, triggering job execution and handling stage status.
   *
   * @param pipelineExecution The pipeline execution.
   * @param stage             The stage to execute.
   */
  @Transactional
  public void startStageExecution(PipelineExecutionEntity pipelineExecution, StageEntity stage) {
    StageExecutionEntity stageExecution = new StageExecutionEntity(pipelineExecution, stage.getName());
    stageExecution.setStatus(ExecutionStatus.RUNNING);
    stageExecution.setStartTime(Instant.now());
    stageExecutionRepository.save(stageExecution);

    // Start executing jobs within the stage
    jobExecutionService.startJobsForStage(stageExecution);

    // Handle stage completion after jobs are done
    handleStageCompletion(stageExecution);
  }

  /**
   * Handles the completion of a stage, either triggering the next stage or completing the pipeline.
   *
   * @param stageExecution The stage execution.
   */
  @Transactional
  public void handleStageCompletion(StageExecutionEntity stageExecution) {
    // Check if all jobs are completed in the stage
    jobExecutionService.checkStageCompletion(stageExecution);

    // Update the stage status
    ExecutionStatus stageStatus = calculateStageStatus(stageExecution);
    stageExecution.setStatus(stageStatus);
    stageExecution.setCompletionTime(Instant.now());
    stageExecutionRepository.save(stageExecution);

    // Proceed to next stage or complete the pipeline
    if (isLastStage(stageExecution)) {
      completePipelineExecution(stageExecution.getPipelineExecution());
    } else {
      proceedToNextStage(stageExecution);
    }
  }

  /**
   * Checks if the current stage is the last in the pipeline.
   *
   * @param stageExecution The stage execution to check.
   * @return true if the stage is the last one, false otherwise.
   */
  private boolean isLastStage(StageExecutionEntity stageExecution) {
    // Find the next stage in the pipeline
    StageEntity nextStage = findNextStage(stageExecution);

    // If there is no next stage, it is the last stage
    return nextStage == null;
  }

  /**
   * Proceeds to the next stage in the pipeline.
   *
   * @param stageExecution The current stage execution.
   */
  private void proceedToNextStage(StageExecutionEntity stageExecution) {
    StageEntity nextStage = findNextStage(stageExecution);
    if (nextStage != null) {
      startNextStageExecution(stageExecution.getPipelineExecution(), nextStage);
    }
  }

  /**
   * Finds the next stage in the pipeline after the current one.
   *
   * @param stageExecution The current stage execution.
   * @return The next stage in the pipeline, or null if there is no next stage.
   */
  private StageEntity findNextStage(StageExecutionEntity stageExecution) {
    // Logic to find the next stage based on the current stage execution
    List<StageEntity> stages = stageExecution.getPipelineExecution().getPipeline().getStages();
    int currentIndex = stages.indexOf(stageExecution.getStage());

    // If it's the last stage, return null, otherwise return the next stage
    if (currentIndex < stages.size() - 1) {
      return stages.get(currentIndex + 1);
    }
    return null;  // No next stage
  }

  /**
   * Calculates the status of the stage based on the job statuses within it.
   *
   * @param stageExecution The stage execution entity.
   * @return The calculated status.
   */
  private ExecutionStatus calculateStageStatus(StageExecutionEntity stageExecution) {
    // Use the job execution status to calculate the overall stage status
    boolean hasFailedJob = stageExecution.getJobExecutions().stream()
            .anyMatch(jobExecution -> jobExecution.getStatus() == ExecutionStatus.FAILED);

    boolean isCancelled = stageExecution.getJobExecutions().stream()
            .anyMatch(jobExecution -> jobExecution.getStatus() == ExecutionStatus.CANCELED);

    if (hasFailedJob) {
      return ExecutionStatus.FAILED;
    } else if (isCancelled) {
      return ExecutionStatus.CANCELED;
    }

    // If no jobs failed or were canceled, the stage is successful
    return ExecutionStatus.SUCCESS;
  }

  @Transactional
  public void startNextStageExecution(PipelineExecutionEntity pipelineExecution, StageEntity nextStage) {
    // Create a new StageExecutionEntity for the next stage
    StageExecutionEntity nextStageExecution = new StageExecutionEntity(pipelineExecution, nextStage.getName());
    nextStageExecution.setStatus(ExecutionStatus.RUNNING);
    nextStageExecution.setStartTime(Instant.now());
    stageExecutionRepository.save(nextStageExecution);

    // Now, pass it on to the StageExecutionService to handle job executions
    startStageExecution(pipelineExecution, nextStage);
  }

  /**
   * Marks a pipeline execution as completed if all stages are done.
   *
   * @param pipelineExecution The execution instance.
   * @return The updated pipeline execution DTO.
   */
  @Transactional
  public PipelineExecutionDTO completePipelineExecution(PipelineExecutionEntity pipelineExecution) {
    // Fetch the stage executions using the repository (not directly from the entity)
    List<StageExecutionDTO> stageExecutions = stageExecutionRepository.findByPipelineExecutionId(pipelineExecution.getId()).stream()
            .map(StageExecutionDTO::fromEntity)  // Convert to DTO
            .toList();

    ExecutionStatus pipelineStatus = calculateOverallStatus(stageExecutions.stream()
            .map(StageExecutionDTO::getStatus)  // Get status from StageExecutionDTO
            .collect(Collectors.toList()));

    pipelineExecution.setStatus(pipelineStatus);
    pipelineExecution.setCompletionTime(Instant.now());
    pipelineExecutionRepository.save(pipelineExecution);

    return PipelineExecutionDTO.fromEntity(pipelineExecution);
  }

  /**
   * Determines the overall status of a pipeline based on stage statuses.
   *
   * @param statuses List of stage statuses.
   * @return The computed pipeline status.
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
