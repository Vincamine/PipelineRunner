package edu.neu.cs6510.sp25.t1.backend.service;

import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.ResourceAccessException;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

import edu.neu.cs6510.sp25.t1.backend.database.entity.JobExecutionEntity;
import edu.neu.cs6510.sp25.t1.backend.database.repository.JobExecutionRepository;
import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;
import edu.neu.cs6510.sp25.t1.common.logging.PipelineLogger;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JobExecutionService {
  private final JobExecutionRepository jobExecutionRepository;
  private final RestTemplate restTemplate = new RestTemplate();
  private final ApplicationContext applicationContext;
  private static final String WORKER_URL = "http://localhost:5000/api/worker/execute";
  private static final int JOB_TIMEOUT_SECONDS = 30;

  private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

  /**
   * Start a job
   * @param jobExecutionId job execution ID
   */
  @Transactional
  public void startJobExecution(UUID jobExecutionId) {
    PipelineLogger.info("Sending job execution request to worker: " + jobExecutionId);

    JobExecutionEntity jobExecution = jobExecutionRepository.findById(jobExecutionId)
            .orElseThrow(() -> new IllegalArgumentException("Job Execution not found"));

    jobExecution.updateState(ExecutionStatus.RUNNING);
    jobExecution.setStartTime(Instant.now());
    jobExecutionRepository.save(jobExecution);

    Future<?> jobFuture = scheduler.submit(() -> {
      try {
        sendJobToWorker(jobExecutionId);
      } catch (Exception e) {
        jobExecution.updateState(ExecutionStatus.FAILED);
        jobExecutionRepository.save(jobExecution);
        PipelineLogger.error("Job execution failed: " + e.getMessage());
      }
    });

    // Use self-proxy to avoid Spring Proxy issues
    getSelf().scheduleTimeoutCheck(jobExecutionId, jobFuture);
  }

  /**
   * Send a job
   * @param jobExecutionId job execution ID
   */
  private void sendJobToWorker(UUID jobExecutionId) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    String requestPayload = "{\"jobExecutionId\": \"" + jobExecutionId + "\"}";

    HttpEntity<String> request = new HttpEntity<>(requestPayload, headers);
    ResponseEntity<String> response;

    try {
      response = restTemplate.postForEntity(WORKER_URL, request, String.class);
    } catch (ResourceAccessException e) {
      throw new RuntimeException("Worker is unreachable: " + e.getMessage());
    }

    if (!response.getStatusCode().is2xxSuccessful()) {
      throw new RuntimeException("Worker job execution failed: " + response.getBody());
    }

    PipelineLogger.info("✅ Job successfully sent to worker: " + jobExecutionId);
  }

  /**
   * Schedule a timeout check for a job execution.
   * @param jobExecutionId job execution ID
   * @param jobFuture  job future
   */
  @Transactional
  public void scheduleTimeoutCheck(UUID jobExecutionId, Future<?> jobFuture) {
    scheduler.schedule(() -> {
      JobExecutionEntity jobExecution = jobExecutionRepository.findById(jobExecutionId)
              .orElseThrow(() -> new IllegalArgumentException("Job Execution not found"));

      if (jobExecution.getStatus() == ExecutionStatus.RUNNING) {
        jobFuture.cancel(true); // ✅ Cancel if running too long
        jobExecution.updateState(ExecutionStatus.FAILED);
        jobExecutionRepository.save(jobExecution);
        PipelineLogger.error("⏳ Job execution timed out: " + jobExecutionId);
      }
    }, JOB_TIMEOUT_SECONDS, TimeUnit.SECONDS);
  }

  /**
   * Update job execution status.
   * @param jobExecutionId job execution ID
   * @param newStatus new status
   */
  @Transactional
  public void updateJobExecutionStatus(UUID jobExecutionId, ExecutionStatus newStatus) {
    JobExecutionEntity jobExecution = jobExecutionRepository.findById(jobExecutionId)
            .orElseThrow(() -> new IllegalArgumentException("Job Execution not found"));

    jobExecution.updateState(newStatus);
    jobExecutionRepository.save(jobExecution);
    PipelineLogger.info("🔄 Job status updated: " + jobExecutionId + " -> " + newStatus);
  }

  /**
   * Cancel a job execution.
   * @param jobExecutionId job execution ID
   */
  @Transactional
  public void cancelJobExecution(UUID jobExecutionId) {
    PipelineLogger.info("🚨 Attempting to cancel job execution: " + jobExecutionId);

    JobExecutionEntity jobExecution = jobExecutionRepository.findById(jobExecutionId)
            .orElseThrow(() -> new IllegalArgumentException("Job Execution not found"));

    if (jobExecution.getStatus() == ExecutionStatus.PENDING || jobExecution.getStatus() == ExecutionStatus.RUNNING) {
      jobExecution.updateState(ExecutionStatus.CANCELED);
      jobExecutionRepository.save(jobExecution);
      PipelineLogger.info("✅ Job execution canceled: " + jobExecutionId);
    } else {
      PipelineLogger.warn("⚠️ Cannot cancel job execution. Current status: " + jobExecution.getStatus());
    }
  }

  public List<JobExecutionEntity> getJobsByStageExecution(UUID stageExecutionId) {
    return jobExecutionRepository.findByStageExecutionId(stageExecutionId);
  }

  @Transactional(readOnly = true)
  public List<UUID> getJobDependencies(UUID jobId) {
    return jobExecutionRepository.findDependenciesByJobId(jobId);
  }

  /**
   * Get self-bean to avoid Spring Proxy issues.
   * @return JobExecutionService
   */
  private JobExecutionService getSelf() {
    return applicationContext.getBean(JobExecutionService.class);
  }

}
