//package edu.neu.cs6510.sp25.t1.worker;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.MockitoAnnotations;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//
//import java.util.List;
//
//import edu.neu.cs6510.sp25.t1.common.api.JobRequest;
//import edu.neu.cs6510.sp25.t1.common.execution.ExecutionState;
//import edu.neu.cs6510.sp25.t1.worker.client.BackendClient;
//import edu.neu.cs6510.sp25.t1.worker.executor.DockerManager;
//import edu.neu.cs6510.sp25.t1.worker.executor.JobExecutor;
//
//import static org.mockito.Mockito.any;
//import static org.mockito.Mockito.never;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//@SpringBootTest
//class JobExecutionOrderTest {
//
//  @Autowired
//  private JobExecutor jobExecutor;
//
//  @MockBean
//  private DockerManager dockerManager;
//
//  @MockBean
//  private BackendClient backendClient;
//
//  @BeforeEach
//  void setUp() {
//    MockitoAnnotations.openMocks(this);
//  }
//
//  @Test
//  void testJobExecutionOrder() {
//    // Define jobs with dependencies
//    JobRequest job1 = new JobRequest("job1-id", "pipeline-1", "job1", "commit-hash", null, List.of());
//    JobRequest job2 = new JobRequest("job2-id", "pipeline-1", "job2", "commit-hash", null, List.of("job1"));
//
//    // Mock job1 execution as SUCCESS
//    when(backendClient.getJobStatus("job1")).thenReturn(ExecutionState.SUCCESS);
//    when(dockerManager.runContainer(any())).thenReturn("container-123");
//    when(dockerManager.waitForContainer("container-123")).thenReturn(true);
//
//    // Execute job1 and job2
//    jobExecutor.executeJob(job1);
//    jobExecutor.executeJob(job2);
//
//    // Verify execution order
//    verify(backendClient).sendJobStatus("job1", ExecutionState.QUEUED);
//    verify(backendClient).sendJobStatus("job1", ExecutionState.RUNNING);
//    verify(backendClient).sendJobStatus("job1", ExecutionState.SUCCESS);
//
//    verify(backendClient).sendJobStatus("job2", ExecutionState.QUEUED);
//    verify(backendClient).sendJobStatus("job2", ExecutionState.RUNNING);
//    verify(backendClient).sendJobStatus("job2", ExecutionState.SUCCESS);
//
//    verify(backendClient, never()).sendJobStatus("job2", ExecutionState.RUNNING);
//  }
//}
//TODO: test when complete backend and worker are implemented