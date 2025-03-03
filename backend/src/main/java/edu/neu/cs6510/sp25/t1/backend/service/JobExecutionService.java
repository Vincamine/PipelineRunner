package edu.neu.cs6510.sp25.t1.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import edu.neu.cs6510.sp25.t1.backend.data.dto.JobExecutionDTO;
import edu.neu.cs6510.sp25.t1.backend.data.entity.JobEntity;
import edu.neu.cs6510.sp25.t1.backend.data.entity.JobExecutionEntity;
import edu.neu.cs6510.sp25.t1.backend.data.entity.StageExecutionEntity;
import edu.neu.cs6510.sp25.t1.backend.data.repository.JobExecutionRepository;
import edu.neu.cs6510.sp25.t1.backend.data.repository.JobRepository;
import edu.neu.cs6510.sp25.t1.backend.data.repository.StageExecutionRepository;
import edu.neu.cs6510.sp25.t1.backend.scheduler.JobScheduler;
import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;
import edu.neu.cs6510.sp25.t1.common.execution.JobExecution;

/**
 * Service for managing job execution within a stage execution.
 */
@Service
public class JobExecutionService {

  private final JobExecutionRepository jobExecutionRepository;
  private final JobRepository jobRepository;
  private final StageExecutionRepository stageExecutionRepository;
  private final JobScheduler jobScheduler;

  public JobExecutionService(
          JobExecutionRepository jobExecutionRepository,
          JobRepository jobRepository,
          StageExecutionRepository stageExecutionRepository,
          JobScheduler jobScheduler) {
    this.jobExecutionRepository = jobExecutionRepository;
    this.jobRepository = jobRepository;
    this.stageExecutionRepository = stageExecutionRepository;
    this.jobScheduler = jobScheduler;
  }

  /**
   * Starts execution of all jobs for a given stage, respecting dependencies.
   *
   * @param stageExecution The stage execution instance.
   */
  @Transactional
  public void startJobsForStage(StageExecutionEntity stageExecution) {
    List<JobEntity> jobs = jobRepository.findByStageId(stageExecution.getStage().getId());
    if (jobs.isEmpty()) {
      throw new IllegalStateException("No jobs defined for stage: " + stageExecution.getStageName());
    }

    for (JobEntity job : jobs) {
      // Check if job can start immediately (no dependencies or all dependencies satisfied)
      if (canJobStart(job, stageExecution)) {
        // Create job execution in RUNNING state
        JobExecutionEntity jobExecution = new JobExecutionEntity(
                stageExecution,
                job,
                ExecutionStatus.RUNNING,
                Instant.now()
        );
        jobExecutionRepository.save(jobExecution);
        // Schedule job for execution
        scheduleJob(jobExecution);
      } else {
        // Create job execution in PENDING state for jobs with unmet dependencies
        JobExecutionEntity jobExecution = new JobExecutionEntity(
                stageExecution,
                job,
                ExecutionStatus.PENDING,
                Instant.now()
        );
        jobExecutionRepository.save(jobExecution);
      }
    }
  }

  /**
   * Schedules a job for execution using the job scheduler.
   *
   * @param jobExecution The job execution to schedule.
   */
  private void scheduleJob(JobExecutionEntity jobExecution) {
    JobExecution job = new JobExecution(
            jobExecution.getJob().getName(),
            jobExecution.getJob().getImage(),
            jobExecution.getJob().getScript(),
            jobExecution.getJob().getDependencies() != null ?
                    jobExecution.getJob().getDependencies().stream()
                            .map(JobEntity::getName)
                            .collect(Collectors.toList()) :
                    Collections.emptyList(),
            jobExecution.getJob().isAllowFailure()
    );
    jobScheduler.addJob(job);
  }

  /**
   * Checks if a job can start based on its dependencies.
   *
   * @param job            The job to check.
   * @param stageExecution The stage execution context.
   * @return True if the job can start, false otherwise.
   */
  private boolean canJobStart(JobEntity job, StageExecutionEntity stageExecution) {
    List<JobEntity> dependencies = job.getDependencies();

    if (dependencies == null || dependencies.isEmpty()) {
      return true;
    }

    String runId = stageExecution.getPipelineExecution().getRunId();
    for (JobEntity dependsOnJob : dependencies) {
      Optional<JobExecutionEntity> dependencyExec =
              jobExecutionRepository.findByJob_NameAndStageExecution_PipelineExecution_RunId(
                      dependsOnJob.getName(),
                      runId
              );

      if (dependencyExec.isEmpty() || dependencyExec.get().getStatus() != ExecutionStatus.SUCCESS) {
        return false;
      }
    }

    return true;
  }

  /**
   * Marks a job as completed and updates the status.
   * Also triggers dependent jobs if this job completes successfully.
   *
   * @param jobExecutionId The job execution ID.
   * @param status         The final job status.
   * @return The updated JobExecutionDTO.
   */
  @Transactional
  public JobExecutionDTO completeJobExecution(Long jobExecutionId, ExecutionStatus status) {
    JobExecutionEntity jobExecution = jobExecutionRepository.findById(jobExecutionId)
            .orElseThrow(() -> new IllegalArgumentException("Job Execution not found: " + jobExecutionId));

    jobExecution.setStatus(status);
    jobExecution.setCompletionTime(Instant.now());
    jobExecutionRepository.save(jobExecution);

    if (status == ExecutionStatus.SUCCESS) {
      startDependentJobs(jobExecution);
    }

    return JobExecutionDTO.fromEntity(jobExecution);
  }

  private void startDependentJobs(JobExecutionEntity completedJobExecution) {
    // Logic to start dependent jobs
  }

  @Transactional
  public void checkStageCompletion(StageExecutionEntity stageExecution) {
    String runId = stageExecution.getPipelineExecution().getRunId();
    List<JobExecutionEntity> jobExecutions = jobExecutionRepository.findByStageExecution_PipelineExecution_RunId(runId).stream()
            .filter(je -> je.getStageExecution().getId().equals(stageExecution.getId()))
            .collect(Collectors.toList());

    boolean allJobsCompleted = jobExecutions.stream()
            .noneMatch(je -> je.getStatus() == ExecutionStatus.RUNNING || je.getStatus() == ExecutionStatus.PENDING);

    if (!allJobsCompleted) return;

    ExecutionStatus stageStatus = calculateOverallStatus(jobExecutions.stream()
            .map(JobExecutionEntity::getStatus).collect(Collectors.toList()));

    stageExecution.setStatus(stageStatus);
    stageExecution.setCompletionTime(Instant.now());
    stageExecutionRepository.save(stageExecution);
  }

  private ExecutionStatus calculateOverallStatus(List<ExecutionStatus> statuses) {
    if (statuses.contains(ExecutionStatus.FAILED)) {
      return ExecutionStatus.FAILED;
    }
    if (statuses.contains(ExecutionStatus.CANCELED)) {
      return ExecutionStatus.CANCELED;
    }
    return ExecutionStatus.SUCCESS;
  }


  /**
   * Gets job execution by ID and returns as DTO.
   *
   * @param jobExecutionId The job execution ID.
   * @return The job execution DTO.
   */
  public JobExecutionDTO getJobExecution(Long jobExecutionId) {
    JobExecutionEntity entity = jobExecutionRepository.findById(jobExecutionId)
            .orElseThrow(() -> new IllegalArgumentException("Job Execution not found: " + jobExecutionId));
    return JobExecutionDTO.fromEntity(entity);
  }

  /**
   * Fetches logs for a specific job execution.
   *
   * @param jobExecutionId The job execution ID.
   * @return The logs for the job execution.
   */
  public Optional<String> getJobLogs(Long jobExecutionId) {
    JobExecutionEntity jobExecution = jobExecutionRepository.findById(jobExecutionId)
            .orElseThrow(() -> new IllegalArgumentException("Job Execution not found: " + jobExecutionId));

    String logs = fetchLogsForJobExecution(jobExecution);

    return Optional.ofNullable(logs);  // Return the logs if available, else Optional.empty()
  }

  /**
   * Placeholder method to fetch logs for a job execution.
   *
   * @param jobExecution The job execution instance.
   * @return The logs for the job execution.
   */
  private String fetchLogsForJobExecution(JobExecutionEntity jobExecution) {
    return "Logs for Job Execution " + jobExecution.getId();  // Placeholder
  }

}
