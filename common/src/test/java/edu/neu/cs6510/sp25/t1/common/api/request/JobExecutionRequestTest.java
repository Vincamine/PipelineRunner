package edu.neu.cs6510.sp25.t1.common.api.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link JobExecutionRequest}.
 */
class JobExecutionRequestTest {

    private static final UUID JOB_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    private static final UUID STAGE_EXECUTION_ID = UUID.fromString("223e4567-e89b-12d3-a456-426614174001");
    private static final String COMMIT_HASH = "abc123def456";
    private static final boolean IS_LOCAL = true;
    private static final String DOCKER_IMAGE = "java:11-alpine";
    private static final List<String> COMMANDS = Arrays.asList("mvn clean", "mvn test", "mvn package");

    @Test
    void constructor_WithValidParameters_ShouldCreateInstanceWithCorrectValues() {
        // Act
        JobExecutionRequest request = new JobExecutionRequest(
                JOB_ID, STAGE_EXECUTION_ID, COMMIT_HASH, IS_LOCAL, DOCKER_IMAGE, COMMANDS);

        // Assert
        assertEquals(JOB_ID, request.getJobId(), "Job ID should match");
        assertEquals(STAGE_EXECUTION_ID, request.getStageExecutionId(), "Stage execution ID should match");
        assertEquals(COMMIT_HASH, request.getCommitHash(), "Commit hash should match");
        assertEquals(IS_LOCAL, request.isLocal(), "IsLocal flag should match");
        assertEquals(DOCKER_IMAGE, request.getDockerImage(), "Docker image should match");
        assertEquals(COMMANDS, request.getCommands(), "Commands list should match");
    }

    static Stream<Arguments> provideParameterVariations() {
        return Stream.of(
                // jobId, stageExecutionId, commitHash, isLocal, dockerImage, commands
                Arguments.of(null, STAGE_EXECUTION_ID, COMMIT_HASH, IS_LOCAL, DOCKER_IMAGE, COMMANDS),
                Arguments.of(JOB_ID, null, COMMIT_HASH, IS_LOCAL, DOCKER_IMAGE, COMMANDS),
                Arguments.of(JOB_ID, STAGE_EXECUTION_ID, null, IS_LOCAL, DOCKER_IMAGE, COMMANDS),
                Arguments.of(JOB_ID, STAGE_EXECUTION_ID, COMMIT_HASH, !IS_LOCAL, DOCKER_IMAGE, COMMANDS),
                Arguments.of(JOB_ID, STAGE_EXECUTION_ID, COMMIT_HASH, IS_LOCAL, null, COMMANDS),
                Arguments.of(JOB_ID, STAGE_EXECUTION_ID, COMMIT_HASH, IS_LOCAL, DOCKER_IMAGE, null),
                Arguments.of(JOB_ID, STAGE_EXECUTION_ID, COMMIT_HASH, IS_LOCAL, DOCKER_IMAGE, Collections.emptyList()),
                Arguments.of(JOB_ID, STAGE_EXECUTION_ID, "", IS_LOCAL, DOCKER_IMAGE, COMMANDS)
        );
    }

    @ParameterizedTest
    @MethodSource("provideParameterVariations")
    void constructor_WithVariousParameters_ShouldCreateCorrectInstance(
            UUID jobId, UUID stageExecutionId, String commitHash,
            boolean isLocal, String dockerImage, List<String> commands) {

        // Act
        JobExecutionRequest request = new JobExecutionRequest(
                jobId, stageExecutionId, commitHash, isLocal, dockerImage, commands);

        // Assert
        assertEquals(jobId, request.getJobId(), "Job ID should match");
        assertEquals(stageExecutionId, request.getStageExecutionId(), "Stage execution ID should match");
        assertEquals(commitHash, request.getCommitHash(), "Commit hash should match");
        assertEquals(isLocal, request.isLocal(), "IsLocal flag should match");
        assertEquals(dockerImage, request.getDockerImage(), "Docker image should match");
        assertEquals(commands, request.getCommands(), "Commands list should match");
    }

    @Test
    void getters_ShouldReturnCorrectValues() {
        // Arrange
        JobExecutionRequest request = new JobExecutionRequest(
                JOB_ID, STAGE_EXECUTION_ID, COMMIT_HASH, IS_LOCAL, DOCKER_IMAGE, COMMANDS);

        // Act & Assert
        assertEquals(JOB_ID, request.getJobId());
        assertEquals(STAGE_EXECUTION_ID, request.getStageExecutionId());
        assertEquals(COMMIT_HASH, request.getCommitHash());
        assertEquals(IS_LOCAL, request.isLocal());
        assertEquals(DOCKER_IMAGE, request.getDockerImage());
        assertEquals(COMMANDS, request.getCommands());
    }

    @Test
    void jsonSerialization_ShouldSerializeAndDeserializeCorrectly() throws Exception {
        // Arrange
        JobExecutionRequest originalRequest = new JobExecutionRequest(
                JOB_ID, STAGE_EXECUTION_ID, COMMIT_HASH, IS_LOCAL, DOCKER_IMAGE, COMMANDS);
        ObjectMapper objectMapper = new ObjectMapper();

        // Act
        String json = objectMapper.writeValueAsString(originalRequest);
//        json = json.replace("\"local\":", "\"isLocal\":");
        JobExecutionRequest deserializedRequest = objectMapper.readValue(json, JobExecutionRequest.class);

        // Assert
        assertEquals(JOB_ID, deserializedRequest.getJobId());
        assertEquals(STAGE_EXECUTION_ID, deserializedRequest.getStageExecutionId());
        assertEquals(COMMIT_HASH, deserializedRequest.getCommitHash());
        assertEquals(IS_LOCAL, deserializedRequest.isLocal());
        assertEquals(DOCKER_IMAGE, deserializedRequest.getDockerImage());
        assertEquals(COMMANDS, deserializedRequest.getCommands());
    }

    @Test
    void longCommitHash_ShouldHandleCorrectly() {
        // Arrange
        String longCommitHash = "a1b2c3d4e5f6a1b2c3d4e5f6a1b2c3d4e5f6a1b2";

        // Act
        JobExecutionRequest request = new JobExecutionRequest(
                JOB_ID, STAGE_EXECUTION_ID, longCommitHash, IS_LOCAL, DOCKER_IMAGE, COMMANDS);

        // Assert
        assertEquals(longCommitHash, request.getCommitHash(), "Should handle long commit hash correctly");
    }

    @Test
    void complexDockerImage_ShouldHandleCorrectly() {
        // Arrange
        String complexDockerImage = "registry.example.com:5000/organization/project/java-build:v1.2.3-alpine";

        // Act
        JobExecutionRequest request = new JobExecutionRequest(
                JOB_ID, STAGE_EXECUTION_ID, COMMIT_HASH, IS_LOCAL, complexDockerImage, COMMANDS);

        // Assert
        assertEquals(complexDockerImage, request.getDockerImage(), "Should handle complex Docker image name correctly");
    }

    @Test
    void longCommandList_ShouldHandleCorrectly() {
        // Arrange
        List<String> longCommandList = Arrays.asList(
                "echo 'Starting build'",
                "cd /app",
                "mvn clean",
                "mvn compile",
                "mvn test",
                "mvn package",
                "cp target/*.jar /artifacts/",
                "echo 'Build completed'"
        );

        // Act
        JobExecutionRequest request = new JobExecutionRequest(
                JOB_ID, STAGE_EXECUTION_ID, COMMIT_HASH, IS_LOCAL, DOCKER_IMAGE, longCommandList);

        // Assert
        assertEquals(longCommandList, request.getCommands(), "Should handle long command list correctly");
    }
}