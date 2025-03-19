package edu.neu.cs6510.sp25.t1.backend.service;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
 */
@Service
@RequiredArgsConstructor
public class StageExecutionService {
  private final StageExecutionRepository stageExecutionRepository;
  private final JobExecutionService jobExecutionService;
  private final ApplicationEventPublisher eventPublisher;

  /**
   * Executes a stage by starting all jobs in the stage.
   *
   * @param stageExecutionId ID of the stage execution
   */
  @Transactional
  public void executeStage(UUID stageExecutionId) {
    PipelineLogger.info("Starting execution for stage: " + stageExecutionId);

    StageExecutionEntity stageExecution = stageExecutionRepository.findById(stageExecutionId)
            .orElseThrow(() -> new IllegalArgumentException("Stage Execution not found"));

    // Set stage status to RUNNING
    stageExecution.updateState(ExecutionStatus.RUNNING);
    stageExecutionRepository.saveAndFlush(stageExecution); // Save and flush in one operation

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
    executeJobsByDependencies(stageExecutionId);
  }

  /**
   * Executes jobs in a stage based on their dependencies.
   *
   * @param stageExecutionId ID of the stage execution
   */
  private void executeJobsByDependencies(UUID stageExecutionId) {
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

    Set<UUID> executedJobs = new HashSet<>();
    boolean jobsRemaining;

    do {
      jobsRemaining = false;

      for (JobExecutionEntity job : jobs) {
        // Skip already executed jobs
        if (executedJobs.contains(job.getId())) continue;

        List<UUID> dependencies = jobDependencies.getOrDefault(job.getId(), List.of());

        // Check if all dependencies are satisfied
        if (executedJobs.containsAll(dependencies)) {
          PipelineLogger.info("Starting job execution: " + job.getId() + " (all dependencies satisfied)");
          try {
            jobExecutionService.startJobExecution(job.getId());
            executedJobs.add(job.getId());
            jobsRemaining = true;
          } catch (Exception e) {
            PipelineLogger.error("Failed to start job: " + job.getId() + " - " + e.getMessage());
          }
        }
      }
    } while (jobsRemaining);

    PipelineLogger.info("Completed job dependency resolution for stage: " + stageExecutionId);
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
    PipelineLogger.info("Job completed in stage: " + stageExecutionId + " | Checking if stage is done...");

    List<JobExecutionEntity> jobs = jobExecutionService.getJobsByStageExecution(stageExecutionId);

    // Check if any job failed but is allowed to fail
    boolean anyFailed = jobs.stream().anyMatch(j -> j.getStatus() == ExecutionStatus.FAILED);
    boolean allowFailures = jobs.stream().allMatch(JobExecutionEntity::isAllowFailure);

    if (anyFailed && !allowFailures) {
      PipelineLogger.error("A job in the stage failed and failure is NOT allowed! Cancelling remaining jobs...");
      cancelRemainingJobs(stageExecutionId);
      finalizeStageExecutionAsFailed(stageExecutionId);
      return; // Stop further processing
    }

    // If all jobs in the stage are successful (or allowed failures), mark the stage as completed
    boolean allSuccessOrAllowedFailures = jobs.stream()
            .allMatch(j -> j.getStatus() == ExecutionStatus.SUCCESS ||
                    (j.getStatus() == ExecutionStatus.FAILED && j.isAllowFailure()));

    if (allSuccessOrAllowedFailures) {
      PipelineLogger.info("All jobs in stage completed! Finalizing stage.");
      finalizeStageExecution(stageExecutionId);

      // Find the pipeline ID and publish a stage completed event
      UUID pipelineId = findPipelineId(stageExecutionId);
      eventPublisher.publishEvent(new StageCompletedEvent(this, stageExecutionId, pipelineId));
    } else {
      PipelineLogger.info("Waiting for remaining jobs in stage...");
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

    StageExecutionEntity stageExecution = stageExecutionRepository.findById(stageExecutionId)
            .orElseThrow(() -> new IllegalArgumentException("Stage Execution not found"));

    stageExecution.updateState(ExecutionStatus.FAILED);
    stageExecutionRepository.saveAndFlush(stageExecution); // Save and flush in one operation
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

    StageExecutionEntity stageExecution = stageExecutionRepository.findById(stageExecutionId)
            .orElseThrow(() -> new IllegalArgumentException("Stage Execution not found"));

    stageExecution.updateState(ExecutionStatus.SUCCESS);
    stageExecution = stageExecutionRepository.saveAndFlush(stageExecution);
    PipelineLogger.info("Stage execution completed: " + stageExecution.getId());
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