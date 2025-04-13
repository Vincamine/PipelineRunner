package edu.neu.cs6510.sp25.t1.backend.service.execution;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import edu.neu.cs6510.sp25.t1.backend.database.entity.JobExecutionEntity;
import edu.neu.cs6510.sp25.t1.backend.database.entity.StageExecutionEntity;
import edu.neu.cs6510.sp25.t1.backend.database.repository.StageExecutionRepository;
import edu.neu.cs6510.sp25.t1.backend.service.event.JobCompletedEvent;
import edu.neu.cs6510.sp25.t1.backend.service.event.StageCompletedEvent;
import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;
import edu.neu.cs6510.sp25.t1.common.logging.PipelineLogger;
import lombok.RequiredArgsConstructor;

/**
 * Service responsible for managing stage executions.
 * This service has been refactored to:
 * 1. Work with a queue system for stage and job executions
 * 2. Remove worker dependencies
 * 3. Follow the "one function does one thing" principle
 */
@Service
@RequiredArgsConstructor
public class StageExecutionService {
  private final StageExecutionRepository stageExecutionRepository;
  private final JobExecutionService jobExecutionService;
  private final ApplicationEventPublisher eventPublisher;

  
  /**
   * Updates the status of a stage execution.
   *
   * @param stageExecutionId ID of the stage execution
   * @param status the new status
   */
  @Transactional
  public void updateStageStatus(UUID stageExecutionId, ExecutionStatus status) {
    StageExecutionEntity stageExecution = stageExecutionRepository.findById(stageExecutionId)
        .orElseThrow(() -> new IllegalArgumentException("Stage Execution not found"));
    
    // Set start time when stage starts running
    if (status == ExecutionStatus.RUNNING && stageExecution.getStartTime() == null) {
      stageExecution.setStartTime(Instant.now());
    }
    
    // Update the state which will set completionTime for terminal states
    stageExecution.updateState(status);
    
    stageExecutionRepository.saveAndFlush(stageExecution);
    PipelineLogger.info("Updated stage execution status to " + status + ": " + stageExecutionId);
  }

  /**
   * DFS helper method for circular dependency detection.
   *
   * @param dependencies Map of job ID to list of dependency job IDs
   * @param jobId Current job being checked
   * @param visited Set of jobs already visited
   * @param recursionStack Set of jobs in the current recursion path
   * @return true if a circular dependency is detected
   */
  private boolean hasCircularDependenciesDFS(Map<UUID, List<UUID>> dependencies, UUID jobId, 
                                           Set<UUID> visited, Set<UUID> recursionStack) {
    // If job is in recursion stack, we found a cycle
    if (recursionStack.contains(jobId)) {
      return true;
    }
    
    // If already visited and not in cycle, skip
    if (visited.contains(jobId)) {
      return false;
    }
    
    // Mark job as visited and add to recursion stack
    visited.add(jobId);
    recursionStack.add(jobId);
    
    // Check all dependencies
    List<UUID> jobDependencies = dependencies.getOrDefault(jobId, List.of());
    for (UUID dependencyId : jobDependencies) {
      if (hasCircularDependenciesDFS(dependencies, dependencyId, visited, recursionStack)) {
        return true;
      }
    }
    
    // Remove from recursion stack when done
    recursionStack.remove(jobId);
    return false;
  }

  /**
   * Event listener for job completion events.
   *
   * @param event job completion event
   */
  @EventListener
  @Transactional
  public void onJobCompleted(JobCompletedEvent event) {
    UUID stageExecutionId = event.getStageExecutionId();
    UUID jobExecutionId = event.getJobExecutionId();
    PipelineLogger.info("Job completed in stage: " + stageExecutionId + " | Checking if more jobs can be started...");

    // Queue any jobs that were waiting on this job
    List<UUID> newlyQueueableJobs = jobExecutionService.findJobsReadyForExecution(stageExecutionId, jobExecutionId);
    
    // Queue the newly executable jobs
    for (UUID jobId : newlyQueueableJobs) {
      PipelineLogger.info("Queueing job that was waiting on dependencies: " + jobId);
    }
    
    // Check if all jobs are complete
    checkStageCompletion(stageExecutionId);
  }
  
  /**
   * Check if all jobs in the stage are complete and finalize if necessary.
   *
   * @param stageExecutionId ID of the stage execution
   */
  private void checkStageCompletion(UUID stageExecutionId) {
    List<JobExecutionEntity> jobs = jobExecutionService.getJobsByStageExecution(stageExecutionId);
    
    // Check if any jobs are still running or pending
    boolean allJobsComplete = jobs.stream().allMatch(j -> 
        j.getStatus() == ExecutionStatus.SUCCESS || 
        j.getStatus() == ExecutionStatus.FAILED || 
        j.getStatus() == ExecutionStatus.CANCELED);
    
    if (!allJobsComplete) {
      PipelineLogger.info("Not all jobs are complete yet. Waiting for remaining jobs...");
      return;
    }
    
    // Check if any job failed or was canceled (and not allow_failure)
    boolean anyFailedOrCanceledNotAllowed = jobs.stream().anyMatch(j -> 
        (j.getStatus() == ExecutionStatus.FAILED || j.getStatus() == ExecutionStatus.CANCELED) 
        && !j.isAllowFailure());
    
    if (anyFailedOrCanceledNotAllowed) {
      PipelineLogger.error("A job in the stage failed or was canceled and failure is NOT allowed!");
      finalizeStageExecutionAsFailed(stageExecutionId);
    } else {
      PipelineLogger.info("All jobs in stage completed successfully or failures/cancellations were allowed. Finalizing stage.");
      finalizeStageExecution(stageExecutionId);
    }
  }

  /**
   * Finalizes a stage execution as failed.
   *
   * @param stageExecutionId current stage execution id
   */
  @Transactional
  public void finalizeStageExecutionAsFailed(UUID stageExecutionId) {
    PipelineLogger.error("Stage execution failed: " + stageExecutionId);
    updateStageStatus(stageExecutionId, ExecutionStatus.FAILED);
    
    // Cancel any remaining jobs
    cancelRemainingJobs(stageExecutionId);
    
    // Clean up in-memory data for this stage to prevent memory leaks
    jobExecutionService.cleanupStageData(stageExecutionId);
    
    // Publish stage completed event
    UUID pipelineExecutionId = findPipelineId(stageExecutionId);
    publishStageCompletedEvent(stageExecutionId, pipelineExecutionId);
  }

  /**
   * Cancel remaining jobs if not allowed to fail
   *
   * @param stageExecutionId current stage execution id
   */
  @Transactional
  public void cancelRemainingJobs(UUID stageExecutionId) {
    List<JobExecutionEntity> jobs = jobExecutionService.getJobsByStageExecution(stageExecutionId);

    for (JobExecutionEntity job : jobs) {
      if (job.getStatus() == ExecutionStatus.PENDING || job.getStatus() == ExecutionStatus.RUNNING) {
        jobExecutionService.cancelJobExecution(job.getId());
      }
    }

    PipelineLogger.info("All pending/running jobs in stage " + stageExecutionId + " are marked as CANCELED.");
  }

  /**
   * Finds the pipeline ID for a stage execution.
   *
   * @param stageExecutionId stage execution ID
   * @return pipeline ID
   */
  private UUID findPipelineId(UUID stageExecutionId) {
    return stageExecutionRepository.findById(stageExecutionId)
            .map(StageExecutionEntity::getPipelineExecutionId)
            .orElseThrow(() -> new IllegalArgumentException("Stage Execution not found"));
  }

  /**
   * Finalizes a stage execution.
   *
   * @param stageExecutionId ID of the stage execution
   */
  @Transactional
  public void finalizeStageExecution(UUID stageExecutionId) {
    PipelineLogger.info("Finalizing stage execution: " + stageExecutionId);
    updateStageStatus(stageExecutionId, ExecutionStatus.SUCCESS);
    
    // Clean up in-memory data for this stage to prevent memory leaks
    jobExecutionService.cleanupStageData(stageExecutionId);
    
    // Find the pipeline ID and publish a stage completed event
    UUID pipelineExecutionId = findPipelineId(stageExecutionId);
    publishStageCompletedEvent(stageExecutionId, pipelineExecutionId);
  }
  
  /**
   * Publish a stage completed event.
   *
   * @param stageExecutionId the stage execution ID
   * @param pipelineExecutionId the pipeline execution ID
   */
  private void publishStageCompletedEvent(UUID stageExecutionId, UUID pipelineExecutionId) {
    eventPublisher.publishEvent(new StageCompletedEvent(this, stageExecutionId, pipelineExecutionId));
    PipelineLogger.info("Published stage completed event for stage: " + stageExecutionId);
  }

}