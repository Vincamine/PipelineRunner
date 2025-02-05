package t1.cicd.cli.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import t1.cicd.cli.model.PipelineState;
import t1.cicd.cli.model.PipelineStatus;

public class StatusServiceTest {
  private StatusService statusService;

  @BeforeEach
  void setUp(){
    statusService = new StatusService();
  }

  @Test
  void testGetPipelineStatus(){
    String pipelineId = "test-123";
    PipelineStatus status = statusService.getPipelineStatus(pipelineId);
    assertNotNull(status);
    assertEquals(pipelineId, status.getPipelineId());
    assertEquals(PipelineState.RUNNING, status.getState());
    assertEquals(75, status.getProgress());
    assertEquals("Deploy to Staging", status.getCurrentStage());
    assertEquals("Deploying to staging environment", status.getMessage());
    assertNotNull(status.getStartTime());
    assertNotNull(status.getLastUpdated());
  }
  @Test
  void testGetPipelineStatusWithEmptyId(){
    String emptyId = "";

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      statusService.getPipelineStatus(emptyId);
    });
    assertEquals("Pipeline ID cannot be empty", exception.getMessage());
  }

  @Test
  void testGetPipelineStatusWithNullId(){
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      statusService.getPipelineStatus(null);
    });
    assertEquals("Pipeline ID cannot be null", exception.getMessage());
  }

}
