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

import edu.neu.cs6510.sp25.t1.common.api.request.JobRequest;
import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;
import edu.neu.cs6510.sp25.t1.worker.api.client.WorkerBackendClient;
import edu.neu.cs6510.sp25.t1.worker.manager.DockerManager;
import edu.neu.cs6510.sp25.t1.worker.executor.JobExecutor;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class FullPipelineExecutionTest {

  @Autowired
  private JobExecutor jobExecutor;

  @MockBean
  private DockerManager dockerManager;

  @MockBean
  private WorkerBackendClient workerBackendClient;

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
    verify(workerBackendClient).sendJobStatus("compile", ExecutionStatus.QUEUED);
    verify(workerBackendClient).sendJobStatus("compile", ExecutionStatus.RUNNING);
    verify(workerBackendClient).sendJobStatus("compile", ExecutionStatus.SUCCESS);

    // Execute second job (test) after compile succeeds
    when(workerBackendClient.getJobStatus("compile")).thenReturn(ExecutionStatus.SUCCESS);
    jobExecutor.executeJob(job2);
    verify(workerBackendClient).sendJobStatus("test", ExecutionStatus.QUEUED);
    verify(workerBackendClient).sendJobStatus("test", ExecutionStatus.RUNNING);
    verify(workerBackendClient).sendJobStatus("test", ExecutionStatus.SUCCESS);

    // Execute third job (deploy) after test succeeds
    when(workerBackendClient.getJobStatus("test")).thenReturn(ExecutionStatus.SUCCESS);
    jobExecutor.executeJob(job3);
    verify(workerBackendClient).sendJobStatus("deploy", ExecutionStatus.QUEUED);
    verify(workerBackendClient).sendJobStatus("deploy", ExecutionStatus.RUNNING);
    verify(workerBackendClient).sendJobStatus("deploy", ExecutionStatus.SUCCESS);
  }
}
