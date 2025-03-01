package edu.neu.cs6510.sp25.t1.api;

import edu.neu.cs6510.sp25.t1.service.PipelineExecutionService;
import edu.neu.cs6510.sp25.t1.model.execution.PipelineExecution;
import edu.neu.cs6510.sp25.t1.model.PipelineState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PipelineControllerTest {
    private PipelineController controller;
    private PipelineExecutionService pipelineService;

    @BeforeEach
    void setUp() {
        pipelineService = Mockito.mock(PipelineExecutionService.class);
        controller = new PipelineController(pipelineService);
    }

    @Test
    void testRunPipeline() {
        RunPipelineRequest request = new RunPipelineRequest();
        request.setPipeline("123");

        PipelineExecution mockExecution = new PipelineExecution("123", null, null);
        when(pipelineService.startPipeline("123", "build")).thenReturn(mockExecution);

        ResponseEntity<PipelineExecution> response = controller.runPipeline(request); // ✅ Pass request object

        assertNotNull(response.getBody());
        assertEquals("123", response.getBody().getPipelineId());
        assertEquals(PipelineState.PENDING, response.getBody().getState());
    }

    @SuppressWarnings("deprecation")
    @Test
    void testGetStatus() {
        PipelineExecution mockExecution = new PipelineExecution("456", null, null);
        when(pipelineService.getPipelineExecution("456")).thenReturn(mockExecution);

        ResponseEntity<?> response = controller.getStatus("456");

        assertEquals(200, response.getStatusCodeValue());
    }

    @SuppressWarnings("deprecation")
    @Test
    void testGetStatus_NotFound() {
        when(pipelineService.getPipelineExecution("789")).thenReturn(null);

        ResponseEntity<?> response = controller.getStatus("789");

        assertEquals(404, response.getStatusCodeValue());
    }
}
