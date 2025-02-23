package edu.neu.cs6510.sp25.t1.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PipelineStateTest {

    @Test
    void testPipelineStateDescriptions() {
        assertEquals("Pipeline is waiting to start", PipelineState.PENDING.getDescription());
        assertEquals("Pipeline completed successfully", PipelineState.SUCCESS.getDescription());
    }
}
