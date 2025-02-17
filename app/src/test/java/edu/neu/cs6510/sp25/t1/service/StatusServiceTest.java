package edu.neu.cs6510.sp25.t1.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.neu.cs6510.sp25.t1.model.PipelineState;
import edu.neu.cs6510.sp25.t1.model.PipelineStatus;
import edu.neu.cs6510.sp25.t1.service.StatusService;

public class StatusServiceTest {

  private StatusService statusService;

  @BeforeEach
  void setUp() {
    statusService = new StatusService();
  }

  @Test
  void testGetPipelineStatus() {
    final String pipelineId = "test-123";
    final PipelineStatus status = statusService.getPipelineStatus(pipelineId);
    assertNotNull(status);
    assertEquals(pipelineId, status.getPipelineId());
    Assertions.assertEquals(PipelineState.RUNNING, status.getState());
    assertEquals(75, status.getProgress());
    assertEquals("Deploy to Staging", status.getCurrentStage());
    assertEquals("Deploying to staging environment", status.getMessage());
    assertNotNull(status.getStartTime());
    assertNotNull(status.getLastUpdated());
  }

  @Test
  void testGetPipelineStatusWithEmptyId() {
    final String emptyId = "";

    final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      statusService.getPipelineStatus(emptyId);
    });
    assertEquals("Pipeline ID cannot be empty", exception.getMessage());
  }

  @Test
  void testGetPipelineStatusWithNullId() {
    final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      statusService.getPipelineStatus(null);
    });
    assertEquals("Pipeline ID cannot be null", exception.getMessage());
  }

}
