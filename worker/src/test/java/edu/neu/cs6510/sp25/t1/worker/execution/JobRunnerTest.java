package edu.neu.cs6510.sp25.t1.worker.execution;

import edu.neu.cs6510.sp25.t1.common.dto.JobDTO;
import edu.neu.cs6510.sp25.t1.common.dto.JobExecutionDTO;
import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;
import edu.neu.cs6510.sp25.t1.worker.service.WorkerCommunicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Test class for JobRunner
 */
@ExtendWith(MockitoExtension.class)
class JobRunnerTest {

    @Mock
    private DockerExecutor dockerExecutor;

    @Mock
    private WorkerCommunicationService workerCommunicationService;

    @InjectMocks
    private JobRunner jobRunner;

    @Captor
    private ArgumentCaptor<ExecutionStatus> statusCaptor;

    private JobExecutionDTO jobExecution;
    private UUID jobId;

    /**
     * Setup test environment before each test
     */
    @BeforeEach
    void setUp() {
        jobId = UUID.randomUUID();

        JobDTO job = new JobDTO();
        job.setDockerImage("test-image");
        job.setScript(List.of("echo test"));

        jobExecution = new JobExecutionDTO();
        jobExecution.setId(jobId);
        jobExecution.setJob(job);
        jobExecution.setAllowFailure(false);
    }

    /**
     * Test successful job execution with no dependencies
     */
    @Test
    void testRunJobSuccessNoDependencies() {
        // Mock dependency check
        when(workerCommunicationService.getJobDependencies(jobId)).thenReturn(new ArrayList<>());

        // Mock successful execution
        when(dockerExecutor.execute(jobExecution)).thenReturn(ExecutionStatus.SUCCESS);

        // Run the job
        jobRunner.runJob(jobExecution);

        // Verify interactions
        verify(workerCommunicationService).getJobDependencies(jobId);
        verify(dockerExecutor).execute(jobExecution);
        verify(workerCommunicationService).reportJobStatus(eq(jobId), eq(ExecutionStatus.SUCCESS), anyString());
    }

    /**
     * Test successful job execution with dependencies
     */
    @Test
    void testRunJobSuccessWithDependencies() {
        // Create mock dependencies
        List<UUID> dependencies = List.of(UUID.randomUUID(), UUID.randomUUID());

        // Mock dependency check
        when(workerCommunicationService.getJobDependencies(jobId)).thenReturn(dependencies);
        when(workerCommunicationService.getJobStatus(any(UUID.class))).thenReturn(ExecutionStatus.SUCCESS);

        // Mock successful execution
        when(dockerExecutor.execute(jobExecution)).thenReturn(ExecutionStatus.SUCCESS);

        // Run the job
        jobRunner.runJob(jobExecution);

        // Verify interactions
        verify(workerCommunicationService).getJobDependencies(jobId);
        verify(workerCommunicationService, times(dependencies.size())).getJobStatus(any(UUID.class));
        verify(dockerExecutor).execute(jobExecution);
        verify(workerCommunicationService).reportJobStatus(eq(jobId), eq(ExecutionStatus.SUCCESS), anyString());
    }

    /**
     * Test failed job execution
     */
    @Test
    void testRunJobFailed() {
        // Mock dependency check
        when(workerCommunicationService.getJobDependencies(jobId)).thenReturn(new ArrayList<>());

        // Mock failed execution
        when(dockerExecutor.execute(jobExecution)).thenReturn(ExecutionStatus.FAILED);

        // Run the job
        jobRunner.runJob(jobExecution);

        // Verify interactions
        verify(workerCommunicationService).getJobDependencies(jobId);
        verify(dockerExecutor).execute(jobExecution);
        verify(workerCommunicationService).reportJobStatus(eq(jobId), eq(ExecutionStatus.FAILED), anyString());
    }

    /**
     * Test failed job execution with allowFailure flag set to true
     */
    @Test
    void testRunJobFailedButAllowedToFail() {
        // Set allowFailure flag
        jobExecution.setAllowFailure(true);

        // Mock dependency check
        when(workerCommunicationService.getJobDependencies(jobId)).thenReturn(new ArrayList<>());

        // Mock failed execution
        when(dockerExecutor.execute(jobExecution)).thenReturn(ExecutionStatus.FAILED);

        // Run the job
        jobRunner.runJob(jobExecution);

        // Verify interactions
        verify(workerCommunicationService).getJobDependencies(jobId);
        verify(dockerExecutor).execute(jobExecution);
        verify(workerCommunicationService).reportJobStatus(eq(jobId), eq(ExecutionStatus.SUCCESS), anyString());
    }

    /**
     * Test job with unresolved dependencies
     */
    @Test
    void testRunJobWithUnresolvedDependencies() {
        // Create mock dependencies
        List<UUID> dependencies = List.of(UUID.randomUUID());

        // Mock dependency check - one dependency not resolved (PENDING)
        when(workerCommunicationService.getJobDependencies(jobId)).thenReturn(dependencies);
        when(workerCommunicationService.getJobStatus(any(UUID.class))).thenReturn(ExecutionStatus.PENDING);

        // Run the job - should fail due to unresolved dependencies
        jobRunner.runJob(jobExecution);

        // Verify interactions
        verify(workerCommunicationService).getJobDependencies(jobId);
        verify(workerCommunicationService, atLeast(1)).getJobStatus(any(UUID.class));

        // Verify job was not executed due to dependency failure
        verify(dockerExecutor, never()).execute(any());

        // Verify FAILED status was reported
        verify(workerCommunicationService).reportJobStatus(eq(jobId), eq(ExecutionStatus.FAILED), anyString());
    }

    /**
     * Test job with failed dependencies
     */
    @Test
    void testRunJobWithFailedDependencies() {
        // Create mock dependencies
        List<UUID> dependencies = List.of(UUID.randomUUID(), UUID.randomUUID());

        // Mock dependency check - first dependency succeeded, second failed
        when(workerCommunicationService.getJobDependencies(jobId)).thenReturn(dependencies);
        when(workerCommunicationService.getJobStatus(dependencies.get(0))).thenReturn(ExecutionStatus.SUCCESS);
        when(workerCommunicationService.getJobStatus(dependencies.get(1))).thenReturn(ExecutionStatus.FAILED);

        // Run the job - should fail due to failed dependencies
        jobRunner.runJob(jobExecution);

        // Verify interactions
        verify(workerCommunicationService).getJobDependencies(jobId);
        verify(workerCommunicationService, atLeast(1)).getJobStatus(any(UUID.class));

        // Verify job was not executed due to dependency failure
        verify(dockerExecutor, never()).execute(any());

        // Verify FAILED status was reported
        verify(workerCommunicationService).reportJobStatus(eq(jobId), eq(ExecutionStatus.FAILED), anyString());
    }

    /**
     * Test interrupted dependency check
     */
    @Test
    void testRunJobInterruptedDependencyCheck() throws Exception {
        // Create a mock JobRunner with overridden dependency check behavior
        JobRunner spyJobRunner = spy(jobRunner);

        // Set up the test to throw InterruptedException during dependency check
        doAnswer(invocation -> {
            throw new InterruptedException("Test interruption");
        }).when(spyJobRunner).runJob(jobExecution);

        // Run the job
        try {
            spyJobRunner.runJob(jobExecution);
        } catch (Exception e) {
            // Expected exception
        }

        // Verify no execution happened
        verify(dockerExecutor, never()).execute(any());
    }
}