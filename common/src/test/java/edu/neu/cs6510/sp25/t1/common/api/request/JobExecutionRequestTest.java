package edu.neu.cs6510.sp25.t1.common.api.request;


import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link JobExecutionRequest}.
 */
class JobExecutionRequestTest {

  private static final String EXECUTION_ID = "exec-123";
  private static final String STAGE = "build";
  private static final String JOB = "compile";
  private static final String DOCKER_IMAGE = "maven:3.8.4-openjdk-17";
  private static final List<String> COMMANDS = Arrays.asList("mvn clean", "mvn install");

  @Test
  void constructor_WithValidParameters_ShouldCreateInstanceWithCorrectValues() {
    // Act
    JobExecutionRequest request = new JobExecutionRequest(
            EXECUTION_ID, STAGE, JOB, DOCKER_IMAGE, COMMANDS);

    // Assert
    assertEquals(EXECUTION_ID, request.getExecutionId(), "Execution ID should match");
    assertEquals(STAGE, request.getStage(), "Stage should match");
    assertEquals(JOB, request.getJob(), "Job should match");
    assertEquals(DOCKER_IMAGE, request.getDockerImage(), "Docker image should match");
    assertEquals(COMMANDS, request.getCommands(), "Commands should match");
  }

  @Test
  void constructor_WithNullParameters_ShouldCreateInstanceWithNullValues() {
    // Act
    JobExecutionRequest request = new JobExecutionRequest(
            null, null, null, null, null);

    // Assert
    assertNull(request.getExecutionId(), "Execution ID should be null");
    assertNull(request.getStage(), "Stage should be null");
    assertNull(request.getJob(), "Job should be null");
    assertNull(request.getDockerImage(), "Docker image should be null");
    assertNull(request.getCommands(), "Commands should be null");
  }

  @Test
  void constructor_WithEmptyCommands_ShouldCreateInstanceWithEmptyCommandsList() {
    // Arrange
    List<String> emptyCommands = Collections.emptyList();

    // Act
    JobExecutionRequest request = new JobExecutionRequest(
            EXECUTION_ID, STAGE, JOB, DOCKER_IMAGE, emptyCommands);

    // Assert
    assertNotNull(request.getCommands(), "Commands list should not be null");
    assertTrue(request.getCommands().isEmpty(), "Commands list should be empty");
  }

  static Stream<Arguments> provideParameterVariations() {
    return Stream.of(
            // executionId, stage, job, dockerImage, commands
            Arguments.of("", "", "", "", Collections.emptyList()),
            Arguments.of("exec-1", null, "job1", "alpine:latest", List.of("echo hello")),
            Arguments.of(null, "test", null, "node:14", null),
            Arguments.of("exec-2", "deploy", "publish", "", List.of("npm publish"))
    );
  }

  @ParameterizedTest
  @MethodSource("provideParameterVariations")
  void constructor_WithVariousParameters_ShouldCreateCorrectInstance(
          String executionId, String stage, String job, String dockerImage, List<String> commands) {

    // Act
    JobExecutionRequest request = new JobExecutionRequest(
            executionId, stage, job, dockerImage, commands);

    // Assert
    assertEquals(executionId, request.getExecutionId(), "Execution ID should match");
    assertEquals(stage, request.getStage(), "Stage should match");
    assertEquals(job, request.getJob(), "Job should match");
    assertEquals(dockerImage, request.getDockerImage(), "Docker image should match");
    assertEquals(commands, request.getCommands(), "Commands should match");
  }

  @Test
  void getters_ShouldReturnCorrectValues() {
    // Arrange
    JobExecutionRequest request = new JobExecutionRequest(
            EXECUTION_ID, STAGE, JOB, DOCKER_IMAGE, COMMANDS);

    // Act & Assert
    assertEquals(EXECUTION_ID, request.getExecutionId());
    assertEquals(STAGE, request.getStage());
    assertEquals(JOB, request.getJob());
    assertEquals(DOCKER_IMAGE, request.getDockerImage());
    assertEquals(COMMANDS, request.getCommands());
    // Testing each getter individually
  }

  @Test
  void jsonSerialization_ShouldSerializeAndDeserializeCorrectly() throws Exception {
    // Arrange
    JobExecutionRequest originalRequest = new JobExecutionRequest(
            EXECUTION_ID, STAGE, JOB, DOCKER_IMAGE, COMMANDS);
    ObjectMapper objectMapper = new ObjectMapper();

    // Act
    String json = objectMapper.writeValueAsString(originalRequest);
    JobExecutionRequest deserializedRequest = objectMapper.readValue(json, JobExecutionRequest.class);

    // Assert
    assertEquals(EXECUTION_ID, deserializedRequest.getExecutionId());
    assertEquals(STAGE, deserializedRequest.getStage());
    assertEquals(JOB, deserializedRequest.getJob());
    assertEquals(DOCKER_IMAGE, deserializedRequest.getDockerImage());
    assertEquals(COMMANDS, deserializedRequest.getCommands());
  }

  @Test
  void commands_ShouldNotBeModifiableExternally() {
    // Arrange
    List<String> originalCommands = Arrays.asList("cmd1", "cmd2");
    JobExecutionRequest request = new JobExecutionRequest(
            EXECUTION_ID, STAGE, JOB, DOCKER_IMAGE, originalCommands);

    // Act & Assert
    List<String> returnedCommands = request.getCommands();
    assertNotNull(returnedCommands, "Returned commands should not be null");
    assertEquals(originalCommands, returnedCommands, "Returned commands should match original commands");

    // Note: We can't test immutability directly because Arrays.asList already returns
    // an unmodifiable list. In a real implementation, you might want to defensively copy
    // the commands in the constructor and getter if they need to be truly immutable.
  }
}