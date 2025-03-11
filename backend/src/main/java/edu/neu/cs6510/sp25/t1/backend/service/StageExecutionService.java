package edu.neu.cs6510.sp25.t1.backend.service;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import edu.neu.cs6510.sp25.t1.backend.database.entity.JobExecutionEntity;
import edu.neu.cs6510.sp25.t1.backend.database.entity.StageExecutionEntity;
import edu.neu.cs6510.sp25.t1.backend.database.repository.StageExecutionRepository;
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
  private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
  private final ApplicationContext applicationContext;

  /**
   * Executes a stage by starting all jobs in the stage and waiting for their completion.
   *
   * @param stageExecutionId ID of the stage execution
   */
  @Transactional
  public void executeStage(UUID stageExecutionId) {
    PipelineLogger.info("Starting execution for stage: " + stageExecutionId);

    StageExecutionEntity stageExecution = stageExecutionRepository.findById(stageExecutionId)
            .orElseThrow(() -> new IllegalArgumentException("Stage Execution not found"));

    stageExecution.updateState(ExecutionStatus.RUNNING);
    stageExecutionRepository.save(stageExecution);

    executeJobsByDependencies(stageExecutionId);

    waitForJobsCompletion(stageExecutionId);

    finalizeStageExecution(stageExecutionId);
  }

  /**
   * Executes jobs in a stage based on their dependencies.
   *
   * @param stageExecutionId ID of the stage execution
   */
  private void executeJobsByDependencies(UUID stageExecutionId) {
    List<JobExecutionEntity> jobs = jobExecutionService.getJobsByStageExecution(stageExecutionId);
    Map<UUID, List<UUID>> jobDependencies = new HashMap<>();

    for (JobExecutionEntity job : jobs) {
      List<UUID> dependencies = jobExecutionService.getJobDependencies(job.getId());
      jobDependencies.put(job.getId(), dependencies);
    }

    Set<UUID> executedJobs = new HashSet<>();
    while (executedJobs.size() < jobs.size()) {
      for (JobExecutionEntity job : jobs) {
        if (executedJobs.contains(job.getId())) continue;
        if (executedJobs.containsAll(jobDependencies.get(job.getId()))) {
          jobExecutionService.startJobExecution(job.getId());
          executedJobs.add(job.getId());
        }
      }
    }
  }

  /**
   * Waits for all jobs in a stage to complete without busy-waiting.
   *
   * @param stageExecutionId ID of the stage execution
   */
  private void waitForJobsCompletion(UUID stageExecutionId) {
    PipelineLogger.info("🕒 Waiting for jobs to complete in stage: " + stageExecutionId);

    scheduler.scheduleAtFixedRate(() -> {
      List<JobExecutionEntity> jobs = jobExecutionService.getJobsByStageExecution(stageExecutionId);
      boolean allSuccess = jobs.stream().allMatch(j -> j.getStatus() == ExecutionStatus.SUCCESS);

      if (allSuccess) {
        PipelineLogger.info("✅ All jobs in stage completed: " + stageExecutionId);
        getSelf().finalizeStageExecution(stageExecutionId); // ✅ Call transactional method correctly
        scheduler.shutdown(); // Stop checking after completion
      }
    }, 0, 2, TimeUnit.SECONDS);
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
    stageExecutionRepository.save(stageExecution);
    PipelineLogger.info("Stage execution completed: " + stageExecutionId);
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

  /**
   * Gets self-injected Spring bean to call transactional methods.
   *
   * @return StageExecutionService proxy managed by Spring
   */
  private StageExecutionService getSelf() {
    return applicationContext.getBean(StageExecutionService.class);
  }
}
