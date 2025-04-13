package edu.neu.cs6510.sp25.t1.backend.service.pipeline;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.neu.cs6510.sp25.t1.backend.database.entity.JobExecutionEntity;
import edu.neu.cs6510.sp25.t1.backend.database.entity.PipelineExecutionEntity;
import edu.neu.cs6510.sp25.t1.backend.database.entity.StageExecutionEntity;
import edu.neu.cs6510.sp25.t1.backend.database.repository.JobExecutionRepository;
import edu.neu.cs6510.sp25.t1.backend.database.repository.PipelineExecutionRepository;
import edu.neu.cs6510.sp25.t1.backend.database.repository.StageExecutionRepository;
import edu.neu.cs6510.sp25.t1.backend.service.event.StageCompletedEvent;
import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;
import edu.neu.cs6510.sp25.t1.common.logging.PipelineLogger;
import lombok.RequiredArgsConstructor;

/**
 * Service responsible for managing pipeline execution statuses.
 * This includes updating statuses, handling status changes, and finalizing executions.
 */
@Service
@RequiredArgsConstructor
public class PipelineStatusService {
  private final PipelineExecutionRepository pipelineExecutionRepository;
  private final StageExecutionRepository stageExecutionRepository;
  private final JobExecutionRepository jobExecutionRepository;
  
  /**
   * Updates the status of a pipeline execution.
   *
   * @param pipelineExecutionId ID of the pipeline execution
   * @param status the new status
   */
  @Transactional
  public void updatePipelineStatus(UUID pipelineExecutionId, ExecutionStatus status) {
    PipelineExecutionEntity pipelineExecution = pipelineExecutionRepository.findById(pipelineExecutionId)
        .orElseThrow(() -> new IllegalArgumentException("Pipeline Execution not found"));
    
    // Set start time when pipeline starts running
    if (status == ExecutionStatus.RUNNING && pipelineExecution.getStartTime() == null) {
      pipelineExecution.setStartTime(Instant.now());
    }
    
    // Update state which will set completionTime for terminal states
    pipelineExecution.updateState(status);
    
    pipelineExecutionRepository.saveAndFlush(pipelineExecution);
    PipelineLogger.info("Updated pipeline execution status to " + status + ": " + pipelineExecutionId);
  }
  
  /**
   * Event listener for stage completion.
   *
   * @param event stage completion event
   */
  @EventListener
  @Transactional
  public void onStageCompleted(StageCompletedEvent event) {
    UUID pipelineExecutionId = event.getPipelineExecutionId();
    PipelineLogger.info("Stage completed in pipeline: " + pipelineExecutionId + " | Checking if pipeline is done...");

    checkAndFinalizePipeline(pipelineExecutionId);
  }
  
  /**
   * Check if all stages are complete and finalize the pipeline if necessary.
   *
   * @param pipelineExecutionId the pipeline execution ID
   */
  @Transactional
  public void checkAndFinalizePipeline(UUID pipelineExecutionId) {
    List<StageExecutionEntity> stages = stageExecutionRepository.findByPipelineExecutionId(pipelineExecutionId);
    
    // Check if all stages are complete
    boolean allComplete = stages.stream().allMatch(s -> 
        s.getStatus() == ExecutionStatus.SUCCESS || 
        s.getStatus() == ExecutionStatus.FAILED || 
        s.getStatus() == ExecutionStatus.CANCELED);
    
    if (!allComplete) {
      PipelineLogger.info("Not all stages are complete yet. Waiting for remaining stages...");
      return;
    }
    
    // Check if any stage failed and was not allowed to fail
    boolean anyFailedNotAllowed = stages.stream().anyMatch(s -> 
        s.getStatus() == ExecutionStatus.FAILED && 
        !stageAllowsFailure(s.getId()));
    
    if (anyFailedNotAllowed) {
      PipelineLogger.error("At least one stage failed and failure is not allowed. Marking pipeline as FAILED.");
      updatePipelineStatus(pipelineExecutionId, ExecutionStatus.FAILED);
    } else {
      PipelineLogger.info("All stages completed successfully or failures were allowed. Marking pipeline as SUCCESS.");
      updatePipelineStatus(pipelineExecutionId, ExecutionStatus.SUCCESS);
    }
  }
  
  /**
   * Check if a stage allows failure by checking if all jobs in the stage allow failure.
   *
   * @param stageExecutionId the stage execution ID
   * @return true if all jobs in the stage allow failure
   */
  private boolean stageAllowsFailure(UUID stageExecutionId) {
    StageExecutionEntity stageExecution = stageExecutionRepository.findById(stageExecutionId)
        .orElseThrow(() -> new IllegalArgumentException("Stage Execution not found"));
    
    List<JobExecutionEntity> jobs = jobExecutionRepository.findByStageExecution(stageExecution);
    
    // If there are no jobs, then we'll say the stage allows failure
    if (jobs.isEmpty()) {
      return true;
    }
    
    // Check if all jobs allow failure
    return jobs.stream().allMatch(JobExecutionEntity::isAllowFailure);
  }
  
//  /**
//   * Finalizes a pipeline execution.
//   *
//   * @param pipelineExecutionId ID of the pipeline execution
//   */
//  @Transactional
//  public void finalizePipelineExecution(UUID pipelineExecutionId) {
//    PipelineLogger.info("Finalizing pipeline execution: " + pipelineExecutionId);
//    updatePipelineStatus(pipelineExecutionId, ExecutionStatus.SUCCESS);
//  }
}