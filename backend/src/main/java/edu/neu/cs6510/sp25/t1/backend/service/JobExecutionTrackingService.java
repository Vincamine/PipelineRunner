//package edu.neu.cs6510.sp25.t1.backend.service;
//
//import edu.neu.cs6510.sp25.t1.backend.data.repository.JobExecutionRepository;
//import edu.neu.cs6510.sp25.t1.worker.execution.JobExecution;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.Optional;
//
///**
// * Service for handling job execution tracking.
// */
//@Service
//public class JobExecutionTrackingService {
//  private static final Logger logger = LoggerFactory.getLogger(JobExecutionTrackingService.class);
//  private final JobExecutionRepository jobExecutionRepository;
//
//  public JobExecutionTrackingService(JobExecutionRepository jobExecutionRepository) {
//    this.jobExecutionRepository = jobExecutionRepository;
//  }
//
//  /**
//   * Logs job execution state updates.
//   *
//   * @param jobName The job name.
//   * @param state   The new execution state.
//   */
//  @Transactional
//  public void logJobExecution(String jobName, String state) {
//    Optional<JobExecution> jobRunStateOpt = jobExecutionRepository.findByJobName(jobName);
//
//    if (jobRunStateOpt.isPresent()) {
//      JobExecution jobExecution = jobRunStateOpt.get();
//      jobExecution.complete(state);
//      jobExecutionRepository.save(jobExecution);
//      logger.info("Updated job execution state: {} -> {}", jobName, state);
//    } else {
//      logger.warn("Job execution not found: {}", jobName);
//    }
//  }
//}
