package edu.neu.cs6510.sp25.t1.backend.scheduler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.neu.cs6510.sp25.t1.backend.client.WorkerClient;
import edu.neu.cs6510.sp25.t1.common.runtime.JobRunState;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


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
    JobRunState job = mock(JobRunState.class);
    jobScheduler.addJob(job);
  }

  @Test
  void testProcessJobs() {
    JobRunState job = mock(JobRunState.class);
    jobScheduler.addJob(job);
    jobScheduler.processJobs();

    verify(workerClient, times(1)).sendJob(job);
  }
}
