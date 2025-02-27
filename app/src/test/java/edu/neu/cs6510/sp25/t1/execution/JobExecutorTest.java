package edu.neu.cs6510.sp25.t1.execution;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.exception.DockerException;
import com.github.dockerjava.core.DockerClientBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JobExecutorTest {

  private DockerClient dockerClient;
  private static final String JOB_NAME = "test-job";
  private static final String DOCKER_IMAGE = "alpine:latest";
  private static final String[] SCRIPT = {"echo", "Hello, CI/CD"};
  private JobExecutor jobExecutor;

  @BeforeEach
  void setup() {
//    dockerClient = DockerClientBuilder.getInstance("tcp://localhost:2375").build(); // Default: Windows Docker API
    // For macOS/Linux, use:
    dockerClient = DockerClientBuilder.getInstance("unix:///var/run/docker.sock").build();
    jobExecutor = new JobExecutor(JOB_NAME, DOCKER_IMAGE, SCRIPT, dockerClient);
  }

  // @Test
  // void testExecute_Success() {
  //   jobExecutor.execute();
  //   assertEquals(ExecutionStatus.SUCCESSFUL, jobExecutor.getStatus());
  //   System.out.println("Job executed successfully.");
  // }

  @Test
  void testExecute_NonExistentImage() {
    // Use a non-existent Docker image to simulate failure
    JobExecutor nonExistentImageJob = new JobExecutor(JOB_NAME, "nonexistent-image:latest", SCRIPT, dockerClient);
    nonExistentImageJob.execute();
    assertEquals(ExecutionStatus.FAILED, nonExistentImageJob.getStatus());
    System.out.println("Job execution failed due to non-existent image as expected.");
  }

  @AfterEach
  void cleanup() {
    // use docker events --filter event=create to monitor docker created logs
    // Cleanup Docker resources if necessary
    System.out.println("Test completed.");
  }
}
