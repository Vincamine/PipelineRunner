//package edu.neu.cs6510.sp25.t1.backend;
//TODO: test when complete backend is implemented
//
//import edu.neu.cs6510.sp25.t1.backend.data.repository.JobExecutionRepository;
//import edu.neu.cs6510.sp25.t1.common.runtime.JobExecution;
//import edu.neu.cs6510.sp25.t1.common.runtime.ExecutionState;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.MockitoAnnotations;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.transaction.annotation.Transactional;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest
//@Transactional
//class DatabaseLoggingTest {
//
//  @Autowired
//  private JobExecutionRepository jobExecutionRepository;
//
//  private JobExecution jobExecution;
//
//  @BeforeEach
//  void setUp() {
//    MockitoAnnotations.openMocks(this);
//
//    // Create a job execution instance
//    jobExecution = new JobExecution("job1", "pipeline-1", ExecutionState.SUCCESS.name(), false, null);
//    jobExecutionRepository.save(jobExecution);
//  }
//
//  @Test
//  void testJobExecutionLogging() {
//    // Retrieve the job execution from the database
//    JobExecution retrievedJob = jobExecutionRepository.findById(jobExecution.getJobId()).orElse(null);
//
//    // Validate that execution metadata was saved correctly
//    assertNotNull(retrievedJob);
//    assertEquals("job1", retrievedJob.getJobName());
//    assertEquals("pipeline-1", retrievedJob.getPipelineName());
//    assertEquals(ExecutionState.SUCCESS.name(), retrievedJob.getStatus());
//  }
//
//  @Test
//  void testFailedJobLogging() {
//    // Simulate a failed job execution
//    JobExecution failedJob = new JobExecution("job2", "pipeline-1", ExecutionState.FAILED.name(), false, null);
//    jobExecutionRepository.save(failedJob);
//
//    // Retrieve and validate failure log
//    JobExecution retrievedJob = jobExecutionRepository.findById("job2").orElse(null);
//    assertNotNull(retrievedJob);
//    assertEquals(ExecutionState.FAILED.name(), retrievedJob.getStatus());
//  }
//}
