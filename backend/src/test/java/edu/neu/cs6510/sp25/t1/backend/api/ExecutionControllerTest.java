package edu.neu.cs6510.sp25.t1.backend.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import edu.neu.cs6510.sp25.t1.backend.api.controller.ExecutionController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;


class ExecutionControllerTest {

  private ExecutionController executionController;

  @TempDir
  private Path tempDir; // Temporary directory for test files

  @BeforeEach
  void setUp() {
    executionController = new ExecutionController();
  }

  @Test
  void testGetExecutionLogs_Success() throws IOException {
    // Create a temporary log file
    Path logFile = tempDir.resolve("job-executions.log");
    Files.write(logFile, List.of("Log entry 1", "Log entry 2"));

    // Mock static call to Files.readAllLines
    try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
      mockedFiles.when(() -> Files.readAllLines(any())).thenReturn(List.of("Log entry 1", "Log entry 2"));

      ResponseEntity<List<String>> response = executionController.getExecutionLogs();

      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertNotNull(response.getBody());
      assertEquals(2, response.getBody().size());
      assertEquals("Log entry 1", response.getBody().getFirst());
    }
  }

  @Test
  void testGetExecutionLogs_FileNotFound() {
    // Mock static call to Files.readAllLines to throw IOException
    try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
      mockedFiles.when(() -> Files.readAllLines(any())).thenThrow(new IOException("File not found"));

      ResponseEntity<List<String>> response = executionController.getExecutionLogs();

      assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
      assertNotNull(response.getBody());
      assertEquals(1, response.getBody().size());
      assertEquals("Error reading logs", response.getBody().getFirst());
    }
  }
}
