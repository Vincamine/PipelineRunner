package edu.neu.cs6510.sp25.t1.worker;

import edu.neu.cs6510.sp25.t1.common.api.JobRequest;
import edu.neu.cs6510.sp25.t1.common.execution.ExecutionState;
import edu.neu.cs6510.sp25.t1.worker.client.BackendClient;
import edu.neu.cs6510.sp25.t1.worker.executor.DockerManager;
import edu.neu.cs6510.sp25.t1.worker.executor.JobExecutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static org.mockito.Mockito.*;

@SpringBootTest
class FailureHandlingTest {

  @Autowired
  private JobExecutor jobExecutor;

  @MockBean
  private DockerManager dockerManager;

  @MockBean
  private BackendClient backendClient;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testJobFailurePropagation() {
    // Define a job that should fail
    JobRequest failingJob = new JobRequest("fail-job-id", "pipeline-1", "failing-job", "commit-hash", null, List.of());

    // Mock container execution failure
    when(dockerManager.runContainer(any())).thenReturn("container-fail");
    when(dockerManager.waitForContainer("container-fail")).thenReturn(false); // Simulate failure

    // Execute the failing job
    jobExecutor.executeJob(failingJob);

    // Verify failure handling
    verify(backendClient).sendJobStatus("failing-job", ExecutionState.QUEUED);
    verify(backendClient).sendJobStatus("failing-job", ExecutionState.RUNNING);
    verify(backendClient).sendJobStatus("failing-job", ExecutionState.FAILED);

    // Ensure that cleanup is still called even on failure
    verify(dockerManager).cleanupContainer("container-fail");
  }
//TODO: test when complete backend and worker are implemented
//
//  @Test
//  void testRetryLogicOnFailure() throws InterruptedException {
//    // Define a job that fails initially but succeeds on retry
//    JobRequest retryJob = new JobRequest("retry-job-id", "pipeline-1", "retry-job", "commit-hash", null, List.of());
//
//    // Simulate first failure, then success on retry
//    when(dockerManager.runContainer(any())).thenReturn("container-retry");
//    when(dockerManager.waitForContainer("container-retry")).thenReturn(false).thenReturn(true);
//
//    // Execute the job
//    jobExecutor.executeJob(retryJob);
//
//    // Verify retry behavior (first failure, then success)
//    verify(backendClient, times(1)).sendJobStatus("retry-job", ExecutionState.QUEUED);
//    verify(backendClient, times(1)).sendJobStatus("retry-job", ExecutionState.RUNNING);
//    verify(backendClient, times(1)).sendJobStatus("retry-job", ExecutionState.FAILED);
//    verify(backendClient, times(1)).sendJobStatus("retry-job", ExecutionState.SUCCESS);
//
//    // Verify cleanup was performed after retry
//    verify(dockerManager, times(2)).cleanupContainer("container-retry");
//  }
}
