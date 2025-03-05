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
 * Unit tests for {@link PipelineExecutionRequest}.
 */
class PipelineExecutionRequestTest {

  private static final String REPO = "https://github.com/username/repo.git";
  private static final String BRANCH = "main";
  private static final String COMMIT = "abc123def456";
  private static final String PIPELINE = "build-test-deploy";

  @Test
  void constructor_WithValidParameters_ShouldCreateInstanceWithCorrectValues() {
    // Act
    PipelineExecutionRequest request = new PipelineExecutionRequest(
            REPO, BRANCH, COMMIT, PIPELINE);

    // Assert
    assertEquals(REPO, request.getRepo(), "Repository URL should match");
    assertEquals(BRANCH, request.getBranch(), "Branch should match");
    assertEquals(COMMIT, request.getCommit(), "Commit hash should match");
    assertEquals(PIPELINE, request.getPipeline(), "Pipeline name should match");
  }

  @Test
  void constructor_WithNullParameters_ShouldCreateInstanceWithNullValues() {
    // Act
    PipelineExecutionRequest request = new PipelineExecutionRequest(
            null, null, null, null);

    // Assert
    assertNull(request.getRepo(), "Repository URL should be null");
    assertNull(request.getBranch(), "Branch should be null");
    assertNull(request.getCommit(), "Commit hash should be null");
    assertNull(request.getPipeline(), "Pipeline name should be null");
  }

  static Stream<Arguments> provideParameterVariations() {
    return Stream.of(
            // repo, branch, commit, pipeline
            Arguments.of("", "", "", ""),
            Arguments.of("https://github.com/user/project.git", null, "abcdef", "build"),
            Arguments.of(null, "feature/new", null, "test"),
            Arguments.of("https://gitlab.com/org/repo.git", "master", "12345", ""),
            Arguments.of("ssh://git@github.com:user/repo.git", "", "abc123", null)
    );
  }

  @ParameterizedTest
  @MethodSource("provideParameterVariations")
  void constructor_WithVariousParameters_ShouldCreateCorrectInstance(
          String repo, String branch, String commit, String pipeline) {

    // Act
    PipelineExecutionRequest request = new PipelineExecutionRequest(
            repo, branch, commit, pipeline);

    // Assert
    assertEquals(repo, request.getRepo(), "Repository URL should match");
    assertEquals(branch, request.getBranch(), "Branch should match");
    assertEquals(commit, request.getCommit(), "Commit hash should match");
    assertEquals(pipeline, request.getPipeline(), "Pipeline name should match");
  }

  @Test
  void getters_ShouldReturnCorrectValues() {
    // Arrange
    PipelineExecutionRequest request = new PipelineExecutionRequest(
            REPO, BRANCH, COMMIT, PIPELINE);

    // Act & Assert
    assertEquals(REPO, request.getRepo());
    assertEquals(BRANCH, request.getBranch());
    assertEquals(COMMIT, request.getCommit());
    assertEquals(PIPELINE, request.getPipeline());
    // Testing each getter individually ensures complete method coverage
  }

  @Test
  void jsonSerialization_ShouldSerializeAndDeserializeCorrectly() throws Exception {
    // Arrange
    PipelineExecutionRequest originalRequest = new PipelineExecutionRequest(
            REPO, BRANCH, COMMIT, PIPELINE);
    ObjectMapper objectMapper = new ObjectMapper();

    // Act
    String json = objectMapper.writeValueAsString(originalRequest);
    PipelineExecutionRequest deserializedRequest = objectMapper.readValue(json, PipelineExecutionRequest.class);

    // Assert
    assertEquals(REPO, deserializedRequest.getRepo());
    assertEquals(BRANCH, deserializedRequest.getBranch());
    assertEquals(COMMIT, deserializedRequest.getCommit());
    assertEquals(PIPELINE, deserializedRequest.getPipeline());
  }

  @Test
  void repo_WithComplexUrl_ShouldHandleCorrectly() {
    // Arrange
    String complexRepo = "git://user:password@github.com:8080/organization/repository.git?query=param#fragment";

    // Act
    PipelineExecutionRequest request = new PipelineExecutionRequest(
            complexRepo, BRANCH, COMMIT, PIPELINE);

    // Assert
    assertEquals(complexRepo, request.getRepo(), "Should handle complex repository URL");
  }

  @Test
  void branch_WithSpecialCharacters_ShouldHandleCorrectly() {
    // Arrange
    String specialBranch = "feature/user-auth+v1.2_testing";

    // Act
    PipelineExecutionRequest request = new PipelineExecutionRequest(
            REPO, specialBranch, COMMIT, PIPELINE);

    // Assert
    assertEquals(specialBranch, request.getBranch(), "Should handle special characters in branch name");
  }

  @Test
  void commit_WithLongHash_ShouldHandleCorrectly() {
    // Arrange
    String longCommitHash = "0123456789abcdef0123456789abcdef01234567";

    // Act
    PipelineExecutionRequest request = new PipelineExecutionRequest(
            REPO, BRANCH, longCommitHash, PIPELINE);

    // Assert
    assertEquals(longCommitHash, request.getCommit(), "Should handle long commit hash");
  }
}