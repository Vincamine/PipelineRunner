package edu.neu.cs6510.sp25.t1.backend.service;

import edu.neu.cs6510.sp25.t1.backend.data.entity.JobEntity;
import edu.neu.cs6510.sp25.t1.backend.data.repository.JobRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for managing jobs within stages.
 */
@Service
public class JobService {

  private final JobRepository jobRepository;

  public JobService(JobRepository jobRepository) {
    this.jobRepository = jobRepository;
  }

  /**
   * Retrieves all jobs for a given stage.
   *
   * @param stageId The stage ID.
   * @return List of jobs in the specified stage.
   */
  public List<JobEntity> getJobsByStage(Long stageId) {
    return jobRepository.findByStageId(stageId);
  }

  /**
   * Retrieves a job by its ID.
   *
   * @param jobId The job ID.
   * @return The job entity.
   */
  public JobEntity getJobById(Long jobId) {
    return jobRepository.findById(jobId)
            .orElseThrow(() -> new IllegalArgumentException("Job not found: " + jobId));
  }

  /**
   * Creates a new job.
   *
   * @param job The job entity to create.
   * @return The saved job entity.
   */
  @Transactional
  public JobEntity createJob(JobEntity job) {
    return jobRepository.save(job);
  }

  /**
   * Updates an existing job.
   *
   * @param jobId The job ID.
   * @param updatedJob The updated job entity.
   * @return The updated job entity.
   */
  @Transactional
  public JobEntity updateJob(Long jobId, JobEntity updatedJob) {
    JobEntity existingJob = getJobById(jobId);
    existingJob.setName(updatedJob.getName());
    existingJob.setImage(updatedJob.getImage());
    existingJob.setAllowFailure(updatedJob.isAllowFailure());
    return jobRepository.save(existingJob);
  }

  /**
   * Deletes a job by its ID.
   *
   * @param jobId The job ID.
   */
  @Transactional
  public void deleteJob(Long jobId) {
    jobRepository.deleteById(jobId);
  }
}
