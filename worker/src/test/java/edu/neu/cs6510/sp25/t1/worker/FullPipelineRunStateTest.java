package edu.neu.cs6510.sp25.t1.worker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import edu.neu.cs6510.sp25.t1.common.api.JobRequest;
import edu.neu.cs6510.sp25.t1.common.execution.ExecutionState;
import edu.neu.cs6510.sp25.t1.worker.client.BackendClient;
import edu.neu.cs6510.sp25.t1.worker.executor.DockerManager;
import edu.neu.cs6510.sp25.t1.worker.executor.JobExecutor;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class FullPipelineRunStateTest {

  @Autowired
  private JobExecutor jobExecutor;

  @MockBean
  private DockerManager dockerManager;

  @MockBean
  private BackendClient backendClient;

  @MockBean
  private RestTemplate restTemplate;

  private JobRequest job1, job2, job3;

  @BeforeEach
  void setUp() {
    job1 = new JobRequest("job-1", "test-pipeline", "compile",
            "commit-hash", null, List.of());
    job2 = new JobRequest("job-2", "test-pipeline", "test",
            "commit-hash", null, List.of("compile"));
    job3 = new JobRequest("job-3", "test-pipeline", "deploy",
            "commit-hash", null, List.of("test"));

    when(dockerManager.runContainer(any())).thenReturn("container-123");
    when(dockerManager.waitForContainer("container-123")).thenReturn(true);
  }

  @Test
  void testFullPipelineExecution() {
    // Execute first job (compile)
    jobExecutor.executeJob(job1);
    verify(backendClient).sendJobStatus("compile", ExecutionState.QUEUED);
    verify(backendClient).sendJobStatus("compile", ExecutionState.RUNNING);
    verify(backendClient).sendJobStatus("compile", ExecutionState.SUCCESS);

    // Execute second job (test) after compile succeeds
    when(backendClient.getJobStatus("compile")).thenReturn(ExecutionState.SUCCESS);
    jobExecutor.executeJob(job2);
    verify(backendClient).sendJobStatus("test", ExecutionState.QUEUED);
    verify(backendClient).sendJobStatus("test", ExecutionState.RUNNING);
    verify(backendClient).sendJobStatus("test", ExecutionState.SUCCESS);

    // Execute third job (deploy) after test succeeds
    when(backendClient.getJobStatus("test")).thenReturn(ExecutionState.SUCCESS);
    jobExecutor.executeJob(job3);
    verify(backendClient).sendJobStatus("deploy", ExecutionState.QUEUED);
    verify(backendClient).sendJobStatus("deploy", ExecutionState.RUNNING);
    verify(backendClient).sendJobStatus("deploy", ExecutionState.SUCCESS);
  }
}
