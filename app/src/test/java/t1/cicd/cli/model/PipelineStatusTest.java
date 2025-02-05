package t1.cicd.cli.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.Instant;
import org.junit.jupiter.api.Test;

public class PipelineStatusTest {
  @Test
  void testConstructorAndGetters(){
    String pipelineId = "test-123";
    PipelineState state = PipelineState.RUNNING;
    int progress = 50;
    String currentStage = "Build";
    Instant startTime = Instant.now();
    Instant lastUpdated = Instant.now();

    PipelineStatus status = new PipelineStatus(
      pipelineId, state, progress,currentStage, startTime, lastUpdated
    );

    assertEquals(pipelineId, status.getPipelineId());
    assertEquals(state, status.getState());
    assertEquals(progress, status.getProgress());
    assertEquals(currentStage, status.getCurrentStage());
    assertEquals(startTime, status.getStartTime());
    assertEquals(lastUpdated, status.getLastUpdated());

  }

  @Test
  void testDefaultConstructor(){
    String pipelineId = "test-123";
    PipelineStatus status = new PipelineStatus(pipelineId);
    assertEquals(pipelineId, status.getPipelineId());
    assertEquals(PipelineState.UNKNOWN, status.getState());
    assertEquals(0, status.getProgress());
    assertNotNull(status.getStartTime());
    assertNotNull(status.getLastUpdated());
  }

  @Test
  void testSetters() {
    PipelineStatus status = new PipelineStatus("test-123");

    status.setState(PipelineState.RUNNING);
    status.setProgress(75);
    status.setCurrentStage("Test");
    status.setMessage("Running tests");
    Instant now = Instant.now();
    status.setLastUpdated(now);

    assertEquals(PipelineState.RUNNING, status.getState());
    assertEquals(75, status.getProgress());
    assertEquals("Test", status.getCurrentStage());
    assertEquals("Running tests", status.getMessage());
    assertEquals(now, status.getLastUpdated());
  }
}
