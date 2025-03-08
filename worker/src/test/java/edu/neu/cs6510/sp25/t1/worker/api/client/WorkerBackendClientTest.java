package edu.neu.cs6510.sp25.t1.worker.api.client;

import edu.neu.cs6510.sp25.t1.common.api.request.ArtifactUploadRequest;
import edu.neu.cs6510.sp25.t1.common.api.request.JobStatusUpdate;
import edu.neu.cs6510.sp25.t1.common.dto.JobExecutionDTO;
import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the WorkerBackendClient class.
 *
 * Tests the client communication with the backend API.
 */
@ExtendWith(MockitoExtension.class)
class WorkerBackendClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private WorkerBackendClient workerBackendClient;

    private static final String BACKEND_API_URL = "http://backend-service/api";

    @BeforeEach
    void setUp() {
        // Setting the non-static field using ReflectionTestUtils
        ReflectionTestUtils.setField(workerBackendClient, "backendApiUrl", BACKEND_API_URL);
    }

    /**
     * Tests getting job execution details from the backend.
     */
    @Test
    void shouldGetJobExecution() {
        // Prepare test data
        UUID jobExecutionId = UUID.randomUUID();
        JobExecutionDTO expectedJobExecution = new JobExecutionDTO();
        expectedJobExecution.setId(jobExecutionId);

        when(restTemplate.getForObject(BACKEND_API_URL + "/jobs/" + jobExecutionId, JobExecutionDTO.class))
                .thenReturn(expectedJobExecution);

        // Execute the test
        JobExecutionDTO result = workerBackendClient.getJobExecution(jobExecutionId);

        // Verify results
        assertEquals(expectedJobExecution, result);
    }

    /**
     * Tests getting job dependencies from the backend.
     */
    @Test
    void shouldGetJobDependencies() {
        // Prepare test data
        UUID jobId = UUID.randomUUID();
        List<UUID> expectedDependencies = List.of(UUID.randomUUID(), UUID.randomUUID());

        when(restTemplate.getForObject(BACKEND_API_URL + "/jobs/" + jobId + "/dependencies", List.class))
                .thenReturn(expectedDependencies);

        // Execute the test
        List<UUID> result = workerBackendClient.getJobDependencies(jobId);

        // Verify results
        assertEquals(expectedDependencies, result);
    }

    /**
     * Tests getting job execution status from the backend.
     */
    @Test
    void shouldGetJobStatus() {
        // Prepare test data
        UUID jobId = UUID.randomUUID();
        ExecutionStatus expectedStatus = ExecutionStatus.SUCCESS;

        when(restTemplate.getForObject(BACKEND_API_URL + "/jobs/" + jobId + "/status", ExecutionStatus.class))
                .thenReturn(expectedStatus);

        // Execute the test
        ExecutionStatus result = workerBackendClient.getJobStatus(jobId);

        // Verify results
        assertEquals(expectedStatus, result);
    }

    /**
     * Tests updating job execution status in the backend.
     */
    @Test
    void shouldUpdateJobStatus() {
        // Prepare test data
        UUID jobExecutionId = UUID.randomUUID();
        ExecutionStatus status = ExecutionStatus.SUCCESS;
        String logs = "Execution logs";

        // Execute the test
        workerBackendClient.updateJobStatus(jobExecutionId, status, logs);

        // Verify results
        verify(restTemplate).put(
                eq(BACKEND_API_URL + "/job/status"),
                any(JobStatusUpdate.class)
        );
    }

    /**
     * Tests uploading artifacts after execution.
     */
    @Test
    void shouldUploadArtifacts() {
        // Prepare test data
        UUID jobExecutionId = UUID.randomUUID();
        List<String> artifacts = List.of("artifact1.txt", "artifact2.txt");

        // Execute the test
        workerBackendClient.uploadArtifacts(jobExecutionId, artifacts);

        // Verify results
        verify(restTemplate).postForObject(
                eq(BACKEND_API_URL + "/job/artifact/upload"),
                any(ArtifactUploadRequest.class),
                eq(Void.class)
        );
//        verify(restTemplate).postForObject(
//                eq(BACKEND_API_URL + "/job/artifact/upload"),
//                eq(new ArtifactUploadRequest(jobExecutionId, artifacts)),
//                eq(Void.class)
//        );
    }
}