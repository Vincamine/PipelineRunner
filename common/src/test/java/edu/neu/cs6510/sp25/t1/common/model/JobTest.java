package edu.neu.cs6510.sp25.t1.common.model;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link Job}.
 */
class JobTest {

  private static final UUID ID = UUID.randomUUID();
  private static final UUID STAGE_ID = UUID.randomUUID();
  private static final String NAME = "build-job";
  private static final String DOCKER_IMAGE = "maven:3.8.4-openjdk-17";
  private static final List<String> SCRIPT = Arrays.asList("mvn clean", "mvn install");
  private static final List<UUID> DEPENDENCIES = Arrays.asList(UUID.randomUUID(), UUID.randomUUID());
  private static final boolean ALLOW_FAILURE = false;
  private static final List<String> ARTIFACTS = Arrays.asList("target/*.jar", "README.md");
  private static final LocalDateTime CREATED_AT = LocalDateTime.now().minusDays(1);
  private static final LocalDateTime UPDATED_AT = LocalDateTime.now();

  @Test
  void constructor_WithValidParameters_ShouldCreateInstanceWithCorrectValues() {
    // Act
    Job job = new Job(
            ID, STAGE_ID, NAME, DOCKER_IMAGE, SCRIPT, DEPENDENCIES,
            ALLOW_FAILURE, ARTIFACTS, CREATED_AT, UPDATED_AT);

    // Assert
    assertEquals(ID, job.getId(), "ID should match");
    assertEquals(STAGE_ID, job.getStageId(), "Stage ID should match");
    assertEquals(NAME, job.getName(), "Name should match");
    assertEquals(DOCKER_IMAGE, job.getDockerImage(), "Docker image should match");
    assertEquals(SCRIPT, job.getScript(), "Script should match");
    assertEquals(DEPENDENCIES, job.getDependencies(), "Dependencies should match");
    assertEquals(ALLOW_FAILURE, job.isAllowFailure(), "Allow failure should match");
    assertEquals(ARTIFACTS, job.getArtifacts(), "Artifacts should match");
    assertEquals(CREATED_AT, job.getCreatedAt(), "Created at should match");
    assertEquals(UPDATED_AT, job.getUpdatedAt(), "Updated at should match");
  }

  @Test
  void constructor_WithNullLists_ShouldCreateInstanceWithEmptyLists() {
    // Act
    Job job = new Job(
            ID, STAGE_ID, NAME, DOCKER_IMAGE, null, null,
            ALLOW_FAILURE, null, CREATED_AT, UPDATED_AT);

    // Assert
    assertNotNull(job.getScript(), "Script should not be null");
    assertTrue(job.getScript().isEmpty(), "Script should be empty");
    assertNotNull(job.getDependencies(), "Dependencies should not be null");
    assertTrue(job.getDependencies().isEmpty(), "Dependencies should be empty");
    assertNotNull(job.getArtifacts(), "Artifacts should not be null");
    assertTrue(job.getArtifacts().isEmpty(), "Artifacts should be empty");
  }

  @Test
  void constructor_WithNullParameters_ShouldCreateInstanceWithNullValues() {
    // Act
    Job job = new Job(
            null, null, null, null, null, null,
            false, null, null, null);

    // Assert
    assertNull(job.getId(), "ID should be null");
    assertNull(job.getStageId(), "Stage ID should be null");
    assertNull(job.getName(), "Name should be null");
    assertNull(job.getDockerImage(), "Docker image should be null");
    assertNotNull(job.getScript(), "Script should not be null");
    assertNotNull(job.getDependencies(), "Dependencies should not be null");
    assertFalse(job.isAllowFailure(), "Allow failure should be false");
    assertNotNull(job.getArtifacts(), "Artifacts should not be null");
    assertNull(job.getCreatedAt(), "Created at should be null");
    assertNull(job.getUpdatedAt(), "Updated at should be null");
  }

  static Stream<Arguments> provideParameterVariations() {
    return Stream.of(
            // id, stageId, name, dockerImage, script, dependencies, allowFailure, artifacts, createdAt, updatedAt
            Arguments.of(UUID.randomUUID(), UUID.randomUUID(), "", "", Collections.emptyList(), Collections.emptyList(), true, Collections.emptyList(), LocalDateTime.now(), LocalDateTime.now()),
            Arguments.of(UUID.randomUUID(), UUID.randomUUID(), "test-job", null, Arrays.asList("echo test"), null, false, Arrays.asList("*.log"), null, LocalDateTime.now()),
            Arguments.of(UUID.randomUUID(), null, "deploy-job", "node:14", null, Arrays.asList(UUID.randomUUID()), true, null, LocalDateTime.now(), null),
            Arguments.of(null, UUID.randomUUID(), "lint-job", "python:3.9", Arrays.asList("pylint"), Collections.emptyList(), false, Collections.emptyList(), null, null)
    );
  }

  @ParameterizedTest
  @MethodSource("provideParameterVariations")
  void constructor_WithVariousParameters_ShouldCreateCorrectInstance(
          UUID id, UUID stageId, String name, String dockerImage, List<String> script,
          List<UUID> dependencies, boolean allowFailure, List<String> artifacts,
          LocalDateTime createdAt, LocalDateTime updatedAt) {

    // Act
    Job job = new Job(
            id, stageId, name, dockerImage, script, dependencies,
            allowFailure, artifacts, createdAt, updatedAt);

    // Assert
    assertEquals(id, job.getId(), "ID should match");
    assertEquals(stageId, job.getStageId(), "Stage ID should match");
    assertEquals(name, job.getName(), "Name should match");
    assertEquals(dockerImage, job.getDockerImage(), "Docker image should match");

    if (script == null) {
      assertNotNull(job.getScript(), "Script should not be null when input is null");
      assertTrue(job.getScript().isEmpty(), "Script should be empty when input is null");
    } else {
      assertEquals(script, job.getScript(), "Script should match");
    }

    if (dependencies == null) {
      assertNotNull(job.getDependencies(), "Dependencies should not be null when input is null");
      assertTrue(job.getDependencies().isEmpty(), "Dependencies should be empty when input is null");
    } else {
      assertEquals(dependencies, job.getDependencies(), "Dependencies should match");
    }

    assertEquals(allowFailure, job.isAllowFailure(), "Allow failure should match");

    if (artifacts == null) {
      assertNotNull(job.getArtifacts(), "Artifacts should not be null when input is null");
      assertTrue(job.getArtifacts().isEmpty(), "Artifacts should be empty when input is null");
    } else {
      assertEquals(artifacts, job.getArtifacts(), "Artifacts should match");
    }

    assertEquals(createdAt, job.getCreatedAt(), "Created at should match");
    assertEquals(updatedAt, job.getUpdatedAt(), "Updated at should match");
  }

  @Test
  void getters_ShouldReturnCorrectValues() {
    // Arrange
    Job job = new Job(
            ID, STAGE_ID, NAME, DOCKER_IMAGE, SCRIPT, DEPENDENCIES,
            ALLOW_FAILURE, ARTIFACTS, CREATED_AT, UPDATED_AT);

    // Act & Assert
    assertEquals(ID, job.getId());
    assertEquals(STAGE_ID, job.getStageId());
    assertEquals(NAME, job.getName());
    assertEquals(DOCKER_IMAGE, job.getDockerImage());
    assertEquals(SCRIPT, job.getScript());
    assertEquals(DEPENDENCIES, job.getDependencies());
    assertEquals(ALLOW_FAILURE, job.isAllowFailure());
    assertEquals(ARTIFACTS, job.getArtifacts());
    assertEquals(CREATED_AT, job.getCreatedAt());
    assertEquals(UPDATED_AT, job.getUpdatedAt());
  }

  @Test
  void jsonSerialization_ShouldHandleBasicFields() throws Exception {
    // Arrange - create a Job without LocalDateTime fields
    Job jobWithoutDates = new Job(
            ID, STAGE_ID, NAME, DOCKER_IMAGE, SCRIPT, DEPENDENCIES,
            ALLOW_FAILURE, ARTIFACTS, null, null); // null for date fields

    ObjectMapper objectMapper = new ObjectMapper();

    // Act
    String json = objectMapper.writeValueAsString(jobWithoutDates);

    // Assert - check that basic fields are serialized correctly
    assertTrue(json.contains("\"id\":\"" + ID + "\""), "JSON should contain ID");
    assertTrue(json.contains("\"stageId\":\"" + STAGE_ID + "\""), "JSON should contain stage ID");
    assertTrue(json.contains("\"name\":\"" + NAME + "\""), "JSON should contain name");
    assertTrue(json.contains("\"dockerImage\":\"" + DOCKER_IMAGE + "\""), "JSON should contain docker image");
    assertTrue(json.contains("\"allowFailure\":" + ALLOW_FAILURE), "JSON should contain allowFailure flag");
  }

  @Test
  void script_WithEmptyList_ShouldReturnEmptyList() {
    // Arrange
    List<String> emptyScript = Collections.emptyList();
    Job job = new Job(
            ID, STAGE_ID, NAME, DOCKER_IMAGE, emptyScript, DEPENDENCIES,
            ALLOW_FAILURE, ARTIFACTS, CREATED_AT, UPDATED_AT);

    // Act
    List<String> returnedScript = job.getScript();

    // Assert
    assertNotNull(returnedScript, "Script should not be null");
    assertTrue(returnedScript.isEmpty(), "Script should be empty");
  }

  @Test
  void dependencies_WithEmptyList_ShouldReturnEmptyList() {
    // Arrange
    List<UUID> emptyDependencies = Collections.emptyList();
    Job job = new Job(
            ID, STAGE_ID, NAME, DOCKER_IMAGE, SCRIPT, emptyDependencies,
            ALLOW_FAILURE, ARTIFACTS, CREATED_AT, UPDATED_AT);

    // Act
    List<UUID> returnedDependencies = job.getDependencies();

    // Assert
    assertNotNull(returnedDependencies, "Dependencies should not be null");
    assertTrue(returnedDependencies.isEmpty(), "Dependencies should be empty");
  }

  @Test
  void artifacts_WithEmptyList_ShouldReturnEmptyList() {
    // Arrange
    List<String> emptyArtifacts = Collections.emptyList();
    Job job = new Job(
            ID, STAGE_ID, NAME, DOCKER_IMAGE, SCRIPT, DEPENDENCIES,
            ALLOW_FAILURE, emptyArtifacts, CREATED_AT, UPDATED_AT);

    // Act
    List<String> returnedArtifacts = job.getArtifacts();

    // Assert
    assertNotNull(returnedArtifacts, "Artifacts should not be null");
    assertTrue(returnedArtifacts.isEmpty(), "Artifacts should be empty");
  }
}