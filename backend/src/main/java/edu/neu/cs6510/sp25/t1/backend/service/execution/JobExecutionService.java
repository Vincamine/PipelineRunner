package edu.neu.cs6510.sp25.t1.backend.service.execution;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.neu.cs6510.sp25.t1.backend.database.entity.JobEntity;
import edu.neu.cs6510.sp25.t1.backend.database.entity.JobExecutionEntity;
import edu.neu.cs6510.sp25.t1.backend.database.entity.StageExecutionEntity;
import edu.neu.cs6510.sp25.t1.backend.database.repository.JobExecutionRepository;
import edu.neu.cs6510.sp25.t1.backend.database.repository.JobRepository;
import edu.neu.cs6510.sp25.t1.backend.database.repository.StageExecutionRepository;
import edu.neu.cs6510.sp25.t1.backend.service.event.JobCompletedEvent;
import edu.neu.cs6510.sp25.t1.common.dto.JobDTO;
import edu.neu.cs6510.sp25.t1.common.dto.JobExecutionDTO;
import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;
import edu.neu.cs6510.sp25.t1.common.logging.PipelineLogger;
import lombok.RequiredArgsConstructor;

/**
 * Service responsible for managing job executions.
 * This service has been refactored to:
 * 1. Work with a queue system for job executions
 * 2. Remove worker dependencies
 * 3. Follow the "one function does one thing" principle
 */
@Service
@RequiredArgsConstructor
public class JobExecutionService {
  private final JobExecutionRepository jobExecutionRepository;
  private final JobRepository jobRepository;
  private final StageExecutionRepository stageExecutionRepository;
  private final ApplicationEventPublisher eventPublisher;
  
  // In-memory storage for job dependencies (stage execution ID -> job dependencies map)
  private final Map<UUID, Map<UUID, List<UUID>>> stageDependenciesMap = new ConcurrentHashMap<>();
  
  // In-memory storage for queued jobs (stage execution ID -> set of queued job IDs)
  private final Map<UUID, Set<UUID>> stageQueuedJobsMap = new ConcurrentHashMap<>();
  
  /**
   * Updates the status of a job execution.
   *
   * @param jobExecutionId ID of the job execution
   * @param status the new status
   */
  @Transactional
  public void updateJobStatus(UUID jobExecutionId, ExecutionStatus status) {
    JobExecutionEntity jobExecution = jobExecutionRepository.findById(jobExecutionId)
        .orElseThrow(() -> new IllegalArgumentException("Job Execution not found"));
    
    // Set start time when job starts running
    if (status == ExecutionStatus.RUNNING && jobExecution.getStartTime() == null) {
      jobExecution.setStartTime(Instant.now());
    }
    
    // Update the state which will set completionTime for terminal states
    jobExecution.updateState(status);
    
    jobExecutionRepository.saveAndFlush(jobExecution);
    PipelineLogger.info("Updated job execution status to " + status + ": " + jobExecutionId);
  }

  
  /**
   * Cleans up dependency maps for a stage after it's completed.
   * This prevents memory leaks by removing data structures that are no longer needed.
   *
   * @param stageExecutionId ID of the completed stage
   */
  @Transactional
  public void cleanupStageData(UUID stageExecutionId) {
    // Remove job dependencies for this stage
    stageDependenciesMap.remove(stageExecutionId);
    
    // Remove queued jobs set for this stage
    stageQueuedJobsMap.remove(stageExecutionId);
    
    PipelineLogger.info("Cleaned up in-memory data for completed stage: " + stageExecutionId);
  }

  /**
   * Cancels a job execution.
   *
   * @param jobExecutionId job execution ID
   */
  @Transactional
  public void cancelJobExecution(UUID jobExecutionId) {
    PipelineLogger.info("Cancelling job execution: " + jobExecutionId);

    JobExecutionEntity jobExecution = jobExecutionRepository.findById(jobExecutionId)
            .orElseThrow(() -> new IllegalArgumentException("Job Execution not found"));

    if (jobExecution.getStatus() == ExecutionStatus.PENDING || jobExecution.getStatus() == ExecutionStatus.RUNNING) {
      updateJobStatus(jobExecutionId, ExecutionStatus.CANCELED);
      PipelineLogger.info("Job execution canceled: " + jobExecutionId);
    } else {
      PipelineLogger.warn("Cannot cancel job execution. Current status: " + jobExecution.getStatus());
    }
  }

  /**
   * Saves job dependencies map for a stage.
   * This is used to track job dependencies and determine when jobs can be executed.
   *
   * @param stageExecutionId ID of the stage execution
   * @param jobDependencies map of job execution ID to list of job dependency IDs
   * @param queuedJobs set of job IDs that have been queued
   */
  @Transactional
  public void saveJobDependenciesMap(UUID stageExecutionId, Map<UUID, List<UUID>> jobDependencies, Set<UUID> queuedJobs) {
    stageDependenciesMap.put(stageExecutionId, new HashMap<>(jobDependencies));
    stageQueuedJobsMap.put(stageExecutionId, ConcurrentHashMap.newKeySet());
    stageQueuedJobsMap.get(stageExecutionId).addAll(queuedJobs);
    
    PipelineLogger.info("Saved job dependencies map for stage: " + stageExecutionId);
    PipelineLogger.info("Initial queued jobs: " + queuedJobs);
  }
  
  /**
   * Finds jobs that are ready for execution after a job has completed.
   * Uses synchronization to prevent race conditions when multiple jobs complete concurrently.
   *
   * @param stageExecutionId ID of the stage execution
   * @param completedJobId ID of the job that has completed
   * @return list of job IDs that are ready for execution
   */
  @Transactional
  public synchronized List<UUID> findJobsReadyForExecution(UUID stageExecutionId, UUID completedJobId) {
    // Get the dependencies map for this stage
    Map<UUID, List<UUID>> jobDependencies = stageDependenciesMap.get(stageExecutionId);
    if (jobDependencies == null) {
      PipelineLogger.warn("No dependencies map found for stage: " + stageExecutionId);
      return Collections.emptyList();
    }
    
    // Get the set of queued jobs for this stage, ensuring it exists
    Set<UUID> queuedJobs = stageQueuedJobsMap.computeIfAbsent(stageExecutionId, k -> ConcurrentHashMap.newKeySet());
    
    // Mark this job as queued (to handle case where a job completes before all dependent jobs are queued)
    queuedJobs.add(completedJobId);
    
    // Find jobs that depend on the completed job and check if all their dependencies are satisfied
    List<UUID> readyJobs = new ArrayList<>();
    
    // Create a defensive copy of the entry set to avoid concurrent modification
    for (Map.Entry<UUID, List<UUID>> entry : new HashMap<>(jobDependencies).entrySet()) {
      UUID jobId = entry.getKey();
      // Create a defensive copy of the dependencies list
      List<UUID> dependencies = new ArrayList<>(entry.getValue());
      
      // Skip if job has no dependencies or has already been queued
      if (dependencies.isEmpty() || queuedJobs.contains(jobId)) {
        continue;
      }
      
      // Check if this job depends on the completed job
      if (dependencies.contains(completedJobId)) {
        // Check if all dependencies are satisfied (queued)
        boolean allDependenciesSatisfied = true;
        for (UUID dependencyId : dependencies) {
          if (!queuedJobs.contains(dependencyId)) {
            allDependenciesSatisfied = false;
            break;
          }
        }
        
        if (allDependenciesSatisfied) {
          readyJobs.add(jobId);
          queuedJobs.add(jobId); // Mark as queued
          PipelineLogger.info("Job " + jobId + " is now ready for execution - all dependencies satisfied");
        }
      }
    }
    
    return readyJobs;
  }

  /**
   * Get jobs by stage execution ID.
   *
   * @param stageExecutionId stage execution ID
   * @return list of job executions
   */
  @Transactional(readOnly = true)
  public List<JobExecutionEntity> getJobsByStageExecution(UUID stageExecutionId) {
    StageExecutionEntity stageExecution = stageExecutionRepository.findById(stageExecutionId)
            .orElseThrow(() -> new IllegalArgumentException("Stage Execution not found"));

    return jobExecutionRepository.findByStageExecution(stageExecution);
  }

  /**
   * Get job dependencies.
   *
   * @param jobId job ID
   * @return list of job dependencies
   */
  @Transactional(readOnly = true)
  public List<UUID> getJobDependencies(UUID jobId) {
    return jobExecutionRepository.findDependenciesByJobId(jobId);
  }

}