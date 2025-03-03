package edu.neu.cs6510.sp25.t1.backend.service;

import edu.neu.cs6510.sp25.t1.backend.data.entity.*;
import edu.neu.cs6510.sp25.t1.backend.data.repository.*;
import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing job execution within a stage execution.
 */
@Service
public class JobExecutionService {

  private final JobExecutionRepository jobExecutionRepository;
  private final JobRepository jobRepository;
  private final StageExecutionRepository stageExecutionRepository;
  private final StageRepository stageRepository;
  private final PipelineExecutionRepository pipelineExecutionRepository;
  private final PipelineExecutionService pipelineExecutionService;

  public JobExecutionService(
          JobExecutionRepository jobExecutionRepository,
          JobRepository jobRepository,
          StageExecutionRepository stageExecutionRepository,
          StageRepository stageRepository,
          PipelineExecutionRepository pipelineExecutionRepository,
          PipelineExecutionService pipelineExecutionService) {
    this.jobExecutionRepository = jobExecutionRepository;
    this.jobRepository = jobRepository;
    this.stageExecutionRepository = stageExecutionRepository;
    this.stageRepository = stageRepository;
    this.pipelineExecutionRepository = pipelineExecutionRepository;
    this.pipelineExecutionService = pipelineExecutionService;
  }

  /**
   * Starts execution of all jobs for a given stage.
   *
   * @param stageExecution The stage execution instance.
   */
  @Transactional
  public void startJobsForStage(StageExecutionEntity stageExecution) {
    List<JobEntity> jobs = jobRepository.findByStageId(stageExecution.getId());
    if (jobs.isEmpty()) {
      throw new IllegalStateException("No jobs defined for stage: " + stageExecution.getStageName());
    }

    for (JobEntity job : jobs) {
      JobExecutionEntity jobExecution = new JobExecutionEntity(stageExecution, job, ExecutionStatus.RUNNING, Instant.now());
      jobExecutionRepository.save(jobExecution);
    }
  }

  /**
   * Marks a job as completed and updates the status.
   *
   * @param jobExecutionId The job execution ID.
   * @param status         The final job status.
   */
  @Transactional
  public void completeJobExecution(Long jobExecutionId, ExecutionStatus status) {
    JobExecutionEntity jobExecution = jobExecutionRepository.findById(jobExecutionId)
            .orElseThrow(() -> new IllegalArgumentException("Job Execution not found: " + jobExecutionId));

    jobExecution.setStatus(status);
    jobExecution.setCompletionTime(Instant.now());
    jobExecutionRepository.save(jobExecution);

    // Check if all jobs in the stage are completed
    checkStageCompletion(jobExecution.getStageExecution());
  }

  /**
   * Checks if all jobs in a stage execution are completed and updates the stage execution status.
   *
   * @param stageExecution The stage execution instance.
   */
  @Transactional
  public void checkStageCompletion(StageExecutionEntity stageExecution) {
    List<JobExecutionEntity> jobExecutions = jobExecutionRepository.findByStageExecutionId(stageExecution.getId());

    ExecutionStatus stageStatus = calculateOverallStatus(jobExecutions.stream()
            .map(JobExecutionEntity::getStatus)
            .collect(Collectors.toList()));

    stageExecution.setStatus(stageStatus);
    stageExecution.setCompletionTime(Instant.now());
    stageExecutionRepository.save(stageExecution);

    // If stage is completed, trigger next stage or complete pipeline
    PipelineExecutionEntity pipelineExecution = stageExecution.getPipelineExecution();
    List<StageEntity> stages = stageRepository.findByPipelineName(pipelineExecution.getPipeline().getName());

    int currentStageIndex = stages.indexOf(stageRepository.findByName(stageExecution.getStageName()));
    if (currentStageIndex < stages.size() - 1) {
      pipelineExecutionService.startNextStageExecution(pipelineExecution, stages.get(currentStageIndex + 1));
    } else {
      pipelineExecutionService.completePipelineExecution(pipelineExecution);
    }
  }

  /**
   * Determines the overall status of a stage based on job statuses.
   *
   * @param statuses List of job execution statuses.
   * @return The computed stage status.
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
