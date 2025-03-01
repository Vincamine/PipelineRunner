package edu.neu.cs6510.sp25.t1.service;

import edu.neu.cs6510.sp25.t1.backend.service.PipelineExecutionService;
import edu.neu.cs6510.sp25.t1.common.model.PipelineState;
import edu.neu.cs6510.sp25.t1.common.model.execution.PipelineExecution;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PipelineExecutionServiceTest {
    private PipelineExecutionService pipelineService;

    @BeforeEach
    void setUp() {
        pipelineService = new PipelineExecutionService();
    }

    @Test
    void testStartPipeline() {
        PipelineExecution execution = pipelineService.startPipeline("123", "build");

        assertNotNull(execution);
        assertEquals("123", execution.getPipelineId());
        assertEquals(PipelineState.PENDING, execution.getState());
    }

    @Test
    void testGetPipelineExecution() {
        pipelineService.startPipeline("456", "deploy");

        PipelineExecution execution = pipelineService.getPipelineExecution("456");

        assertNotNull(execution);
        assertEquals("456", execution.getPipelineId());
    }

    // @Test
    // void testUpdatePipelineStatus() {
    //     pipelineService.startPipeline("789", "test");
    //     pipelineService.updatePipelineStatus("789", PipelineState.SUCCESS);

    //     PipelineExecution execution = pipelineService.getPipelineExecution("789");

    //     assertNotNull(execution);
    //     assertEquals(PipelineState.SUCCESS, execution.getState());
    // }
}
