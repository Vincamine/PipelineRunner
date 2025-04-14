package edu.neu.cs6510.sp25.t1.cli.utils;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateVolumeResponse;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.transport.DockerHttpClient;
import edu.neu.cs6510.sp25.t1.common.logging.PipelineLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DockerVolumeUtilTest {

    @TempDir
    Path tempDir;

    private File testFile;

    @BeforeEach
    void setUp() throws Exception {
        Path nestedPath = tempDir.resolve("a/b/input.yaml");
        nestedPath.toFile().getParentFile().mkdirs();
        assertTrue(nestedPath.toFile().createNewFile());
        testFile = nestedPath.toFile();
    }

    @Test
    void testCreateVolumeFromHostDirSuccess() throws Exception {
        UUID fakeUUID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        try (
                MockedStatic<UUID> uuidMock = mockStatic(UUID.class);
                MockedStatic<PipelineLogger> loggerMock = mockStatic(PipelineLogger.class);
                MockedStatic<DockerClientImpl> implMock = mockStatic(DockerClientImpl.class)
        ) {
            // Mock UUID
            uuidMock.when(UUID::randomUUID).thenReturn(fakeUUID);

            // Mock DockerClient
            DockerClient mockDockerClient = mock(DockerClient.class);
            implMock.when(() -> DockerClientImpl.getInstance(any(), any())).thenReturn(mockDockerClient);

            // Simulate volume not found initially
            doThrow(new RuntimeException("volume not found"))
                    .when(mockDockerClient).inspectVolumeCmd(anyString());

            // Mock createVolumeCmd chain
            var createVolumeCmd = mock(com.github.dockerjava.api.command.CreateVolumeCmd.class);
            when(mockDockerClient.createVolumeCmd()).thenReturn(createVolumeCmd);
            when(createVolumeCmd.withName(anyString())).thenReturn(createVolumeCmd);
            when(createVolumeCmd.withDriver(anyString())).thenReturn(createVolumeCmd);
            when(createVolumeCmd.exec()).thenReturn(mock(CreateVolumeResponse.class));

            // Mock ProcessBuilder and Process
            Process mockProcess = mock(Process.class);
            when(mockProcess.waitFor()).thenReturn(0); // Simulate success

            // Mock ProcessBuilder.start() using a spy
            ProcessBuilder realBuilder = spy(new ProcessBuilder("echo", "fake"));
            doReturn(mockProcess).when(realBuilder).start();

            // 🧪 Execute
            String result = DockerVolumeUtil.createVolumeFromHostDir(testFile.getAbsolutePath());

            // ✅ Assert result contains our UUID
            assertNotNull(result);
            assertTrue(result.contains(fakeUUID.toString()));
        }
    }

}
