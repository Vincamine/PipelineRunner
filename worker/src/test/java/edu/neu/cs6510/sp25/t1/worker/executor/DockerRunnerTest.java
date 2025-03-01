package edu.neu.cs6510.sp25.t1.worker.executor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class DockerRunnerTest {
  private DockerRunner dockerRunner;
  private Process mockProcess;

  @BeforeEach
  void setUp() {
    dockerRunner = new DockerRunner("test-image", List.of("echo Hello"));
    mockProcess = mock(Process.class);
  }

  @Test
  void testRun_Success() throws IOException, InterruptedException {
    ProcessBuilder mockBuilder = mock(ProcessBuilder.class);
    when(mockBuilder.start()).thenReturn(mockProcess);
    when(mockProcess.waitFor()).thenReturn(0); // Simulate success

    DockerRunner spyRunner = Mockito.spy(dockerRunner);
    doReturn(mockBuilder).when(spyRunner).createProcessBuilder(); // ✅ Fix Mocking

    spyRunner.run(); // Execute

    verify(mockProcess, times(1)).waitFor();
  }

  @Test
  void testRun_ProcessFails() throws IOException, InterruptedException {
    ProcessBuilder mockBuilder = mock(ProcessBuilder.class);
    when(mockBuilder.start()).thenReturn(mockProcess);
    when(mockProcess.waitFor()).thenReturn(1); // Simulate failure

    DockerRunner spyRunner = Mockito.spy(dockerRunner);
    doReturn(mockBuilder).when(spyRunner).createProcessBuilder();

    spyRunner.run();

    verify(mockProcess, times(1)).waitFor();
  }

  @Test
  void testRun_ThrowsIOException() throws IOException {
    ProcessBuilder mockBuilder = mock(ProcessBuilder.class);
    when(mockBuilder.start()).thenThrow(new IOException("Docker error"));

    DockerRunner spyRunner = Mockito.spy(dockerRunner);
    doReturn(mockBuilder).when(spyRunner).createProcessBuilder();

    assertThrows(IOException.class, spyRunner::run);
  }

  @Test
  void testRun_InterruptedException() throws IOException, InterruptedException {
    ProcessBuilder mockBuilder = mock(ProcessBuilder.class);
    when(mockBuilder.start()).thenReturn(mockProcess);
    doThrow(new InterruptedException("Interrupted")).when(mockProcess).waitFor();

    DockerRunner spyRunner = Mockito.spy(dockerRunner);
    doReturn(mockBuilder).when(spyRunner).createProcessBuilder();

    assertThrows(InterruptedException.class, spyRunner::run);
  }
}
