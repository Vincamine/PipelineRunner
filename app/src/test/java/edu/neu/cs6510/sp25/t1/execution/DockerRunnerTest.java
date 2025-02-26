package edu.neu.cs6510.sp25.t1.execution;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for DockerRunner using real Docker Desktop.
 */
public class DockerRunnerTest {

  private DockerRunner dockerRunner;
  private static final String TEST_IMAGE = "alpine:latest";

  @BeforeEach
  void setup() {
    dockerRunner = new DockerRunner(TEST_IMAGE);
  }

  @Test
  void testStartContainer_Success() {
    String containerId = dockerRunner.startContainer("echo", "Hello, CI/CD");
    assertNotNull(containerId);
    System.out.println("Container started successfully: " + containerId);
  }
}
