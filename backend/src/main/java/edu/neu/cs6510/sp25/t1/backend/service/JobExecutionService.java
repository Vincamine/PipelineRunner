package edu.neu.cs6510.sp25.t1.backend.service;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import edu.neu.cs6510.sp25.t1.backend.database.entity.JobExecutionEntity;
import edu.neu.cs6510.sp25.t1.backend.database.repository.JobExecutionRepository;
import edu.neu.cs6510.sp25.t1.backend.service.event.JobCompletedEvent;
import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;
import edu.neu.cs6510.sp25.t1.common.logging.PipelineLogger;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JobExecutionService {
  private final JobExecutionRepository jobExecutionRepository;
  private final ApplicationEventPublisher eventPublisher;
  private final RestTemplate restTemplate = new RestTemplate();
  private static final String WORKER_URL = "http://localhost:5000/api/worker/execute";

  /**
   * Start a job
   *
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

    try {
      sendJobToWorker(jobExecutionId);
    } catch (Exception e) {
      jobExecution.updateState(ExecutionStatus.FAILED);
      jobExecutionRepository.save(jobExecution);
      PipelineLogger.error("Job execution failed: " + e.getMessage());
    }
  }

  /**
   * Send a job
   *
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

    PipelineLogger.info("Job successfully sent to worker: " + jobExecutionId);
  }

  /**
   * Update job execution status.
   *
   * @param jobExecutionId job execution ID
   * @param newStatus      new status
   */
  @Transactional
  public void updateJobExecutionStatus(UUID jobExecutionId, ExecutionStatus newStatus) {
    JobExecutionEntity jobExecution = jobExecutionRepository.findById(jobExecutionId)
            .orElseThrow(() -> new IllegalArgumentException("Job Execution not found"));

    jobExecution.updateState(newStatus);
    jobExecutionRepository.save(jobExecution);

    if (newStatus == ExecutionStatus.SUCCESS) {
      eventPublisher.publishEvent(new JobCompletedEvent(this, jobExecutionId, jobExecution.getStageExecutionId()));
    }
  }

  /**
   * Cancel a job execution.
   *
   * @param jobExecutionId job execution ID
   */
  @Transactional
  public void cancelJobExecution(UUID jobExecutionId) {
    PipelineLogger.info("Attempting to cancel job execution: " + jobExecutionId);

    JobExecutionEntity jobExecution = jobExecutionRepository.findById(jobExecutionId)
            .orElseThrow(() -> new IllegalArgumentException("Job Execution not found"));

    if (jobExecution.getStatus() == ExecutionStatus.PENDING || jobExecution.getStatus() == ExecutionStatus.RUNNING) {
      jobExecution.updateState(ExecutionStatus.CANCELED);
      jobExecutionRepository.save(jobExecution);
      PipelineLogger.info("Job execution canceled in backend: " + jobExecutionId);

      // Send cancel request to worker
      sendCancelRequestToWorker(jobExecutionId);
    } else {
      PipelineLogger.warn("Cannot cancel job execution. Current status: " + jobExecution.getStatus());
    }
  }

  /**
   * Send a cancel request to worker.
   *
   * @param jobExecutionId job execution id
   */
  private void sendCancelRequestToWorker(UUID jobExecutionId) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    String requestPayload = "{\"jobExecutionId\": \"" + jobExecutionId + "\", \"action\": \"CANCEL\"}";
    HttpEntity<String> request = new HttpEntity<>(requestPayload, headers);
    ResponseEntity<String> response;

    try {
      response = restTemplate.postForEntity(WORKER_URL + "/cancel", request, String.class);
      if (response.getStatusCode().is2xxSuccessful()) {
        PipelineLogger.info("Cancel request confirmed by worker for job: " + jobExecutionId);
      } else {
        PipelineLogger.error("Worker failed to cancel job. Keeping status unchanged.");
      }
    } catch (Exception e) {
      PipelineLogger.error("Error sending cancel request to worker: " + e.getMessage());
    }
  }


  /**
   * Get jobs by stage execution id
   *
   * @param stageExecutionId stage execution ID
   * @return list of job executions
   */
  public List<JobExecutionEntity> getJobsByStageExecution(UUID stageExecutionId) {
    return jobExecutionRepository.findByStageExecutionId(stageExecutionId);
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


  /**
   * Retrieves job definitions for a given stage execution.
   *
   * @param stageExecutionId the stage execution ID
   * @return list of job DTOs
   */
  public List<JobExecutionEntity> getJobExecutionsForStage(UUID stageExecutionId) {
    // Fetch jobs from database based on stage execution ID
    // This should be replaced with the actual method to get jobs
    return jobExecutionRepository.findJobExecutionsByStageExecutionId(stageExecutionId);
  }

  /**
   * Saves a job execution to the database.
   *
   * @param jobExecution Job execution entity
   */
  @Transactional
  public void saveJobExecution(JobExecutionEntity jobExecution) {
    jobExecutionRepository.save(jobExecution);
    PipelineLogger.info("💾 Job execution saved: " + jobExecution.getId());
  }
}
