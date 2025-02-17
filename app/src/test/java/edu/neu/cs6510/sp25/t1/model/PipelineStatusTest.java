package edu.neu.cs6510.sp25.t1.model;

import org.junit.jupiter.api.Test;
import java.time.Instant;
import static org.junit.jupiter.api.Assertions.*;

class PipelineStatusTest {

    @Test
    void testDefaultConstructor() {
        final PipelineStatus status = new PipelineStatus("pipeline-123");

        assertEquals("pipeline-123", status.getPipelineId());
        assertEquals(PipelineState.UNKNOWN, status.getState());
        assertEquals(0, status.getProgress());
        assertNotNull(status.getStartTime());
        assertNotNull(status.getLastUpdated());
    }

    @Test
    void testParameterizedConstructor() {
        final Instant start = Instant.now().minusSeconds(600);
        final Instant updated = Instant.now();
        final PipelineStatus status = new PipelineStatus("pipeline-123", PipelineState.RUNNING, 50, "Build Stage", start, updated);

        assertEquals("pipeline-123", status.getPipelineId());
        assertEquals(PipelineState.RUNNING, status.getState());
        assertEquals(50, status.getProgress());
        assertEquals("Build Stage", status.getCurrentStage());
        assertEquals(start, status.getStartTime());
        assertEquals(updated, status.getLastUpdated());
    }

    @Test
    void testSetters() {
        final PipelineStatus status = new PipelineStatus("pipeline-123");
        status.setState(PipelineState.FAILED);
        status.setProgress(80);
        status.setCurrentStage("Deploy");
        status.setMessage("Deployment failed");
        final Instant newUpdateTime = Instant.now();
        status.setLastUpdated(newUpdateTime);

        assertEquals(PipelineState.FAILED, status.getState());
        assertEquals(80, status.getProgress());
        assertEquals("Deploy", status.getCurrentStage());
        assertEquals("Deployment failed", status.getMessage());
        assertEquals(newUpdateTime, status.getLastUpdated());
    }

    @Test
    void testProgressBoundary() {
        final PipelineStatus status = new PipelineStatus("pipeline-123");
        status.setProgress(120);
        assertEquals(100, status.getProgress());

        status.setProgress(-5);
        assertEquals(0, status.getProgress());
    }
}
