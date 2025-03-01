package edu.neu.cs6510.sp25.t1.worker.api;

import edu.neu.cs6510.sp25.t1.common.api.JobRequest;
import edu.neu.cs6510.sp25.t1.worker.executor.JobExecutor;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WorkerControllerTest {

    private final JobExecutor jobExecutor = mock(JobExecutor.class);
    private final WorkerController controller = new WorkerController(jobExecutor);

    @Test
    void testExecuteJob_Success() {
        JobRequest request = new JobRequest("job1", "pipeline1", "testJob", "commit123", null, null);
        ResponseEntity<String> response = controller.executeJob(request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Job execution started successfully.", response.getBody());
        verify(jobExecutor, times(1)).executeJob(request);
    }

    @Test
    void testExecuteJob_BadRequest() {
        JobRequest request = new JobRequest(null, "pipeline1", "", "commit123", null, null);
        ResponseEntity<String> response = controller.executeJob(request);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Error: Job name cannot be null or empty.", response.getBody());
    }

    @Test
    void testExecuteJob_ExceptionHandling() {
        JobRequest request = new JobRequest("job1", "pipeline1", "testJob", "commit123", null, null);
        doThrow(new RuntimeException("Job failed")).when(jobExecutor).executeJob(any());

        ResponseEntity<String> response = controller.executeJob(request);
        assertEquals(500, response.getStatusCodeValue());
        assertTrue(response.getBody().contains("Job execution failed"));
    }
}
