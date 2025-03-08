package edu.neu.cs6510.sp25.t1.worker.service;

import edu.neu.cs6510.sp25.t1.common.dto.JobExecutionDTO;
import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;
import edu.neu.cs6510.sp25.t1.worker.execution.JobRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the PipelineExecutionWorkerService class.
 *
 * Tests the pipeline execution service that handles job execution with dependency resolution.
 */
@ExtendWith(MockitoExtension.class)
class PipelineExecutionWorkerServiceTest {

    @Mock
    private JobRunner jobRunner;

    @Mock
    private WorkerCommunicationService workerCommunicationService;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private PipelineExecutionWorkerService pipelineExecutionWorkerService;

    private static final String BACKEND_API_URL = "http://backend-service/api";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(pipelineExecutionWorkerService, "backendApiUrl", BACKEND_API_URL);
    }

    /**
     * Tests executing a single job.
     */
    @Test
    void shouldExecuteSingleJob() {
        // Prepare test data
        UUID jobId = UUID.randomUUID();
        JobExecutionDTO job = new JobExecutionDTO();
        job.setId(jobId);

        // Execute the test
        pipelineExecutionWorkerService.executeJob(job);

        // Verify the results
        verify(jobRunner).runJob(job);
    }

    /**
     * Tests executing a pipeline with independent jobs only.
     */
    @Test
    void shouldExecutePipelineWithIndependentJobsOnly() {
        // Prepare test data
        UUID pipelineExecutionId = UUID.randomUUID();

        JobExecutionDTO job1 = new JobExecutionDTO();
        job1.setId(UUID.randomUUID());

        JobExecutionDTO job2 = new JobExecutionDTO();
        job2.setId(UUID.randomUUID());

        List<JobExecutionDTO> jobs = Arrays.asList(job1, job2);

        // Mock dependencies and API calls
        when(restTemplate.getForObject(eq(BACKEND_API_URL + "/pipeline-executions/" + pipelineExecutionId + "/jobs"), eq(List.class)))
                .thenReturn(jobs);

        when(workerCommunicationService.getJobDependencies(job1.getId())).thenReturn(Collections.emptyList());
        when(workerCommunicationService.getJobDependencies(job2.getId())).thenReturn(Collections.emptyList());

        // Execute the test
        pipelineExecutionWorkerService.executePipeline(pipelineExecutionId);

        // Verify the results
        verify(restTemplate).getForObject(eq(BACKEND_API_URL + "/pipeline-executions/" + pipelineExecutionId + "/jobs"), eq(List.class));
        verify(workerCommunicationService).getJobDependencies(job1.getId());
        verify(workerCommunicationService).getJobDependencies(job2.getId());
        verify(jobRunner).runJob(job1);
        verify(jobRunner).runJob(job2);
    }

    /**
     * Tests executing a pipeline with both independent and dependent jobs.
     */
    @Test
    void shouldExecutePipelineWithMixedJobDependencies() {
        // Prepare test data
        UUID pipelineExecutionId = UUID.randomUUID();

        JobExecutionDTO independentJob = new JobExecutionDTO();
        independentJob.setId(UUID.randomUUID());

        JobExecutionDTO dependentJob = new JobExecutionDTO();
        dependentJob.setId(UUID.randomUUID());

        List<JobExecutionDTO> jobs = Arrays.asList(independentJob, dependentJob);

        // Mock dependencies and API calls
        when(restTemplate.getForObject(eq(BACKEND_API_URL + "/pipeline-executions/" + pipelineExecutionId + "/jobs"), eq(List.class)))
                .thenReturn(jobs);

        when(workerCommunicationService.getJobDependencies(independentJob.getId())).thenReturn(Collections.emptyList());
        when(workerCommunicationService.getJobDependencies(dependentJob.getId())).thenReturn(List.of(independentJob.getId()));
        when(workerCommunicationService.getJobStatus(independentJob.getId())).thenReturn(ExecutionStatus.SUCCESS);

        // Execute the test
        pipelineExecutionWorkerService.executePipeline(pipelineExecutionId);

        // Verify the results
        verify(restTemplate).getForObject(eq(BACKEND_API_URL + "/pipeline-executions/" + pipelineExecutionId + "/jobs"), eq(List.class));
        verify(workerCommunicationService, times(2)).getJobDependencies(any(UUID.class));
        verify(jobRunner).runJob(independentJob);
        verify(jobRunner).runJob(dependentJob);
    }

    /**
     * Tests handling an empty job list for pipeline execution.
     */
    @Test
    void shouldHandleEmptyJobList() {
        // Prepare test data
        UUID pipelineExecutionId = UUID.randomUUID();

        // Mock API call to return empty list
        when(restTemplate.getForObject(eq(BACKEND_API_URL + "/pipeline-executions/" + pipelineExecutionId + "/jobs"), eq(List.class)))
                .thenReturn(Collections.emptyList());

        // Execute the test
        pipelineExecutionWorkerService.executePipeline(pipelineExecutionId);

        // Verify the results
        verify(restTemplate).getForObject(eq(BACKEND_API_URL + "/pipeline-executions/" + pipelineExecutionId + "/jobs"), eq(List.class));
        verify(jobRunner, never()).runJob(any());
    }

    /**
     * Tests handling API call failure for pipeline execution.
     */
    @Test
    void shouldHandleApiCallFailure() {
        // Prepare test data
        UUID pipelineExecutionId = UUID.randomUUID();

        // Mock API call to return null
        when(restTemplate.getForObject(eq(BACKEND_API_URL + "/pipeline-executions/" + pipelineExecutionId + "/jobs"), eq(List.class)))
                .thenReturn(null);

        // Execute the test
        pipelineExecutionWorkerService.executePipeline(pipelineExecutionId);

        // Verify the results
        verify(restTemplate).getForObject(eq(BACKEND_API_URL + "/pipeline-executions/" + pipelineExecutionId + "/jobs"), eq(List.class));
        verify(jobRunner, never()).runJob(any());
    }

    /**
     * Tests handling timeout for dependent jobs.
     */
    @Test
    void shouldHandleDependentJobTimeout() {
        // Prepare test data
        UUID pipelineExecutionId = UUID.randomUUID();

        JobExecutionDTO independentJob = new JobExecutionDTO();
        independentJob.setId(UUID.randomUUID());

        JobExecutionDTO dependentJob = new JobExecutionDTO();
        dependentJob.setId(UUID.randomUUID());

        List<JobExecutionDTO> jobs = Arrays.asList(independentJob, dependentJob);

        // Mock dependencies and API calls
        when(restTemplate.getForObject(eq(BACKEND_API_URL + "/pipeline-executions/" + pipelineExecutionId + "/jobs"), eq(List.class)))
                .thenReturn(jobs);

        when(workerCommunicationService.getJobDependencies(independentJob.getId())).thenReturn(Collections.emptyList());
        when(workerCommunicationService.getJobDependencies(dependentJob.getId())).thenReturn(List.of(independentJob.getId()));
        when(workerCommunicationService.getJobStatus(independentJob.getId())).thenReturn(ExecutionStatus.RUNNING);

        // Execute the test - Should time out after 10 attempts
        pipelineExecutionWorkerService.executePipeline(pipelineExecutionId);

        // Verify the results
        verify(restTemplate).getForObject(eq(BACKEND_API_URL + "/pipeline-executions/" + pipelineExecutionId + "/jobs"), eq(List.class));
        verify(workerCommunicationService, times(2)).getJobDependencies(any(UUID.class));
        verify(jobRunner).runJob(independentJob);
        verify(workerCommunicationService, atLeast(10)).getJobStatus(independentJob.getId());
    }
}