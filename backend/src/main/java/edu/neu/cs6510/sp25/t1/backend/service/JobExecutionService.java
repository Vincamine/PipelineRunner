package edu.neu.cs6510.sp25.t1.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import edu.neu.cs6510.sp25.t1.backend.error.WorkerCommunicationException;
import edu.neu.cs6510.sp25.t1.backend.database.entity.JobExecutionEntity;
import edu.neu.cs6510.sp25.t1.backend.database.entity.StageExecutionEntity;
import edu.neu.cs6510.sp25.t1.backend.database.entity.JobEntity;
import edu.neu.cs6510.sp25.t1.backend.database.repository.JobRepository;
import edu.neu.cs6510.sp25.t1.backend.database.repository.JobExecutionRepository;
import edu.neu.cs6510.sp25.t1.backend.database.repository.StageExecutionRepository;
import edu.neu.cs6510.sp25.t1.backend.service.event.JobCompletedEvent;
import edu.neu.cs6510.sp25.t1.common.dto.JobDTO;
import edu.neu.cs6510.sp25.t1.common.dto.JobExecutionDTO;
import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;
import edu.neu.cs6510.sp25.t1.common.logging.PipelineLogger;
import edu.neu.cs6510.sp25.t1.backend.error.GlobalExceptionHandler;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JobExecutionService {
  private final JobExecutionRepository jobExecutionRepository;
  private final ApplicationEventPublisher eventPublisher;
  private final StageExecutionRepository stageExecutionRepository;
  private final RestTemplate restTemplate = new RestTemplate();
  private final JobRepository jobRepository;
  @Value("${worker.api.execute-url}")
  private String workerExecuteUrl;
  @Value("${worker.api.cancel-url}")
  private String workerCancelUrl;

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
    jobExecutionRepository.saveAndFlush(jobExecution); // Save and flush in one operation

    // Use separate transaction for worker communication to prevent rollback of job state
    sendJobToWorkerInNewTransaction(jobExecutionId);
  }

  /**
   * Send job to worker in a new transaction to prevent rollback of job state
   *
   * @param jobExecutionId job execution ID
   */
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void sendJobToWorkerInNewTransaction(UUID jobExecutionId) {
    try {
      sendJobToWorker(jobExecutionId);
    } catch (Exception e) {
      // Update job status to failed but in a new transaction
      updateJobStatusAfterWorkerFailure(jobExecutionId, e.getMessage());
      PipelineLogger.error("Job execution failed: " + e.getMessage());
    }
  }

  /**
   * Update job status after worker failure in a new transaction
   *
   * @param jobExecutionId job execution ID
   * @param errorMessage   error message
   */
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void updateJobStatusAfterWorkerFailure(UUID jobExecutionId, String errorMessage) {
    try {
      JobExecutionEntity jobExecution = jobExecutionRepository.findById(jobExecutionId)
              .orElseThrow(() -> new IllegalArgumentException("Job Execution not found"));

      jobExecution.updateState(ExecutionStatus.FAILED);
      jobExecutionRepository.saveAndFlush(jobExecution);

      PipelineLogger.info("Updated job status to FAILED due to worker error: " + errorMessage);
    } catch (Exception e) {
      PipelineLogger.error("Failed to update job status after worker failure: " + e.getMessage());
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
      PipelineLogger.info("Sending request to worker: " + workerExecuteUrl);
      response = restTemplate.postForEntity(workerExecuteUrl, request, String.class);
    } catch (ResourceAccessException e) {
      PipelineLogger.error("Worker is unreachable: " + e.getMessage());
      throw new WorkerCommunicationException("Worker is unreachable: " + e.getMessage());
    } catch (Exception e) {
      PipelineLogger.error("Error sending request to worker: " + e.getMessage());
      throw new WorkerCommunicationException("Error communicating with worker: " + e.getMessage());
    }

    if (!response.getStatusCode().is2xxSuccessful()) {
      PipelineLogger.error("Worker returned non-success status code: " + response.getStatusCode());
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
    jobExecutionRepository.saveAndFlush(jobExecution); // Save and flush in one operation

    if (newStatus == ExecutionStatus.SUCCESS) {
      eventPublisher.publishEvent(new JobCompletedEvent(this, jobExecutionId, jobExecution.getStageExecution().getId()));
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
      jobExecutionRepository.saveAndFlush(jobExecution); // Save and flush in one operation
      PipelineLogger.info("Job execution canceled in backend: " + jobExecutionId);

      // Send cancel request to worker in a new transaction
      sendCancelRequestInNewTransaction(jobExecutionId);
    } else {
      PipelineLogger.warn("Cannot cancel job execution. Current status: " + jobExecution.getStatus());
    }
  }

  /**
   * Send cancel request in a new transaction
   *
   * @param jobExecutionId job execution ID
   */
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void sendCancelRequestInNewTransaction(UUID jobExecutionId) {
    try {
      sendCancelRequestToWorker(jobExecutionId);
    } catch (Exception e) {
      PipelineLogger.error("Failed to send cancel request to worker: " + e.getMessage());
      // No need to rollback status change even if worker communication fails
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
      response = restTemplate.postForEntity(workerCancelUrl + "/cancel", request, String.class);
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

  /**
   * Retrieves job definitions for a given stage execution.
   *
   * @param stageExecutionId the stage execution ID
   * @return list of job DTOs
   */
  public List<JobExecutionEntity> getJobExecutionsForStage(UUID stageExecutionId) {
    StageExecutionEntity stageExecution = stageExecutionRepository.findById(stageExecutionId)
            .orElseThrow(() -> new IllegalArgumentException("Stage Execution not found"));

    List<JobExecutionEntity> jobs = jobExecutionRepository.findByStageExecution(stageExecution);

    PipelineLogger.info("🔍 Found " + jobs.size() + " jobs for stage execution ID: " + stageExecutionId);

    return jobs;
  }

  /**
   * Saves a job execution to the database.
   *
   * @param jobExecution Job execution entity
   */
  @Transactional
  public void saveJobExecution(JobExecutionEntity jobExecution) {
    jobExecutionRepository.saveAndFlush(jobExecution); // Save and flush in one operation
    PipelineLogger.info("💾 Job execution saved: " + jobExecution.getId());
  }

  /**
   * Get a job execution by ID.
   *
   * @param jobExecutionId The job execution ID
   * @return The job execution DTO
   * @throws IllegalArgumentException if the job execution is not found
   */
  public JobExecutionDTO getJobExecution(UUID jobExecutionId) {
    JobExecutionEntity jobExecution = jobExecutionRepository.findById(jobExecutionId)
            .orElseThrow(() -> new IllegalArgumentException("Job Execution not found"));

    // You'll need to convert this entity to a DTO
    // This assumes you have a method or mapper for this conversion
    return convertToDTO(jobExecution);
  }

  // Helper method to convert entity to DTO
  private JobExecutionDTO convertToDTO(JobExecutionEntity entity) {
    JobExecutionDTO dto = new JobExecutionDTO();
    dto.setId(entity.getId());
    dto.setAllowFailure(entity.isAllowFailure());
    dto.setStatus(entity.getStatus());

    // You'll need to fetch the job details and set them
    JobDTO jobDTO = new JobDTO();
    // Fill in job details from your database or other source
    jobDTO.setId(entity.getJobId());
    // Fetch job data from JobEntity
    JobEntity jobEntity = jobRepository.findById(entity.getJobId())
        .orElseThrow(() -> new IllegalArgumentException("Job not found"));
    // Set other job properties
    jobDTO.setStageId(jobEntity.getStageId());
    jobDTO.setName(jobEntity.getName());
    jobDTO.setDockerImage(jobEntity.getDockerImage());
    jobDTO.setAllowFailure(jobEntity.isAllowFailure());
    jobDTO.setCreatedAt(jobEntity.getCreatedAt());
    jobDTO.setUpdatedAt(jobEntity.getUpdatedAt());
    jobDTO.setScript(jobEntity.getScript());
    jobDTO.setWorkingDir(jobEntity.getWorkingDir());
    jobDTO.setDependencies(jobEntity.getDependencies());
    // job_artifacts needs re-work

    dto.setJob(jobDTO);

    return dto;
  }
}