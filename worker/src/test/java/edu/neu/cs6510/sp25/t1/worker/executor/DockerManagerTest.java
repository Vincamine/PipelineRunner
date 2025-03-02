package edu.neu.cs6510.sp25.t1.worker.executor;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.RemoveContainerCmd;
import com.github.dockerjava.api.command.StartContainerCmd;
import com.github.dockerjava.api.command.WaitContainerCmd;
import com.github.dockerjava.api.command.WaitContainerResultCallback;
import com.github.dockerjava.api.model.HostConfig;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import edu.neu.cs6510.sp25.t1.common.config.JobConfig;
import edu.neu.cs6510.sp25.t1.common.runtime.JobRunState;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DockerManagerTest {

  @Mock
  private DockerClient dockerClient;
  @Mock
  private CreateContainerCmd createContainerCmd;
  @Mock
  private StartContainerCmd startContainerCmd;
  @Mock
  private RemoveContainerCmd removeContainerCmd;
  @Mock
  private WaitContainerResultCallback waitCallback;

  @InjectMocks
  private DockerManager dockerManager;

  private JobRunState jobRunState;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    jobRunState = new JobRunState(
            new JobConfig("test-job", "default-stage", "default-image",
                    List.of(), List.of(), false),
            "PENDING",
            false,
            List.of()
    );

    CreateContainerResponse containerResponse = mock(CreateContainerResponse.class);
    when(containerResponse.getId()).thenReturn("container-123");

    when(dockerClient.createContainerCmd(anyString())).thenReturn(createContainerCmd);
    when(createContainerCmd.withImage(anyString())).thenReturn(createContainerCmd);
    when(createContainerCmd.withHostConfig(any(HostConfig.class))).thenReturn(createContainerCmd);
    when(createContainerCmd.exec()).thenReturn(containerResponse);

    when(dockerClient.startContainerCmd("container-123")).thenReturn(startContainerCmd);
    when(dockerClient.removeContainerCmd("container-123")).thenReturn(removeContainerCmd);
  }

  @Test
  void testRunContainer_Success() {
    String containerId = dockerManager.runContainer(jobRunState);
    assertEquals("container-123", containerId);
    verify(dockerClient).startContainerCmd("container-123");
  }

  @Test
  void testRunContainer_Failure() {
    when(createContainerCmd.exec()).thenThrow(new RuntimeException("Docker failure"));
    String containerId = dockerManager.runContainer(jobRunState);
    assertNull(containerId);
  }

  @Test
  void testStartContainer_Success() {
    CreateContainerResponse containerResponse = mock(CreateContainerResponse.class);
    when(containerResponse.getId()).thenReturn("container-456");

    when(dockerClient.createContainerCmd(anyString())).thenReturn(createContainerCmd);
    when(createContainerCmd.withHostConfig(any(HostConfig.class))).thenReturn(createContainerCmd);
    when(createContainerCmd.exec()).thenReturn(containerResponse);

    when(dockerClient.startContainerCmd(anyString())).thenReturn(startContainerCmd);
    doNothing().when(startContainerCmd).exec();

    String containerId = dockerManager.startContainer("test-image");
    assertEquals("container-456", containerId);
  }

  @Test
  void testStartContainer_Failure_NullContainer() {
    when(createContainerCmd.exec()).thenReturn(null);
    String containerId = dockerManager.startContainer("test-image");
    assertNull(containerId);
  }

  @Test
  void testStartContainer_Failure_NullStartCmd() {
    CreateContainerResponse containerResponse = mock(CreateContainerResponse.class);
    when(containerResponse.getId()).thenReturn("container-789");
    when(createContainerCmd.exec()).thenReturn(containerResponse);

    when(dockerClient.startContainerCmd(anyString())).thenReturn(null); // Force failure

    String containerId = dockerManager.startContainer("test-image");
    assertNull(containerId);
  }

  @Test
  void testWaitForContainer_Success() throws Exception {
    when(dockerClient.waitContainerCmd(anyString())).thenReturn(mock(WaitContainerCmd.class));
    when(dockerClient.waitContainerCmd(anyString()).exec(any(WaitContainerResultCallback.class)))
            .thenReturn(waitCallback);

    boolean result = dockerManager.waitForContainer("container-123");
    assertTrue(result);
  }

  @Test
  void testWaitForContainer_Failure() throws Exception {
    when(dockerClient.waitContainerCmd(anyString())).thenThrow(new RuntimeException("Error"));
    boolean result = dockerManager.waitForContainer("container-123");
    assertFalse(result);
  }

  @Test
  void testCleanupContainer_Success() {
    dockerManager.cleanupContainer("container-123");
    verify(dockerClient).removeContainerCmd("container-123");
  }

  @Test
  void testCleanupContainer_Failure() {
    doThrow(new RuntimeException("Docker error")).when(dockerClient).removeContainerCmd(anyString());
    dockerManager.cleanupContainer("container-123");
    verify(dockerClient).removeContainerCmd("container-123");
  }
}
