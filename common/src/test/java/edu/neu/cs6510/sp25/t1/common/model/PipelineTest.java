package edu.neu.cs6510.sp25.t1.common.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PipelineTest {

  private Pipeline pipeline;
  private UUID id;
  private String name;
  private String repoUrl;
  private String branch;
  private String commitHash;
  private List<Stage> stages;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  @BeforeEach
  void setUp() {
    id = UUID.randomUUID();
    name = "Sample Pipeline";
    repoUrl = "https://github.com/sample/repo";
    branch = "dev";
    commitHash = "abcd1234";
    createdAt = LocalDateTime.now().minusDays(1);
    updatedAt = LocalDateTime.now();

    // Create valid Stage instances
    UUID stageId1 = UUID.randomUUID();
    UUID stageId2 = UUID.randomUUID();
    UUID pipelineId = id;
    int executionOrder1 = 1;
    int executionOrder2 = 2;
    List<Job> jobs = List.of(); // Assuming Job class exists, using an empty list
    LocalDateTime stageCreatedAt = LocalDateTime.now().minusDays(2);
    LocalDateTime stageUpdatedAt = LocalDateTime.now().minusDays(1);

    Stage stage1 = new Stage(stageId1, "Build", pipelineId, executionOrder1, jobs, stageCreatedAt, stageUpdatedAt);
    Stage stage2 = new Stage(stageId2, "Deploy", pipelineId, executionOrder2, jobs, stageCreatedAt, stageUpdatedAt);
    stages = List.of(stage1, stage2);

    pipeline = new Pipeline(id, name, repoUrl, branch, commitHash, stages, createdAt, updatedAt);
  }

  @Test
  void testConstructor() {
    assertNotNull(pipeline.getId());
    assertEquals(id, pipeline.getId());
    assertEquals(name, pipeline.getName());
    assertEquals(repoUrl, pipeline.getRepoUrl());
    assertEquals(branch, pipeline.getBranch());
    assertEquals(commitHash, pipeline.getCommitHash());
    assertEquals(stages, pipeline.getStages());
    assertEquals(createdAt, pipeline.getCreatedAt());
    assertEquals(updatedAt, pipeline.getUpdatedAt());
  }

  @Test
  void testBranchDefaultsToMainWhenNull() {
    Pipeline defaultPipeline = new Pipeline(id, name, repoUrl, null, commitHash, stages, createdAt, updatedAt);
    assertEquals("main", defaultPipeline.getBranch());
  }

  @Test
  void testStagesDefaultsToEmptyListWhenNull() {
    Pipeline noStagesPipeline = new Pipeline(id, name, repoUrl, branch, commitHash, null, createdAt, updatedAt);
    assertNotNull(noStagesPipeline.getStages());
    assertTrue(noStagesPipeline.getStages().isEmpty());
  }

  @Test
  void testGetters() {
    assertEquals(id, pipeline.getId());
    assertEquals(name, pipeline.getName());
    assertEquals(repoUrl, pipeline.getRepoUrl());
    assertEquals(branch, pipeline.getBranch());
    assertEquals(commitHash, pipeline.getCommitHash());
    assertEquals(stages, pipeline.getStages());
    assertEquals(createdAt, pipeline.getCreatedAt());
    assertEquals(updatedAt, pipeline.getUpdatedAt());
  }
}
