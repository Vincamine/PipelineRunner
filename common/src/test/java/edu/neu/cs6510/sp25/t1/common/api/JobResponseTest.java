package edu.neu.cs6510.sp25.t1.common.api;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


class JobResponseTest {

  @Test
  void testJobResponseConstructorAndGetters() {
    JobResponse response = new JobResponse("job123", 0, "Success output",
            List.of("artifact1"), "");

    assertEquals("job123", response.getJobId());
    assertEquals(0, response.getExitCode());
    assertEquals("Success output", response.getOutput());
    assertEquals(List.of("artifact1"), response.getCollectedArtifacts());
    assertEquals("", response.getErrorMessage());
    assertTrue(response.isSuccess()); // Exit code 0 means success
  }

  @Test
  void testJobResponseFailureCase() {
    JobResponse response = new JobResponse("job456", 1, "Error output",
            List.of(), "Some error occurred");

    assertEquals("job456", response.getJobId());
    assertEquals(1, response.getExitCode());
    assertEquals("Error output", response.getOutput());
    assertEquals(List.of(), response.getCollectedArtifacts());
    assertEquals("Some error occurred", response.getErrorMessage());
    assertFalse(response.isSuccess()); // Exit code 1 means failure
  }

  @Test
  void testJobResponseWithNullValues() {
    JobResponse response = new JobResponse("job789", 2, null, null, null);

    assertEquals("job789", response.getJobId());
    assertEquals(2, response.getExitCode());
    assertEquals("", response.getOutput()); // Should default to empty string
    assertNotNull(response.getCollectedArtifacts());
    assertTrue(response.getCollectedArtifacts().isEmpty()); // Should default to empty list
    assertEquals("", response.getErrorMessage()); // Should default to empty string
  }

  @Test
  void testToStringMethod() {
    JobResponse response = new JobResponse("jobTest", 0, "Output log",
            List.of("artifact1", "artifact2"), "");

    String result = response.toString();
    System.out.println("Actual toString() Output: " + result); // Debugging line

    assertTrue(result.contains("jobTest"));
    assertTrue(result.contains("0")); // Exit code
    assertTrue(result.contains("Output log"));
    assertTrue(result.contains("artifact1"));
    assertTrue(result.contains("artifact2"));
  }
}
