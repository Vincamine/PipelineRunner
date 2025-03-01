package edu.neu.cs6510.sp25.t1.worker.api;

import edu.neu.cs6510.sp25.t1.common.model.execution.JobExecution;
import edu.neu.cs6510.sp25.t1.worker.api.WorkerController;
import edu.neu.cs6510.sp25.t1.worker.client.BackendClient;
import edu.neu.cs6510.sp25.t1.worker.executor.JobExecutor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class WorkerControllerTest {

    private WorkerController workerController;

    @Mock
    private JobExecutor jobExecutor;

    @Mock
    private BackendClient backendClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        workerController = new WorkerController(jobExecutor);
    }

    @Test
    void testExecuteJob_Success() {
        JobExecution jobExecution = mock(JobExecution.class);
        when(jobExecution.getJobName()).thenReturn("test-job"); // Ensure job name is set

        doNothing().when(jobExecutor).executeJob(jobExecution);

        ResponseEntity<String> response = workerController.executeJob(jobExecution);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Job executed successfully", response.getBody()); // Updated expected response
    }
}
