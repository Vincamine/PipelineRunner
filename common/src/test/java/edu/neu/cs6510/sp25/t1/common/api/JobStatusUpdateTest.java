package edu.neu.cs6510.sp25.t1.common.api;

import org.junit.jupiter.api.Test;

import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JobStatusUpdateTest {

  @Test
  void testConstructorWithValidInputs() {
    JobStatusUpdate update = new JobStatusUpdate("job-1", "RUNNING");

    assertEquals("job-1", update.getJobName());
    assertEquals(ExecutionStatus.RUNNING, update.getStatus());
  }

  @Test
  void testConstructorThrowsExceptionForNullJobName() {
    Exception exception = assertThrows(IllegalArgumentException.class,
            () -> new JobStatusUpdate(null, "SUCCESS"));

    assertEquals("Job name cannot be null or empty.", exception.getMessage());
  }

  @Test
  void testConstructorThrowsExceptionForEmptyJobName() {
    Exception exception1 = assertThrows(IllegalArgumentException.class,
            () -> new JobStatusUpdate("", "FAILED"));

    assertEquals("Job name cannot be null or empty.", exception1.getMessage());

    Exception exception2 = assertThrows(IllegalArgumentException.class,
            () -> new JobStatusUpdate("   ", "FAILED"));

    assertEquals("Job name cannot be null or empty.", exception2.getMessage());
  }

  @Test
  void testConstructorHandlesInvalidStatus() {
    Exception exception = assertThrows(IllegalArgumentException.class,
            () -> new JobStatusUpdate("job-2", "INVALID_STATUS"));

    assertEquals("Invalid ExecutionStatus: INVALID_STATUS", exception.getMessage(),
            "Expected exception for invalid execution status");
  }


  @Test
  void testGetters() {
    JobStatusUpdate update = new JobStatusUpdate("stage-1", "PENDING");
    assertEquals("stage-1", update.getJobName());
    assertEquals(ExecutionStatus.PENDING, update.getStatus());
  }

  @Test
  void testToStringMethod() {
    JobStatusUpdate update = new JobStatusUpdate("job-3", "SUCCESS");

    String expectedString = "JobStatusUpdate{jobName='job-3', status=SUCCESS}";
    assertEquals(expectedString, update.toString());
  }
}
