//package edu.neu.cs6510.sp25.t1.backend;
//TODO: test when complete backend is implemented
//import edu.neu.cs6510.sp25.t1.common.runtime.JobExecution;
//import edu.neu.cs6510.sp25.t1.common.execution.ExecutionState;
//import edu.neu.cs6510.sp25.t1.common.config.JobDefinition;
//import edu.neu.cs6510.sp25.t1.worker.client.BackendClient;
//import edu.neu.cs6510.sp25.t1.worker.executor.DockerManager;
//import edu.neu.cs6510.sp25.t1.worker.executor.JobExecutor;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.util.List;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.*;
//
//class BackendWorkerIntegrationTest {
//    private JobExecutor worker;
//    private BackendClient backendClient;
//    private DockerManager mockDockerManager;
//
//    @BeforeEach
//    void setUp() {
//        backendClient = mock(BackendClient.class);
//        mockDockerManager = mock(DockerManager.class);
//
//        when(mockDockerManager.runContainer(any())).thenReturn("mock-container-id");
//        when(mockDockerManager.waitForContainer(any())).thenReturn(true); // Simulate success
//
//        worker = new JobExecutor(mockDockerManager, backendClient);
//    }
//
//    @Test
//    void testBackendWorkerIntegration() {
//        // 🔹 Correct JobExecution creation using JobDefinition
//        JobDefinition jobDefinition = new JobDefinition(
//                "job1", "stage1", "ubuntu:latest", List.of(), List.of(), false
//        );
//        JobExecution job = new JobExecution(jobDefinition, ExecutionState.PENDING.name(), false, List.of());
//
//        // Run job execution
//        worker.executeJob(job);
//
//        // 🔹 Verify the backend received the correct status updates in order
//        verify(backendClient).sendJobStatus(eq("job1"), eq(ExecutionState.RUNNING));
//        verify(backendClient).sendJobStatus(eq("job1"), eq(ExecutionState.SUCCESS));
//
//        // 🔹 Ensure Docker methods were called
//        verify(mockDockerManager).runContainer(any());
//        verify(mockDockerManager).waitForContainer(eq("mock-container-id"));
//        verify(mockDockerManager).cleanupContainer(eq("mock-container-id"));
//    }
//
//    @Test
//    void testBackendWorkerHandlesFailure() {
//        when(mockDockerManager.waitForContainer(any())).thenReturn(false); // Simulate failure
//
//        JobDefinition jobDefinition = new JobDefinition(
//                "job2", "stage2", "ubuntu:latest", List.of(), List.of(), false
//        );
//        JobExecution job = new JobExecution(jobDefinition, ExecutionState.PENDING.name(), false, List.of());
//
//        worker.executeJob(job);
//
//        // 🔹 Ensure failure state is sent to the backend
//        verify(backendClient).sendJobStatus(eq("job2"), eq(ExecutionState.RUNNING));
//        verify(backendClient).sendJobStatus(eq("job2"), eq(ExecutionState.FAILED));
//
//        verify(mockDockerManager).cleanupContainer(eq("mock-container-id"));
//    }
//}
