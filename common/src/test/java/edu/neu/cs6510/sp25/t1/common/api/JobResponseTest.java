package edu.neu.cs6510.sp25.t1.common.api;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JobResponseTest {

  @Test
  void testJobResponseConstructorAndGetters() {
    JobResponse response = new JobResponse("job123", 0, "Job completed",
            List.of("artifact1"), "");

    assertEquals("job123", response.getJobId());
    assertEquals(0, response.getExitCode());
    assertEquals("Job completed", response.getOutput());
    assertTrue(response.isSuccess());
    assertEquals(List.of("artifact1"), response.getCollectedArtifacts());
    assertEquals("", response.getErrorMessage());
  }

  @Test
  void testJobResponseFailureCase() {
    JobResponse response = new JobResponse("job123", 1, "Job failed",
            List.of(), "Some error");

    assertFalse(response.isSuccess());
    assertEquals(1, response.getExitCode());
    assertEquals("Some error", response.getErrorMessage());
  }

  @Test
  void testToString() {
    JobResponse response = new JobResponse("job123", 0, "Success", List.of("artifact1"), "");

    String result = response.toString();
    assertTrue(result.contains("job123"));
    assertTrue(result.contains("0")); // Checking exitCode instead of "Success"
  }

}
