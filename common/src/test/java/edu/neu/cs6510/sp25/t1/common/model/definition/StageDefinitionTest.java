package edu.neu.cs6510.sp25.t1.common.model.definition;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

class StageDefinitionTest {

  @Test
  void testConstructorAndGetters() {
    List<JobDefinition> jobs = List.of(new JobDefinition("test-job", "build-stage", "ubuntu:latest", List.of("echo Hello"), List.of(), false));
    StageDefinition stage = new StageDefinition("build-stage", jobs);

    assertEquals("build-stage", stage.getName());
    assertEquals(1, stage.getJobs().size());
  }

  @Test
  void testConstructorWithEmptyJobList() {
    StageDefinition stage = new StageDefinition("build-stage", List.of());
    assertNotNull(stage.getJobs());
    assertTrue(stage.getJobs().isEmpty());
  }
}
