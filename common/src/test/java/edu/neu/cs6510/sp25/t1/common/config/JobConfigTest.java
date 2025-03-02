package edu.neu.cs6510.sp25.t1.common.config;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JobConfigTest {

  @Test
  void testConstructorAndGetters() {
    List<String> script = List.of("echo Hello", "mvn test");
    List<String> needs = List.of("build");

    JobConfig job = new JobConfig("test-job", "build-stage", "ubuntu:latest", script, needs, true);

    assertEquals("test-job", job.getName());
    assertEquals("build-stage", job.getStageName());
    assertEquals("ubuntu:latest", job.getImage());
    assertEquals(script, job.getScript());
    assertEquals(needs, job.getNeeds());
    assertTrue(job.isAllowFailure());
  }

  @Test
  void testConstructorWithNullDependencies() {
    JobConfig job = new JobConfig("test-job", "build-stage", "ubuntu:latest", List.of(), null, false);
    assertNotNull(job.getNeeds());
    assertTrue(job.getNeeds().isEmpty());
  }
}
