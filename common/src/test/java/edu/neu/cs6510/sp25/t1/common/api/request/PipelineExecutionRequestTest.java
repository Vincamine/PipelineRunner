package edu.neu.cs6510.sp25.t1.common.api.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Unit tests for {@link PipelineExecutionRequest}.
 */
class PipelineExecutionRequestTest {

    private static final UUID PIPELINE_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    private static final String REPO = "https://github.com/organization/project.git";
    private static final String BRANCH = "main";
    private static final String COMMIT_HASH = "abc123def456";
    private static final boolean IS_LOCAL = true;
    private static final int RUN_NUMBER = 42;

    @Test
    void constructor_WithValidParameters_ShouldCreateInstanceWithCorrectValues() {
        // Act
        PipelineExecutionRequest request = new PipelineExecutionRequest(
                PIPELINE_ID, REPO, BRANCH, COMMIT_HASH, IS_LOCAL, RUN_NUMBER);

        // Assert
        assertEquals(PIPELINE_ID, request.getPipelineId(), "Pipeline ID should match");
        assertEquals(REPO, request.getRepo(), "Repository URL should match");
        assertEquals(BRANCH, request.getBranch(), "Branch should match");
        assertEquals(COMMIT_HASH, request.getCommitHash(), "Commit hash should match");
        assertEquals(IS_LOCAL, request.isLocal(), "IsLocal flag should match");
        assertEquals(RUN_NUMBER, request.getRunNumber(), "Run number should match");
    }

    static Stream<Arguments> provideParameterVariations() {
        return Stream.of(
                // pipelineId, repo, branch, commitHash, isLocal, runNumber
                Arguments.of(null, REPO, BRANCH, COMMIT_HASH, IS_LOCAL, RUN_NUMBER),
                Arguments.of(PIPELINE_ID, null, BRANCH, COMMIT_HASH, IS_LOCAL, RUN_NUMBER),
                Arguments.of(PIPELINE_ID, REPO, null, COMMIT_HASH, IS_LOCAL, RUN_NUMBER),
                Arguments.of(PIPELINE_ID, REPO, BRANCH, null, IS_LOCAL, RUN_NUMBER),
                Arguments.of(PIPELINE_ID, REPO, BRANCH, COMMIT_HASH, !IS_LOCAL, RUN_NUMBER),
                Arguments.of(PIPELINE_ID, REPO, BRANCH, COMMIT_HASH, IS_LOCAL, 0),
                Arguments.of(PIPELINE_ID, "", BRANCH, COMMIT_HASH, IS_LOCAL, RUN_NUMBER),
                Arguments.of(PIPELINE_ID, REPO, "", COMMIT_HASH, IS_LOCAL, RUN_NUMBER),
                Arguments.of(PIPELINE_ID, REPO, BRANCH, "", IS_LOCAL, RUN_NUMBER)
        );
    }

    @ParameterizedTest
    @MethodSource("provideParameterVariations")
    void constructor_WithVariousParameters_ShouldCreateCorrectInstance(
            UUID pipelineId, String repo, String branch,
            String commitHash, boolean isLocal, int runNumber) {

        // Act
        PipelineExecutionRequest request = new PipelineExecutionRequest(
                pipelineId, repo, branch, commitHash, isLocal, runNumber);

        // Assert
        assertEquals(pipelineId, request.getPipelineId(), "Pipeline ID should match");
        assertEquals(repo, request.getRepo(), "Repository URL should match");
        assertEquals(branch, request.getBranch(), "Branch should match");
        assertEquals(commitHash, request.getCommitHash(), "Commit hash should match");
        assertEquals(isLocal, request.isLocal(), "IsLocal flag should match");
        assertEquals(runNumber, request.getRunNumber(), "Run number should match");
    }

    @Test
    void getters_ShouldReturnCorrectValues() {
        // Arrange
        PipelineExecutionRequest request = new PipelineExecutionRequest(
                PIPELINE_ID, REPO, BRANCH, COMMIT_HASH, IS_LOCAL, RUN_NUMBER);

        // Act & Assert
        assertEquals(PIPELINE_ID, request.getPipelineId());
        assertEquals(REPO, request.getRepo());
        assertEquals(BRANCH, request.getBranch());
        assertEquals(COMMIT_HASH, request.getCommitHash());
        assertEquals(IS_LOCAL, request.isLocal());
        assertEquals(RUN_NUMBER, request.getRunNumber());
    }

    @Test
    void jsonSerialization_ShouldSerializeAndDeserializeCorrectly() throws Exception {
        // Arrange
        PipelineExecutionRequest originalRequest = new PipelineExecutionRequest(
                PIPELINE_ID, REPO, BRANCH, COMMIT_HASH, IS_LOCAL, RUN_NUMBER);
        ObjectMapper objectMapper = new ObjectMapper();

        // Act
        String json = objectMapper.writeValueAsString(originalRequest);
//        json = json.replace("\"local\":", "\"isLocal\":");
        PipelineExecutionRequest deserializedRequest = objectMapper.readValue(json, PipelineExecutionRequest.class);

        // Assert
        assertEquals(PIPELINE_ID, deserializedRequest.getPipelineId());
        assertEquals(REPO, deserializedRequest.getRepo());
        assertEquals(BRANCH, deserializedRequest.getBranch());
        assertEquals(COMMIT_HASH, deserializedRequest.getCommitHash());
        assertEquals(IS_LOCAL, deserializedRequest.isLocal());
        assertEquals(RUN_NUMBER, deserializedRequest.getRunNumber());
    }

    @Test
    void largeRunNumber_ShouldHandleCorrectly() {
        // Arrange
        int largeRunNumber = Integer.MAX_VALUE;

        // Act
        PipelineExecutionRequest request = new PipelineExecutionRequest(
                PIPELINE_ID, REPO, BRANCH, COMMIT_HASH, IS_LOCAL, largeRunNumber);

        // Assert
        assertEquals(largeRunNumber, request.getRunNumber(), "Should handle large run number correctly");
    }

    @Test
    void specialRepoUrl_ShouldHandleCorrectly() {
        // Arrange
        String specialRepoUrl = "git@github.com:organization/project-with-special_chars.git";

        // Act
        PipelineExecutionRequest request = new PipelineExecutionRequest(
                PIPELINE_ID, specialRepoUrl, BRANCH, COMMIT_HASH, IS_LOCAL, RUN_NUMBER);

        // Assert
        assertEquals(specialRepoUrl, request.getRepo(), "Should handle special repository URL correctly");
    }

    @Test
    void complexBranchName_ShouldHandleCorrectly() {
        // Arrange
        String complexBranchName = "feature/user-auth/JIRA-1234_implement-oauth";

        // Act
        PipelineExecutionRequest request = new PipelineExecutionRequest(
                PIPELINE_ID, REPO, complexBranchName, COMMIT_HASH, IS_LOCAL, RUN_NUMBER);

        // Assert
        assertEquals(complexBranchName, request.getBranch(), "Should handle complex branch name correctly");
    }
}