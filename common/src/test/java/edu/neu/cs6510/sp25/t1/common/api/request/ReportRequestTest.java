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
 * Unit tests for {@link ReportRequest}.
 */
class ReportRequestTest {

  private static final String PIPELINE_NAME = "ci-pipeline";
  private static final String STAGE_NAME = "build";
  private static final String JOB_NAME = "compile";
  private static final String RUN_NUMBER = "42";

  @Test
  void constructor_WithValidParameters_ShouldCreateInstanceWithCorrectValues() {
    // Act
    ReportRequest request = new ReportRequest(
            PIPELINE_NAME, STAGE_NAME, JOB_NAME, RUN_NUMBER);

    // Assert
    assertEquals(PIPELINE_NAME, request.getPipelineName(), "Pipeline name should match");
    assertEquals(STAGE_NAME, request.getStageName(), "Stage name should match");
    assertEquals(JOB_NAME, request.getJobName(), "Job name should match");
    assertEquals(RUN_NUMBER, request.getRunNumber(), "Run number should match");
  }

  @Test
  void constructor_WithNullParameters_ShouldCreateInstanceWithNullValues() {
    // Act
    ReportRequest request = new ReportRequest(null, null, null, null);

    // Assert
    assertNull(request.getPipelineName(), "Pipeline name should be null");
    assertNull(request.getStageName(), "Stage name should be null");
    assertNull(request.getJobName(), "Job name should be null");
    assertNull(request.getRunNumber(), "Run number should be null");
  }

  static Stream<Arguments> provideParameterVariations() {
    return Stream.of(
            // pipelineName, stageName, jobName, runNumber
            Arguments.of("", "", "", ""),
            Arguments.of("deploy-pipeline", null, "deploy", "1"),
            Arguments.of(null, "test", null, "123"),
            Arguments.of("build-pipeline", "integration", "test-integration", ""),
            Arguments.of("release", "", "publish", null)
    );
  }

  @ParameterizedTest
  @MethodSource("provideParameterVariations")
  void constructor_WithVariousParameters_ShouldCreateCorrectInstance(
          String pipelineName, String stageName, String jobName, String runNumber) {

    // Act
    ReportRequest request = new ReportRequest(
            pipelineName, stageName, jobName, runNumber);

    // Assert
    assertEquals(pipelineName, request.getPipelineName(), "Pipeline name should match");
    assertEquals(stageName, request.getStageName(), "Stage name should match");
    assertEquals(jobName, request.getJobName(), "Job name should match");
    assertEquals(runNumber, request.getRunNumber(), "Run number should match");
  }

  @Test
  void getters_ShouldReturnCorrectValues() {
    // Arrange
    ReportRequest request = new ReportRequest(
            PIPELINE_NAME, STAGE_NAME, JOB_NAME, RUN_NUMBER);

    // Act & Assert
    assertEquals(PIPELINE_NAME, request.getPipelineName());
    assertEquals(STAGE_NAME, request.getStageName());
    assertEquals(JOB_NAME, request.getJobName());
    assertEquals(RUN_NUMBER, request.getRunNumber());
    // Testing each getter individually ensures complete method coverage
  }

  @Test
  void jsonSerialization_ShouldSerializeAndDeserializeCorrectly() throws Exception {
    // Arrange
    ReportRequest originalRequest = new ReportRequest(
            PIPELINE_NAME, STAGE_NAME, JOB_NAME, RUN_NUMBER);
    ObjectMapper objectMapper = new ObjectMapper();

    // Act
    String json = objectMapper.writeValueAsString(originalRequest);
    ReportRequest deserializedRequest = objectMapper.readValue(json, ReportRequest.class);

    // Assert
    assertEquals(PIPELINE_NAME, deserializedRequest.getPipelineName());
    assertEquals(STAGE_NAME, deserializedRequest.getStageName());
    assertEquals(JOB_NAME, deserializedRequest.getJobName());
    assertEquals(RUN_NUMBER, deserializedRequest.getRunNumber());
  }

  @Test
  void pipelineName_WithSpecialCharacters_ShouldHandleCorrectly() {
    // Arrange
    String specialPipelineName = "pipeline-name_with.special@characters";

    // Act
    ReportRequest request = new ReportRequest(
            specialPipelineName, STAGE_NAME, JOB_NAME, RUN_NUMBER);

    // Assert
    assertEquals(specialPipelineName, request.getPipelineName(),
            "Should handle special characters in pipeline name");
  }

  @Test
  void runNumber_WithNonNumericValue_ShouldHandleCorrectly() {
    // Arrange
    String nonNumericRunNumber = "abc-123";

    // Act
    ReportRequest request = new ReportRequest(
            PIPELINE_NAME, STAGE_NAME, JOB_NAME, nonNumericRunNumber);

    // Assert
    assertEquals(nonNumericRunNumber, request.getRunNumber(),
            "Should handle non-numeric run number values");
  }

  @Test
  void jobName_WithLongValue_ShouldHandleCorrectly() {
    // Arrange
    String longJobName = "extremely-long-job-name-that-contains-many-words-and-descriptions-for-testing-purposes";

    // Act
    ReportRequest request = new ReportRequest(
            PIPELINE_NAME, STAGE_NAME, longJobName, RUN_NUMBER);

    // Assert
    assertEquals(longJobName, request.getJobName(),
            "Should handle long job name values");
  }
}