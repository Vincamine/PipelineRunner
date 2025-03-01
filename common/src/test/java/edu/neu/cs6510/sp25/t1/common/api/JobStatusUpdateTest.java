package edu.neu.cs6510.sp25.t1.common.api;

import edu.neu.cs6510.sp25.t1.common.model.ExecutionState;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JobStatusUpdateTest {

  @Test
  void testJobStatusUpdateConstructorAndGetters() {
    JobStatusUpdate update = new JobStatusUpdate("job123", "RUNNING");

    assertEquals("job123", update.getJobName());
    assertEquals(ExecutionState.RUNNING, update.getStatus());
  }

  @Test
  void testJobStatusUpdateWithInvalidStatus() {
    JobStatusUpdate update = new JobStatusUpdate("job123", "INVALID");

    assertEquals(ExecutionState.UNKNOWN, update.getStatus());
  }

  @Test
  void testJobStatusUpdateWithNullJobName() {
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      new JobStatusUpdate(null, "RUNNING");
    });

    assertTrue(exception.getMessage().contains("Job name cannot be null"));
  }

  @Test
  void testToString() {
    JobStatusUpdate update = new JobStatusUpdate("job123", "SUCCESS");

    String result = update.toString();
    assertTrue(result.contains("job123"));
    assertTrue(result.contains("SUCCESS"));
  }
}
