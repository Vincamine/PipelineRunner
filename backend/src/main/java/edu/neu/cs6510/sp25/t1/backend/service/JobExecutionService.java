package edu.neu.cs6510.sp25.t1.backend.service;

import edu.neu.cs6510.sp25.t1.backend.repository.JobExecutionRepository;
import edu.neu.cs6510.sp25.t1.common.runtime.JobRunState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service for handling job execution tracking.
 */
@Service
public class JobExecutionService {
  private static final Logger logger = LoggerFactory.getLogger(JobExecutionService.class);
  private final JobExecutionRepository jobExecutionRepository;

  public JobExecutionService(JobExecutionRepository jobExecutionRepository) {
    this.jobExecutionRepository = jobExecutionRepository;
  }

  /**
   * Logs job execution state updates.
   *
   * @param jobName The job name.
   * @param state   The new execution state.
   */
  @Transactional
  public void logJobExecution(String jobName, String state) {
    Optional<JobRunState> jobRunStateOpt = jobExecutionRepository.findByJobName(jobName);

    if (jobRunStateOpt.isPresent()) {
      JobRunState jobRunState = jobRunStateOpt.get();
      jobRunState.complete(state);
      jobExecutionRepository.save(jobRunState);
      logger.info("Updated job execution state: {} -> {}", jobName, state);
    } else {
      logger.warn("Job execution not found: {}", jobName);
    }
  }
}
