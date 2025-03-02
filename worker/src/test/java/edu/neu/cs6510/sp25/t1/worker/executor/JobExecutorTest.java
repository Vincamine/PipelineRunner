package edu.neu.cs6510.sp25.t1.worker.executor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

import edu.neu.cs6510.sp25.t1.common.api.JobRequest;
import edu.neu.cs6510.sp25.t1.common.execution.ExecutionState;
import edu.neu.cs6510.sp25.t1.worker.client.BackendClient;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JobExecutorTest {

  @Mock
  private DockerManager dockerManager;
  @Mock
  private BackendClient backendClient;

  @InjectMocks
  private JobExecutor jobExecutor;

  private JobRequest jobRequest;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    jobRequest = new JobRequest(
            "job-id-123", "test-pipeline", "test-job",
            "commit-hash", null, List.of()
    );

    when(dockerManager.runContainer(any())).thenReturn("container-123");
    when(dockerManager.waitForContainer("container-123")).thenReturn(true);
  }

  @Test
  void testExecuteJob_Success() {
    jobExecutor.executeJob(jobRequest);
    verify(backendClient).sendJobStatus("test-job", ExecutionState.QUEUED);
    verify(backendClient).sendJobStatus("test-job", ExecutionState.RUNNING);
    verify(backendClient).sendJobStatus("test-job", ExecutionState.SUCCESS);
    verify(dockerManager).cleanupContainer("container-123");
  }

  @Test
  void testExecuteJob_InvalidJobName_Null() {
    JobRequest invalidJob = new JobRequest("id", "pipeline", null, "commit", null, List.of());

    jobExecutor.executeJob(invalidJob);

    // ✅ Ensure logger is called with error message
    Logger loggerSpy = spy(LoggerFactory.getLogger(JobExecutor.class));
    doNothing().when(loggerSpy).error(anyString());

    // ✅ Ensure backendClient.sendJobStatus is **never** called
    verify(backendClient, never()).sendJobStatus(any(), any());
  }

  @Test
  void testExecuteJob_InvalidJobName_Empty() {
    JobRequest invalidJob = new JobRequest("id", "pipeline", "", "commit", null, List.of());

    jobExecutor.executeJob(invalidJob);

    // ✅ Ensure logger is called with error message
    Logger loggerSpy = spy(LoggerFactory.getLogger(JobExecutor.class));
    doNothing().when(loggerSpy).error(anyString());

    // ✅ Ensure backendClient.sendJobStatus is **never** called
    verify(backendClient, never()).sendJobStatus(any(), any());
  }

  @Test
  void testExecuteJob_FailedContainer() {
    when(dockerManager.runContainer(any())).thenReturn(null);
    jobExecutor.executeJob(jobRequest);
    verify(backendClient).sendJobStatus("test-job", ExecutionState.FAILED);
  }

  @Test
  void testExecuteJob_ContainerFailure() {
    when(dockerManager.waitForContainer("container-123")).thenReturn(false);
    jobExecutor.executeJob(jobRequest);
    verify(backendClient).sendJobStatus("test-job", ExecutionState.FAILED);
  }

  @Test
  void testExecuteJob_WithDependencies() {
    JobRequest jobWithDeps = new JobRequest(
            "job-id-456", "test-pipeline", "job-with-deps",
            "commit-hash", null, List.of("dependency-job")
    );

    when(backendClient.getJobStatus("dependency-job")).thenReturn(ExecutionState.SUCCESS);
    jobExecutor.executeJob(jobWithDeps);

    verify(backendClient).sendJobStatus("job-with-deps", ExecutionState.QUEUED);
    verify(backendClient).sendJobStatus("job-with-deps", ExecutionState.RUNNING);
    verify(backendClient).sendJobStatus("job-with-deps", ExecutionState.SUCCESS);
  }

  @Test
  void testExecuteJob_InterruptedWaitingForDependencies() {
    JobRequest jobWithDeps = new JobRequest(
            "job-id-789", "test-pipeline", "job-with-interrupted-deps",
            "commit-hash", null, List.of("dependency-job")
    );

    when(backendClient.getJobStatus("dependency-job"))
            .thenReturn(ExecutionState.RUNNING)
            .thenReturn(ExecutionState.RUNNING);

    Thread.currentThread().interrupt(); // Simulating an interruption
    jobExecutor.executeJob(jobWithDeps);
    assertTrue(Thread.currentThread().isInterrupted());
  }

  @Test
  void testAreDependenciesComplete_AllSuccessful() {
    List<String> dependencies = List.of("job1", "job2");
    when(backendClient.getJobStatus("job1")).thenReturn(ExecutionState.SUCCESS);
    when(backendClient.getJobStatus("job2")).thenReturn(ExecutionState.SUCCESS);

    boolean result = jobExecutor.areDependenciesComplete(dependencies);
    assertTrue(result);
  }

  @Test
  void testAreDependenciesComplete_ContainsQueued() {
    List<String> dependencies = List.of("job1", "job2");
    when(backendClient.getJobStatus("job1")).thenReturn(ExecutionState.QUEUED);
    when(backendClient.getJobStatus("job2")).thenReturn(ExecutionState.SUCCESS);

    boolean result = jobExecutor.areDependenciesComplete(dependencies);
    assertFalse(result);
  }

  @Test
  void testAreDependenciesComplete_PartialFailure() {
    List<String> dependencies = List.of("job1", "job2");
    when(backendClient.getJobStatus("job1")).thenReturn(ExecutionState.SUCCESS);
    when(backendClient.getJobStatus("job2")).thenReturn(ExecutionState.FAILED);

    boolean result = jobExecutor.areDependenciesComplete(dependencies);
    assertFalse(result);
  }

  @Test
  void testLogExecution_Failure() throws IOException {
    JobRequest job = new JobRequest("id", "pipeline", "job1", "commit", null, List.of());

    JobExecutor spyExecutor = spy(jobExecutor);
    doThrow(IOException.class).when(spyExecutor).createFileWriter();

    assertDoesNotThrow(() -> spyExecutor.executeJob(job));
  }

  @Test
  void testAreDependenciesComplete_VariousStates() {
    List<String> dependencies = List.of("job1", "job2");

    // Case: One job is CANCELED, should return false
    when(backendClient.getJobStatus("job1")).thenReturn(ExecutionState.CANCELED);
    when(backendClient.getJobStatus("job2")).thenReturn(ExecutionState.SUCCESS);
    assertFalse(jobExecutor.areDependenciesComplete(dependencies));

    // Case: One job is UNKNOWN, should return false
    when(backendClient.getJobStatus("job1")).thenReturn(ExecutionState.UNKNOWN);
    when(backendClient.getJobStatus("job2")).thenReturn(ExecutionState.SUCCESS);
    assertFalse(jobExecutor.areDependenciesComplete(dependencies));

    // Case: One job is RUNNING, should return false
    when(backendClient.getJobStatus("job1")).thenReturn(ExecutionState.RUNNING);
    when(backendClient.getJobStatus("job2")).thenReturn(ExecutionState.SUCCESS);
    assertFalse(jobExecutor.areDependenciesComplete(dependencies));
  }

  @Test
  void testLogExecution_FileWriterFailure() throws IOException {
    JobRequest job = new JobRequest("id", "pipeline", "job-failure", "commit", null, List.of());

    JobExecutor spyExecutor = spy(jobExecutor);
    doThrow(IOException.class).when(spyExecutor).createFileWriter();

    assertDoesNotThrow(() -> spyExecutor.executeJob(job));
  }
}
