package edu.neu.cs6510.sp25.t1.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import edu.neu.cs6510.sp25.t1.backend.api.client.WorkerClient;
import edu.neu.cs6510.sp25.t1.backend.data.entity.JobEntity;
import edu.neu.cs6510.sp25.t1.backend.data.entity.JobExecutionEntity;
import edu.neu.cs6510.sp25.t1.backend.data.entity.StageExecutionEntity;
import edu.neu.cs6510.sp25.t1.backend.data.repository.JobExecutionRepository;
import edu.neu.cs6510.sp25.t1.backend.data.repository.StageExecutionRepository;
import edu.neu.cs6510.sp25.t1.backend.model.StageExecution;
import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;
import edu.neu.cs6510.sp25.t1.common.model.Job;
import edu.neu.cs6510.sp25.t1.common.model.Stage;

/**
 * Service for managing stage execution, scheduling jobs, and tracking stage progress.
 */
@Service
public class StageExecutionService {

  private final StageExecutionRepository stageExecutionRepository;
  private final JobExecutionRepository jobExecutionRepository;
  private final WorkerClient workerClient;

  public StageExecutionService(StageExecutionRepository stageExecutionRepository,
                               JobExecutionRepository jobExecutionRepository,
                               WorkerClient workerClient) {
    this.stageExecutionRepository = stageExecutionRepository;
    this.jobExecutionRepository = jobExecutionRepository;
    this.workerClient = workerClient;
  }

  /**
   * Starts execution of a stage by sending it to the worker.
   *
   * @param stageExecution The stage execution instance.
   */
  @Transactional
  public void executeStage(StageExecutionEntity stageExecution) {
    stageExecution.setStatus(ExecutionStatus.RUNNING);
    stageExecution.setStartTime(Instant.now());
    stageExecutionRepository.save(stageExecution);

    // Extract job executions from the stageExecution entity
    List<JobExecutionEntity> jobExecutionEntities = stageExecution.getJobExecutions();

    // Convert JobExecutionEntity -> Job model
    List<Job> jobs = jobExecutionEntities.stream()
            .map(jobExec -> new Job(
                    jobExec.getJob().getName(),
                    jobExec.getJob().getImage(),
                    jobExec.getJob().getScript(),
                    jobExec.getJob().getDependencies().stream()
                            .map(JobEntity::getName)
                            .collect(Collectors.toList()),
                    jobExec.getJob().isAllowFailure()
            ))
            .collect(Collectors.toList());

    // Convert StageEntity to Stage model
    Stage stageModel = new Stage(stageExecution.getStageName(), jobs);

    // Create StageExecution model
    StageExecution stageExecutionModel =
            new StageExecution(stageModel, new ArrayList<>());

    // Send stage execution to worker
    workerClient.sendStage(stageExecutionModel);
  }


  /**
   * Marks a stage as completed.
   *
   * @param stageExecution The completed stage execution.
   */
  @Transactional
  public void completeStage(StageExecutionEntity stageExecution) {
    stageExecution.setStatus(ExecutionStatus.SUCCESS);
    stageExecution.setCompletionTime(Instant.now());
    stageExecutionRepository.save(stageExecution);
  }

  /**
   * Checks if all jobs in a stage have completed.
   *
   * @param stageExecution The stage execution instance.
   */
  @Transactional
  public void checkStageCompletion(StageExecutionEntity stageExecution) {
    List<JobExecutionEntity> jobExecutions = jobExecutionRepository.findByStageExecution_PipelineExecution_RunId(
            stageExecution.getPipelineExecution().getRunId()
    );

    boolean allJobsCompleted = jobExecutions.stream()
            .allMatch(job -> job.getStatus() != ExecutionStatus.RUNNING && job.getStatus() != ExecutionStatus.PENDING);

    if (allJobsCompleted) {
      completeStage(stageExecution);
    }
  }
}
