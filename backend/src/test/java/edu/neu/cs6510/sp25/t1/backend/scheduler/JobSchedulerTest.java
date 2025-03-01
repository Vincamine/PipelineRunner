package edu.neu.cs6510.sp25.t1.backend.scheduler;

import edu.neu.cs6510.sp25.t1.backend.client.WorkerClient;
import edu.neu.cs6510.sp25.t1.common.model.execution.JobExecution;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class JobSchedulerTest {

  private WorkerClient workerClient;
  private JobScheduler jobScheduler;

  @BeforeEach
  void setUp() {
    workerClient = mock(WorkerClient.class);
    jobScheduler = new JobScheduler(workerClient);
  }

  @Test
  void testAddJob() {
    JobExecution job = mock(JobExecution.class);
    jobScheduler.addJob(job);
  }

  @Test
  void testProcessJobs() {
    JobExecution job = mock(JobExecution.class);
    jobScheduler.addJob(job);
    jobScheduler.processJobs();

    verify(workerClient, times(1)).sendJob(job);
  }
}
