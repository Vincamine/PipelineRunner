package edu.neu.cs6510.sp25.t1.worker.service;

import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;
import edu.neu.cs6510.sp25.t1.worker.api.client.WorkerBackendClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the WorkerCommunicationService class.
 *
 * Tests the communication service between worker and backend.
 */
@ExtendWith(MockitoExtension.class)
class WorkerCommunicationServiceTest {

    @Mock
    private WorkerBackendClient backendClient;

    @InjectMocks
    private WorkerCommunicationService service;

    /**
     * Tests retrieving job dependencies.
     */
    @Test
    void shouldGetJobDependencies() {
        // Prepare test data
        UUID jobId = UUID.randomUUID();
        List<UUID> expectedDependencies = List.of(UUID.randomUUID(), UUID.randomUUID());

        when(backendClient.getJobDependencies(jobId)).thenReturn(expectedDependencies);

        // Execute the test
        List<UUID> result = service.getJobDependencies(jobId);

        // Verify results
        assertEquals(expectedDependencies, result);
        verify(backendClient).getJobDependencies(jobId);
    }

    /**
     * Tests retrieving job execution status.
     */
    @Test
    void shouldGetJobStatus() {
        // Prepare test data
        UUID jobId = UUID.randomUUID();
        ExecutionStatus expectedStatus = ExecutionStatus.SUCCESS;

        when(backendClient.getJobStatus(jobId)).thenReturn(expectedStatus);

        // Execute the test
        ExecutionStatus result = service.getJobStatus(jobId);

        // Verify results
        assertEquals(expectedStatus, result);
        verify(backendClient).getJobStatus(jobId);
    }

    /**
     * Tests reporting job status to the backend.
     */
    @Test
    void shouldReportJobStatus() {
        // Prepare test data
        UUID jobExecutionId = UUID.randomUUID();
        ExecutionStatus status = ExecutionStatus.SUCCESS;
        String logs = "Execution logs";

        // Execute the test
        service.reportJobStatus(jobExecutionId, status, logs);

        // Verify results
        verify(backendClient).updateJobStatus(jobExecutionId, status, logs);
    }
}