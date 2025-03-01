package edu.neu.cs6510.sp25.t1.worker.executor;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.StartContainerCmd;
import com.github.dockerjava.api.command.WaitContainerCmd;
import com.github.dockerjava.api.command.WaitContainerResultCallback;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import edu.neu.cs6510.sp25.t1.common.model.execution.JobExecution;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


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
    JobExecution jobExecution = new JobExecution(
            new edu.neu.cs6510.sp25.t1.common.model.definition.JobDefinition(
                    "test-job", "default-stage", "default-image",
                    List.of(), List.of(), false
            ), "RUNNING", false, List.of()
    );

    CreateContainerResponse containerResponse = mock(CreateContainerResponse.class);
    when(containerResponse.getId()).thenReturn("container123");

    var createContainerCmd = mock(com.github.dockerjava.api.command.CreateContainerCmd.class);
    when(dockerClient.createContainerCmd(anyString())).thenReturn(createContainerCmd);
    when(createContainerCmd.withImage(anyString())).thenReturn(createContainerCmd);
    when(createContainerCmd.withHostConfig(any())).thenReturn(createContainerCmd);
    when(createContainerCmd.withCmd(anyString())).thenReturn(createContainerCmd);
    when(createContainerCmd.exec()).thenReturn(containerResponse);

    StartContainerCmd startCmd = mock(StartContainerCmd.class);
    when(dockerClient.startContainerCmd(anyString())).thenReturn(startCmd);
    doNothing().when(startCmd).exec();

    String containerId = dockerManager.runContainer(jobExecution);

    assertNotNull(containerId);
    assertEquals("container123", containerId);
    verify(dockerClient, times(1)).createContainerCmd("test-job");
    verify(createContainerCmd, times(1)).withImage("test-job");
    verify(createContainerCmd, times(1)).withHostConfig(any());
    verify(createContainerCmd, times(1)).withCmd("test-job");
    verify(createContainerCmd, times(1)).exec();
    verify(dockerClient, times(1)).startContainerCmd("container123");
  }

  @Test
  void testRunContainer_Failure() {
    JobExecution jobExecution = new JobExecution(
            new edu.neu.cs6510.sp25.t1.common.model.definition.JobDefinition(
                    "test-job", "default-stage", "default-image",
                    List.of(), List.of(), false
            ), "RUNNING", false, List.of()
    );

    when(dockerClient.createContainerCmd(anyString())).thenThrow(new RuntimeException("Docker failure"));

    String containerId = dockerManager.runContainer(jobExecution);

    assertNull(containerId);
    verify(dockerClient, times(1)).createContainerCmd("test-job");
  }

  @Test
  void testStartContainer_Success() {
    CreateContainerResponse containerResponse = mock(CreateContainerResponse.class);
    when(containerResponse.getId()).thenReturn("container456");

    var createContainerCmd = mock(com.github.dockerjava.api.command.CreateContainerCmd.class);
    when(dockerClient.createContainerCmd(anyString())).thenReturn(createContainerCmd);
    when(createContainerCmd.withHostConfig(any())).thenReturn(createContainerCmd);
    when(createContainerCmd.exec()).thenReturn(containerResponse);

    StartContainerCmd startCmd = mock(StartContainerCmd.class);
    when(dockerClient.startContainerCmd(anyString())).thenReturn(startCmd);
    doNothing().when(startCmd).exec();

    String containerId = dockerManager.startContainer("my-image");

    assertNotNull(containerId);
    assertEquals("container456", containerId);
    verify(dockerClient, times(1)).createContainerCmd("my-image");
    verify(createContainerCmd, times(1)).withHostConfig(any());
    verify(createContainerCmd, times(1)).exec();
    verify(dockerClient, times(1)).startContainerCmd("container456");
  }

  @Test
  void testWaitForContainer_Success() throws Exception {
    WaitContainerCmd waitCmd = mock(WaitContainerCmd.class);
    WaitContainerResultCallback callback = mock(WaitContainerResultCallback.class);

    when(dockerClient.waitContainerCmd(anyString())).thenReturn(waitCmd);
    when(waitCmd.exec(any())).thenReturn(callback);
    when(callback.awaitCompletion()).thenReturn(callback); // ✅ FIXED `doNothing()` issue

    boolean result = dockerManager.waitForContainer("container123");

    assertTrue(result);
    verify(dockerClient, times(1)).waitContainerCmd("container123");
  }

  @Test
  void testWaitForContainer_Failure() throws Exception {
    WaitContainerCmd waitCmd = mock(WaitContainerCmd.class);
    when(dockerClient.waitContainerCmd(anyString())).thenReturn(waitCmd);
    when(waitCmd.exec(any())).thenThrow(new RuntimeException("Wait error"));

    boolean result = dockerManager.waitForContainer("container123");

    assertFalse(result);
    verify(dockerClient, times(1)).waitContainerCmd("container123");
  }

  @Test
  void testCleanupContainer_Success() {
    dockerManager.cleanupContainer("container789");
    verify(dockerClient, times(1)).removeContainerCmd("container789");
  }

  @Test
  void testCleanupContainer_Failure() {
    doThrow(new RuntimeException("Cleanup error")).when(dockerClient).removeContainerCmd(anyString());

    dockerManager.cleanupContainer("container789");

    verify(dockerClient, times(1)).removeContainerCmd("container789");
  }
}
