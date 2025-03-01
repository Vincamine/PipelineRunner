package edu.neu.cs6510.sp25.t1.worker.executor;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.StartContainerCmd;

import edu.neu.cs6510.sp25.t1.common.model.execution.JobExecution;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class DockerManagerTest {
  private DockerManager dockerManager;
  private DockerClient dockerClient;

  @BeforeEach
  void setUp() {
    dockerClient = mock(DockerClient.class);
    dockerManager = new DockerManager(dockerClient);
  }

  @Test
  void testRunContainer_Success() {
    JobExecution jobExecution = mock(JobExecution.class);
    when(jobExecution.getJobName()).thenReturn("test-job");

    // Mocking the CreateContainerCmd chain properly
    CreateContainerResponse containerResponse = mock(CreateContainerResponse.class);
    when(containerResponse.getId()).thenReturn("container123");

    var createContainerCmd = mock(com.github.dockerjava.api.command.CreateContainerCmd.class);
    when(dockerClient.createContainerCmd(anyString())).thenReturn(createContainerCmd);
    when(createContainerCmd.withImage(anyString())).thenReturn(createContainerCmd);
    when(createContainerCmd.withHostConfig(any())).thenReturn(createContainerCmd);
    when(createContainerCmd.withCmd(anyString())).thenReturn(createContainerCmd);
    when(createContainerCmd.exec()).thenReturn(containerResponse);

    // Mock StartContainerCmd
    StartContainerCmd startCmd = mock(StartContainerCmd.class);
    when(dockerClient.startContainerCmd(anyString())).thenReturn(startCmd);
    doNothing().when(startCmd).exec(); // Ensures exec() doesn't cause issues

    // Execute method
    String containerId = dockerManager.runContainer(jobExecution);

    // Assertions
    assertNotNull(containerId);
    verify(dockerClient, times(1)).createContainerCmd("test-job");
    verify(createContainerCmd, times(1)).withImage("test-job");
    verify(createContainerCmd, times(1)).withHostConfig(any());
    verify(createContainerCmd, times(1)).withCmd("test-job");
    verify(createContainerCmd, times(1)).exec();
    verify(dockerClient, times(1)).startContainerCmd("container123");
  }


  @Test
  void testCleanupContainer_Success() {
    dockerManager.cleanupContainer("container123");
    verify(dockerClient, times(1)).removeContainerCmd("container123");
  }
}
