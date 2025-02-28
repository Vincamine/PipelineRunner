package edu.neu.cs6510.sp25.t1.executor;

import edu.neu.cs6510.sp25.t1.client.BackendClient;
import edu.neu.cs6510.sp25.t1.model.definition.JobDefinition;
import edu.neu.cs6510.sp25.t1.model.execution.JobExecution;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.mockito.Mockito.*;

class JobExecutorTest {

    private JobExecutor jobExecutor;

    @Mock
    private DockerManager dockerManager;

    @Mock
    private BackendClient backendClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jobExecutor = new JobExecutor(dockerManager, backendClient);
    }

    @Test
    void testExecuteJob_Success() {
        JobDefinition jobDefinition = new JobDefinition("test-job", "test-stage", "test-image",
                List.of("echo Hello"), List.of(), false);
        JobExecution jobExecution = new JobExecution(jobDefinition, "PENDING", false, List.of());

        when(dockerManager.runContainer(jobExecution)).thenReturn("container123");
        when(dockerManager.waitForContainer("container123")).thenReturn(true);

        jobExecutor.executeJob(jobExecution);

        // Verify status updates to backend
        verify(backendClient).sendJobStatus("test-job", "RUNNING");
        verify(backendClient).sendJobStatus("test-job", "SUCCESS");

        // Verify container cleanup
        verify(dockerManager).cleanupContainer("container123");
    }

    @Test
    void testExecuteJob_Failure() {
        JobDefinition jobDefinition = new JobDefinition("test-job", "test-stage", "test-image",
                List.of("echo Hello"), List.of(), false);
        JobExecution jobExecution = new JobExecution(jobDefinition, "PENDING", false, List.of());

        when(dockerManager.runContainer(jobExecution)).thenReturn("container123");
        when(dockerManager.waitForContainer("container123")).thenReturn(false);

        jobExecutor.executeJob(jobExecution);

        // Verify failure status updates
        verify(backendClient).sendJobStatus("test-job", "RUNNING");
        verify(backendClient).sendJobStatus("test-job", "FAILED");

        // Verify container cleanup
        verify(dockerManager).cleanupContainer("container123");
    }

    @Test
    void testExecuteJob_ContainerFailure() {
        JobDefinition jobDefinition = new JobDefinition("test-job", "test-stage", "test-image",
                List.of("echo Hello"), List.of(), false);
        JobExecution jobExecution = new JobExecution(jobDefinition, "PENDING", false, List.of());

        when(dockerManager.runContainer(jobExecution)).thenReturn(null);

        jobExecutor.executeJob(jobExecution);

        // Verify immediate failure
        verify(backendClient).sendJobStatus("test-job", "FAILED");

        // Ensure no container cleanup happens since it was never created
        verify(dockerManager, never()).cleanupContainer(anyString());
    }
}
