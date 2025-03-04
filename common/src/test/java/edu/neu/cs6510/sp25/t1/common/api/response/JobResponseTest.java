package edu.neu.cs6510.sp25.t1.common.api.response;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JobResponseTest {

  @Test
  void testConstructorWithValidValues() {
    JobResponse response = new JobResponse(
            "job-123",
            0,
            "Execution successful",
            List.of("artifact1", "artifact2"),
            ""
    );

    assertEquals("job-123", response.getJobId());
    assertEquals(0, response.getExitCode());
    assertEquals("Execution successful", response.getOutput());
    assertEquals(List.of("artifact1", "artifact2"), response.getCollectedArtifacts());
    assertEquals("", response.getErrorMessage());
    assertTrue(response.isSuccess());
  }

  @Test
  void testConstructorHandlesNullValues() {
    JobResponse response = new JobResponse("job-456", 1, null, null, null);

    assertEquals("job-456", response.getJobId());
    assertEquals(1, response.getExitCode());
    assertEquals("", response.getOutput()); // Null should default to an empty string
    assertNotNull(response.getCollectedArtifacts());
    assertTrue(response.getCollectedArtifacts().isEmpty()); // Null list should default to empty list
    assertEquals("", response.getErrorMessage()); // Null should default to an empty string
    assertFalse(response.isSuccess());
  }

  @Test
  void testIsSuccessMethod() {
    JobResponse successResponse = new JobResponse("job-789", 0, "Success", List.of(), "");
    JobResponse failureResponse = new JobResponse("job-890", 1, "Failure", List.of(), "Error occurred");

    assertTrue(successResponse.isSuccess(), "Expected isSuccess() to return true for exitCode 0");
    assertFalse(failureResponse.isSuccess(), "Expected isSuccess() to return false for exitCode != 0");
  }

  @Test
  void testGetters() {
    JobResponse response = new JobResponse("job-321", 2, "Some output", List.of("artifactA"), "Some error");

    assertEquals("job-321", response.getJobId());
    assertEquals(2, response.getExitCode());
    assertEquals("Some output", response.getOutput());
    assertEquals(List.of("artifactA"), response.getCollectedArtifacts());
    assertEquals("Some error", response.getErrorMessage());
  }

  @Test
  void testToStringMethod() {
    JobResponse response = new JobResponse("job-555", 0, "Test output", List.of("file1", "file2"), "");

    String expectedString = "JobResponse{jobId='job-555', exitCode=0, output='Test output', " +
            "success=true, collectedArtifacts=[file1, file2], errorMessage=''}";

    assertEquals(expectedString, response.toString());
  }
}
