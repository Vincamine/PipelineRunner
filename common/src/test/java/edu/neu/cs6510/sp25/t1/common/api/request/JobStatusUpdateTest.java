package edu.neu.cs6510.sp25.t1.common.api.request;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Unit tests for {@link JobStatusUpdate}.
 */
class JobStatusUpdateTest {

  private static final String JOB_EXECUTION_ID = "job-123";
  private static final String STATUS = "COMPLETED";
  private static final String LOGS = "Build successful";

  @Test
  void constructor_WithValidParameters_ShouldCreateInstanceWithCorrectValues() {
    // Act
    JobStatusUpdate update = new JobStatusUpdate(JOB_EXECUTION_ID, STATUS, LOGS);

    // Assert
    assertEquals(JOB_EXECUTION_ID, update.getJobExecutionId(), "Job execution ID should match");
    assertEquals(STATUS, update.getStatus(), "Status should match");
    assertEquals(LOGS, update.getLogs(), "Logs should match");
  }

  @Test
  void constructor_WithNullParameters_ShouldCreateInstanceWithNullValues() {
    // Act
    JobStatusUpdate update = new JobStatusUpdate(null, null, null);

    // Assert
    assertNull(update.getJobExecutionId(), "Job execution ID should be null");
    assertNull(update.getStatus(), "Status should be null");
    assertNull(update.getLogs(), "Logs should be null");
  }

  static Stream<Arguments> provideParameterVariations() {
    return Stream.of(
            // jobExecutionId, status, logs
            Arguments.of("", "", ""),
            Arguments.of("job-1", null, "Build failed"),
            Arguments.of(null, "RUNNING", null),
            Arguments.of("job-2", "FAILED", ""),
            Arguments.of("job-3", "", "Test output")
    );
  }

  @ParameterizedTest
  @MethodSource("provideParameterVariations")
  void constructor_WithVariousParameters_ShouldCreateCorrectInstance(
          String jobExecutionId, String status, String logs) {

    // Act
    JobStatusUpdate update = new JobStatusUpdate(jobExecutionId, status, logs);

    // Assert
    assertEquals(jobExecutionId, update.getJobExecutionId(), "Job execution ID should match");
    assertEquals(status, update.getStatus(), "Status should match");
    assertEquals(logs, update.getLogs(), "Logs should match");
  }

  @Test
  void getters_ShouldReturnCorrectValues() {
    // Arrange
    JobStatusUpdate update = new JobStatusUpdate(JOB_EXECUTION_ID, STATUS, LOGS);

    // Act & Assert
    assertEquals(JOB_EXECUTION_ID, update.getJobExecutionId());
    assertEquals(STATUS, update.getStatus());
    assertEquals(LOGS, update.getLogs());
    // Testing each getter individually ensures complete method coverage
  }

  @Test
  void jsonSerialization_ShouldSerializeAndDeserializeCorrectly() throws Exception {
    // Arrange
    JobStatusUpdate originalUpdate = new JobStatusUpdate(JOB_EXECUTION_ID, STATUS, LOGS);
    ObjectMapper objectMapper = new ObjectMapper();

    // Act
    String json = objectMapper.writeValueAsString(originalUpdate);
    JobStatusUpdate deserializedUpdate = objectMapper.readValue(json, JobStatusUpdate.class);

    // Assert
    assertEquals(JOB_EXECUTION_ID, deserializedUpdate.getJobExecutionId());
    assertEquals(STATUS, deserializedUpdate.getStatus());
    assertEquals(LOGS, deserializedUpdate.getLogs());
  }

  @Test
  void jobExecutionId_WithSpecialCharacters_ShouldHandleCorrectly() {
    // Arrange
    String specialId = "job-123!@#$%^&*()";

    // Act
    JobStatusUpdate update = new JobStatusUpdate(specialId, STATUS, LOGS);

    // Assert
    assertEquals(specialId, update.getJobExecutionId(), "Should handle special characters in job execution ID");
  }

  @Test
  void logs_WithMultilineContent_ShouldHandleCorrectly() {
    // Arrange
    String multilineLogs = "Line 1\nLine 2\nLine 3";

    // Act
    JobStatusUpdate update = new JobStatusUpdate(JOB_EXECUTION_ID, STATUS, multilineLogs);

    // Assert
    assertEquals(multilineLogs, update.getLogs(), "Should handle multiline logs correctly");
  }
}