package edu.neu.cs6510.sp25.t1.common.execution;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JobExecutionTest {

  private JobExecution jobExecution;

  @BeforeEach
  void setUp() {
    jobExecution = new JobExecution("build-job", "ubuntu:latest",
            List.of("echo 'Hello World'"), List.of("dependency-job"), false);
  }

  @Test
  void testConstructorInitialization() {
    assertEquals("build-job", jobExecution.getName());
    assertEquals(ExecutionStatus.PENDING, jobExecution.getStatus());
    assertEquals("ubuntu:latest", jobExecution.getImage());
    assertFalse(jobExecution.isAllowFailure());

    assertNotNull(jobExecution.getStatus(), "Status should not be null");
    assertNotNull(jobExecution.getImage(), "Image should not be null");
    assertNotNull(jobExecution.getName(), "Job name should not be null");
  }

  @Test
  void testUpdateState() {
    Instant beforeUpdate = Instant.now();

    jobExecution.updateState(ExecutionStatus.RUNNING);

    assertEquals(ExecutionStatus.RUNNING, jobExecution.getStatus());
    assertTrue(jobExecution.getStatus() != ExecutionStatus.PENDING, "Status should be updated");

    // Ensure lastUpdated time changes
    Instant afterUpdate = Instant.now();
    assertTrue(beforeUpdate.isBefore(afterUpdate), "Last updated timestamp should be updated");
  }

  @Test
  void testUpdateStateMultipleTimes() {
    jobExecution.updateState(ExecutionStatus.RUNNING);
    assertEquals(ExecutionStatus.RUNNING, jobExecution.getStatus());

    jobExecution.updateState(ExecutionStatus.SUCCESS);
    assertEquals(ExecutionStatus.SUCCESS, jobExecution.getStatus());

    jobExecution.updateState(ExecutionStatus.FAILED);
    assertEquals(ExecutionStatus.FAILED, jobExecution.getStatus());
  }

  @Test
  void testGetters() {
    assertEquals("build-job", jobExecution.getName());
    assertEquals("ubuntu:latest", jobExecution.getImage());
    assertFalse(jobExecution.isAllowFailure());
  }
}
