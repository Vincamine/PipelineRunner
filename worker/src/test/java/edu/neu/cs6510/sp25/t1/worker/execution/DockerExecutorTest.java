package edu.neu.cs6510.sp25.t1.worker.execution;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import edu.neu.cs6510.sp25.t1.common.dto.JobDTO;
import edu.neu.cs6510.sp25.t1.common.dto.JobExecutionDTO;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

/**
 * Tests for DockerExecutor class
 */
@ExtendWith(MockitoExtension.class)
public class DockerExecutorTest {

  @InjectMocks
  private DockerExecutor dockerExecutor;

  private JobExecutionDTO jobExecution;
  private JobDTO job;

  /**
   * Setup before each test
   */
  @BeforeEach
  public void setUp() {
    // Create JobDTO object for testing
    job = new JobDTO();
    job.setDockerImage("test-image");
    job.setScript(Arrays.asList("echo hello", "ls -la"));

    // Create JobExecutionDTO object for testing
    jobExecution = new JobExecutionDTO();
    jobExecution.setJob(job);
  }

  /**
   * Test buildDockerCommand method properly builds Docker commands
   */
  @Test
  public void testBuildDockerCommand() throws Exception {
    // Access private method using reflection
    Method buildDockerCommandMethod = DockerExecutor.class.getDeclaredMethod(
            "buildDockerCommand", String.class, List.class);
    buildDockerCommandMethod.setAccessible(true);

    // Invoke private method
    String[] command = (String[]) buildDockerCommandMethod.invoke(
            dockerExecutor, "test-image", Arrays.asList("echo hello", "ls -la"));

    // Expected command
    String[] expectedCommand = {
            "docker", "run", "--rm",
            "test-image",
            "sh", "-c", "echo hello && ls -la"
    };

    // Verify command is built correctly
    assertArrayEquals(expectedCommand, command, "Docker command not built correctly");
  }

  /**
   * Test handling of empty script list
   */
  @Test
  public void testEmptyScriptList() throws Exception {
    // Access private method using reflection
    Method buildDockerCommandMethod = DockerExecutor.class.getDeclaredMethod(
            "buildDockerCommand", String.class, List.class);
    buildDockerCommandMethod.setAccessible(true);

    // Invoke private method with empty script list
    String[] command = (String[]) buildDockerCommandMethod.invoke(
            dockerExecutor, "test-image", Collections.emptyList());

    // Expected command
    String[] expectedCommand = {
            "docker", "run", "--rm",
            "test-image",
            "sh", "-c", ""
    };

    // Verify command is built correctly
    assertArrayEquals(expectedCommand, command, "Empty script list not handled correctly");
  }

  /**
   * Test buildDockerCommand with single command script
   */
  @Test
  public void testBuildDockerCommandSingleCommand() throws Exception {
    // Access private method using reflection
    Method buildDockerCommandMethod = DockerExecutor.class.getDeclaredMethod(
            "buildDockerCommand", String.class, List.class);
    buildDockerCommandMethod.setAccessible(true);

    // Invoke private method with single command
    String[] command = (String[]) buildDockerCommandMethod.invoke(
            dockerExecutor, "test-image", Collections.singletonList("echo hello"));

    // Expected command
    String[] expectedCommand = {
            "docker", "run", "--rm",
            "test-image",
            "sh", "-c", "echo hello"
    };

    // Verify command is built correctly
    assertArrayEquals(expectedCommand, command, "Single command script not handled correctly");
  }

  /**
   * Test buildDockerCommand with multiple commands
   */
  @Test
  public void testBuildDockerCommandMultipleCommands() throws Exception {
    // Access private method using reflection
    Method buildDockerCommandMethod = DockerExecutor.class.getDeclaredMethod(
            "buildDockerCommand", String.class, List.class);
    buildDockerCommandMethod.setAccessible(true);

    // Invoke private method with multiple commands
    String[] command = (String[]) buildDockerCommandMethod.invoke(
            dockerExecutor, "test-image",
            Arrays.asList("cd /app", "echo hello", "ls -la", "cat file.txt"));

    // Expected command
    String[] expectedCommand = {
            "docker", "run", "--rm",
            "test-image",
            "sh", "-c", "cd /app && echo hello && ls -la && cat file.txt"
    };

    // Verify command is built correctly
    assertArrayEquals(expectedCommand, command, "Multiple commands not handled correctly");
  }

  /**
   * Test buildDockerCommand with different Docker image
   */
  @Test
  public void testBuildDockerCommandDifferentImage() throws Exception {
    // Access private method using reflection
    Method buildDockerCommandMethod = DockerExecutor.class.getDeclaredMethod(
            "buildDockerCommand", String.class, List.class);
    buildDockerCommandMethod.setAccessible(true);

    // Invoke private method with a different Docker image
    String[] command = (String[]) buildDockerCommandMethod.invoke(
            dockerExecutor, "alpine:latest", Arrays.asList("echo hello"));

    // Expected command
    String[] expectedCommand = {
            "docker", "run", "--rm",
            "alpine:latest",
            "sh", "-c", "echo hello"
    };

    // Verify command is built correctly
    assertArrayEquals(expectedCommand, command, "Different Docker image not handled correctly");
  }
}