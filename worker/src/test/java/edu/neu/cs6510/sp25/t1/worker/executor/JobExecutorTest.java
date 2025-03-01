package edu.neu.cs6510.sp25.t1.worker.executor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileWriter;
import java.io.IOException;

import edu.neu.cs6510.sp25.t1.common.api.JobRequest;
import edu.neu.cs6510.sp25.t1.common.model.ExecutionState;
import edu.neu.cs6510.sp25.t1.worker.client.BackendClient;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


class JobExecutorTest {
  private JobExecutor jobExecutor;
  private DockerManager dockerManager;
  private BackendClient backendClient;

  @BeforeEach
  void setUp() {
    dockerManager = mock(DockerManager.class);
    backendClient = mock(BackendClient.class);
    jobExecutor = new JobExecutor(dockerManager, backendClient);
  }

  @Test
  void testExecuteJob_Success() {
    JobRequest jobRequest = new JobRequest("123", "pipeline1", "job1", "commitHash", null, null);
    when(dockerManager.runContainer(any())).thenReturn("container123");
    when(dockerManager.waitForContainer("container123")).thenReturn(true);

    jobExecutor.executeJob(jobRequest);

    verify(backendClient).sendJobStatus("job1", ExecutionState.RUNNING);
    verify(backendClient).sendJobStatus("job1", ExecutionState.SUCCESS);
    verify(dockerManager).cleanupContainer("container123");
  }

  @Test
  void testExecuteJob_Failure() {
    JobRequest jobRequest = new JobRequest("123", "pipeline1", "job1", "commitHash", null, null);
    when(dockerManager.runContainer(any())).thenReturn(null);

    jobExecutor.executeJob(jobRequest);

    verify(backendClient).sendJobStatus("job1", ExecutionState.FAILED);
  }

  @Test
  void testLogExecution_WritesLog() throws IOException {
    JobRequest jobRequest = new JobRequest("123", "pipeline1", "job1", "commitHash", null, null);

    FileWriter fileWriter = mock(FileWriter.class);
    JobExecutor spyExecutor = spy(jobExecutor);

    doReturn(fileWriter).when(spyExecutor).createFileWriter();

    spyExecutor.executeJob(jobRequest);

    verify(fileWriter, atLeastOnce()).write(anyString());
  }

}
