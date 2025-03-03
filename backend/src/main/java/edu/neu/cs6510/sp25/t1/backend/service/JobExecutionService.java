package edu.neu.cs6510.sp25.t1.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.neu.cs6510.sp25.t1.backend.data.entity.JobExecutionEntity;
import edu.neu.cs6510.sp25.t1.backend.data.repository.JobExecutionRepository;
import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;

import java.time.Instant;
import java.util.Optional;

/**
 * Service for managing job executions within a CI/CD pipeline.
 */
@Service
public class JobExecutionService {
  private final JobExecutionRepository jobExecutionRepository;

  /**
   * Constructor for JobExecutionService.
   *
   * @param jobExecutionRepository The repository handling job executions.
   */
  public JobExecutionService(JobExecutionRepository jobExecutionRepository) {
    this.jobExecutionRepository = jobExecutionRepository;
  }

  /**
   * Starts execution of a job within a pipeline stage.
   *
   * @param jobName The name of the job to start.
   * @param pipelineName The name of the pipeline.
   * @param stageName The stage in which the job belongs.
   * @return The started job execution entity.
   */
  @Transactional
  public JobExecutionEntity startJobExecution(String jobName, String pipelineName, String stageName) {
    JobExecutionEntity jobExecution = new JobExecutionEntity(
            jobName,
            pipelineName,
            stageName,
            ExecutionStatus.RUNNING,
            Instant.now()
    );

    return jobExecutionRepository.save(jobExecution);
  }

  /**
   * Retrieves the execution status of a job.
   *
   * @param jobName The name of the job.
   * @return The execution status of the job.
   */
  public Optional<ExecutionStatus> getJobStatus(String jobName) {
    return jobExecutionRepository.findTopByJobNameOrderByStartTimeDesc(jobName)
            .map(JobExecutionEntity::getStatus);
  }

  /**
   * Marks a job as completed with a success or failure status.
   *
   * @param jobName The job name.
   * @param status The final status of the job.
   * @return True if the job status was updated successfully, false otherwise.
   */
  @Transactional
  public boolean completeJobExecution(String jobName, ExecutionStatus status) {
    Optional<JobExecutionEntity> jobExecutionOpt = jobExecutionRepository.findTopByJobNameOrderByStartTimeDesc(jobName);

    if (jobExecutionOpt.isPresent()) {
      JobExecutionEntity jobExecution = jobExecutionOpt.get();
      jobExecution.setStatus(status);
      jobExecution.setCompletionTime(Instant.now());
      jobExecutionRepository.save(jobExecution);
      return true;
    }

    return false;
  }

  /**
   * Stops an ongoing job execution.
   *
   * @param jobName The job to stop.
   * @return True if the job execution was stopped, false otherwise.
   */
  @Transactional
  public boolean stopJobExecution(String jobName) {
    Optional<JobExecutionEntity> jobExecutionOpt = jobExecutionRepository.findTopByJobNameOrderByStartTimeDesc(jobName);

    if (jobExecutionOpt.isPresent()) {
      JobExecutionEntity jobExecution = jobExecutionOpt.get();
      jobExecution.setStatus(ExecutionStatus.CANCELED);
      jobExecution.setCompletionTime(Instant.now());
      jobExecutionRepository.save(jobExecution);
      return true;
    }

    return false;
  }
}
