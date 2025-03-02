package edu.neu.cs6510.sp25.t1.common.config;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StageConfigTest {

  @Test
  void testConstructorAndGetters() {
    List<JobConfig> jobs = List.of(new JobConfig("test-job", "build-stage", "ubuntu:latest", List.of("echo Hello"), List.of(), false));
    StageConfig stage = new StageConfig("build-stage", jobs);

    assertEquals("build-stage", stage.getName());
    assertEquals(1, stage.getJobs().size());
  }

  @Test
  void testConstructorWithEmptyJobList() {
    StageConfig stage = new StageConfig("build-stage", List.of());
    assertNotNull(stage.getJobs());
    assertTrue(stage.getJobs().isEmpty());
  }
}
