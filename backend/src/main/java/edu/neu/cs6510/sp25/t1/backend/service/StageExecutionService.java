package edu.neu.cs6510.sp25.t1.backend.service;

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
import edu.neu.cs6510.sp25.t1.backend.service.queue.JobExecutionQueueService;
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
  private final JobExecutionQueueService jobExecutionQueueService;
  private final ApplicationEventPublisher eventPublisher;

  /**
   * Processes a stage execution from the queue or when triggered directly.
   * This method is called by the queue service to process a stage execution
   * or when manually triggered via the API.
   *
   * @param stageExecutionId ID of the stage execution to process
   */
  @Transactional
  public void processStageExecution(UUID stageExecutionId) {
    PipelineLogger.info("Processing stage execution: " + stageExecutionId);
    
    try {
      // Update stage status to RUNNING
      updateStageStatus(stageExecutionId, ExecutionStatus.RUNNING);
      
      // Process jobs for this stage
      processStageJobs(stageExecutionId);
    } catch (Exception e) {
      PipelineLogger.error("Failed to process stage execution: " + e.getMessage());
      updateStageStatus(stageExecutionId, ExecutionStatus.FAILED);
    }
  }
  
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
    
    stageExecution.updateState(status);
    
    if (status == ExecutionStatus.SUCCESS || status == ExecutionStatus.FAILED || status == ExecutionStatus.CANCELED) {
      stageExecution.setEndTime(Instant.now());
    }
    
    stageExecutionRepository.saveAndFlush(stageExecution);
    PipelineLogger.info("Updated stage execution status to " + status + ": " + stageExecutionId);
  }

  /**
   * Process jobs for a stage based on dependencies.
   *
   * @param stageExecutionId ID of the stage execution
   */
  private void processStageJobs(UUID stageExecutionId) {
    // Get job executions for this stage
    List<JobExecutionEntity> jobs = jobExecutionService.getJobExecutionsForStage(stageExecutionId);

    if (jobs.isEmpty()) {
      PipelineLogger.warn("No jobs found for stage execution: " + stageExecutionId);
      // If no jobs, mark the stage as successful
      finalizeStageExecution(stageExecutionId);
      return;
    }

    PipelineLogger.info("Found " + jobs.size() + " jobs to execute for stage: " + stageExecutionId);

    // Execute jobs with dependency handling
    queueJobsByDependencies(stageExecutionId);
  }

  /**
   * Queue jobs for execution based on their dependencies.
   *
   * @param stageExecutionId ID of the stage execution
   */
  private void queueJobsByDependencies(UUID stageExecutionId) {
    List<JobExecutionEntity> jobs = jobExecutionService.getJobsByStageExecution(stageExecutionId);

    if (jobs.isEmpty()) {
      PipelineLogger.warn("No jobs to execute for stage: " + stageExecutionId);
      return;
    }

    Map<UUID, List<UUID>> jobDependencies = new HashMap<>();

    // Load dependencies for each job
    for (JobExecutionEntity job : jobs) {
      List<UUID> dependencies = jobExecutionService.getJobDependencies(job.getId());
      jobDependencies.put(job.getId(), dependencies);
      PipelineLogger.info("Job " + job.getId() + " has " + dependencies.size() + " dependencies");
    }

    // Find jobs with no dependencies and queue them
    Set<UUID> queuedJobs = new HashSet<>();
    for (JobExecutionEntity job : jobs) {
      List<UUID> dependencies = jobDependencies.getOrDefault(job.getId(), List.of());
      if (dependencies.isEmpty()) {
        PipelineLogger.info("Queueing job with no dependencies: " + job.getId());
        jobExecutionQueueService.enqueueJobExecution(job.getId());
        queuedJobs.add(job.getId());
      }
    }
    
    // Save the job dependencies map for the JobExecutionService to use
    jobExecutionService.saveJobDependenciesMap(stageExecutionId, jobDependencies, queuedJobs);
    
    // If no jobs were queued but we have jobs, there might be a circular dependency
    if (queuedJobs.isEmpty() && !jobs.isEmpty()) {
      PipelineLogger.error("No jobs could be queued for stage: " + stageExecutionId + ". Possible circular dependency.");
      finalizeStageExecutionAsFailed(stageExecutionId);
    }
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
      jobExecutionQueueService.enqueueJobExecution(jobId);
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
    
    // Check if any job failed (and not allow_failure)
    boolean anyFailedNotAllowed = jobs.stream().anyMatch(j -> 
        j.getStatus() == ExecutionStatus.FAILED && !j.isAllowFailure());
    
    if (anyFailedNotAllowed) {
      PipelineLogger.error("A job in the stage failed and failure is NOT allowed!");
      finalizeStageExecutionAsFailed(stageExecutionId);
    } else {
      PipelineLogger.info("All jobs in stage completed! Finalizing stage.");
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

  /**
   * Retrieves the status of a stage execution.
   *
   * @param pipelineExecutionId pipeline execution id
   * @param stageId             stage id
   * @return the status of the stage
   */
  public String getStageStatus(UUID pipelineExecutionId, UUID stageId) {
    return stageExecutionRepository.findByPipelineExecutionIdAndStageId(pipelineExecutionId, stageId)
            .map(stageExecution -> stageExecution.getStatus().toString())
            .orElse("Stage not found");
  }
}