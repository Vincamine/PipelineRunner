package edu.neu.cs6510.sp25.t1.executor;

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

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class DockerManagerTest {

    private DockerManager dockerManager;
    private DockerClient dockerClient;

    @BeforeEach
    void setUp() {
        dockerClient = mock(DockerClient.class);
        dockerManager = new DockerManager(dockerClient);
    }

    @Test
    void testStartContainer_Success() {
        CreateContainerResponse mockResponse = new CreateContainerResponse();
        mockResponse.setId("container123");

        CreateContainerCmd createContainerCmd = mock(CreateContainerCmd.class);
        when(dockerClient.createContainerCmd(anyString())).thenReturn(createContainerCmd);
        when(createContainerCmd.withHostConfig(any(HostConfig.class))).thenReturn(createContainerCmd);
        when(createContainerCmd.exec()).thenReturn(mockResponse);

        StartContainerCmd startContainerCmd = mock(StartContainerCmd.class);
        when(dockerClient.startContainerCmd("container123")).thenReturn(startContainerCmd);

        String containerId = dockerManager.startContainer("test-image");
        assertEquals("container123", containerId);
        verify(dockerClient).startContainerCmd("container123");
    }

    @Test
    void testWaitForContainer_Success() throws Exception {
        WaitContainerCmd waitContainerCmd = mock(WaitContainerCmd.class);
        WaitContainerResultCallback callback = mock(WaitContainerResultCallback.class);
        
        when(dockerClient.waitContainerCmd(anyString())).thenReturn(waitContainerCmd);
        when(waitContainerCmd.exec(any())).thenReturn(callback);

        boolean result = dockerManager.waitForContainer("container123");
        assertTrue(result);
    }

    @Test
    void testCleanupContainer_Success() {
        RemoveContainerCmd removeContainerCmd = mock(RemoveContainerCmd.class);
        when(dockerClient.removeContainerCmd(anyString())).thenReturn(removeContainerCmd);

        dockerManager.cleanupContainer("container123");

        verify(removeContainerCmd).exec();
    }
}
