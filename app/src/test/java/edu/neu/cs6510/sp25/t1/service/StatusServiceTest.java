package edu.neu.cs6510.sp25.t1.service;

import edu.neu.cs6510.sp25.t1.model.PipelineStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StatusServiceTest {
    private StatusService statusService;

    @BeforeEach
    void setUp() {
        statusService = new StatusService();
    }

    @Test
    void testGetPipelineStatus_ValidId_ReturnsMockStatus() {
        final PipelineStatus status = statusService.getPipelineStatus("pipeline-123");
        assertNotNull(status);
        assertEquals("pipeline-123", status.getPipelineId());
    }

    @Test
    void testGetPipelineStatus_ThrowsErrorOnNullId() {
        assertThrows(IllegalArgumentException.class, () -> statusService.getPipelineStatus(null));
    }
}
