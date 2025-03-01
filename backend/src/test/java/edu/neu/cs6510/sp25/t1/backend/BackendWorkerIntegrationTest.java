//package edu.neu.cs6510.sp25.t1.backend;
//
//import edu.neu.cs6510.sp25.t1.common.model.execution.JobExecution;
//import edu.neu.cs6510.sp25.t1.worker.client.BackendClient;
//import edu.neu.cs6510.sp25.t1.worker.executor.DockerManager;
//import edu.neu.cs6510.sp25.t1.worker.executor.JobExecutor;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//class BackendWorkerIntegrationTest {
//    private JobExecutor worker;
//    private BackendClient backendClient;
//
//    @BeforeEach
//    void setUp() {
//        backendClient = mock(BackendClient.class);
//
//        DockerManager mockDockerManager = mock(DockerManager.class);
//        when(mockDockerManager.runContainer(any())).thenReturn("mock-container-id");
//        when(mockDockerManager.waitForContainer(any())).thenReturn(true);
//
//        worker = new JobExecutor(mockDockerManager, backendClient);
//    }
//
//    @Test
//    void testBackendWorkerIntegration() {
//        JobExecution job = new JobExecution("job1", "PENDING");
//
//        worker.executeJob(job);
//
//        // Verify the backend correctly receives job status updates
//        verify(backendClient).sendJobStatus("job1", "RUNNING");
//        verify(backendClient).sendJobStatus("job1", "SUCCESS");
//    }
//}
