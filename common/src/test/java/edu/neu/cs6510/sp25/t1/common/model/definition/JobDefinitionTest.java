package edu.neu.cs6510.sp25.t1.common.model.definition;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

class JobDefinitionTest {

  @Test
  void testConstructorAndGetters() {
    List<String> script = List.of("echo Hello", "mvn test");
    List<String> needs = List.of("build");

    JobDefinition job = new JobDefinition("test-job", "build-stage", "ubuntu:latest", script, needs, true);

    assertEquals("test-job", job.getName());
    assertEquals("build-stage", job.getStageName());
    assertEquals("ubuntu:latest", job.getImage());
    assertEquals(script, job.getScript());
    assertEquals(needs, job.getNeeds());
    assertTrue(job.isAllowFailure());
  }

  @Test
  void testConstructorWithNullDependencies() {
    JobDefinition job = new JobDefinition("test-job", "build-stage", "ubuntu:latest", List.of(), null, false);
    assertNotNull(job.getNeeds());
    assertTrue(job.getNeeds().isEmpty());
  }
}
