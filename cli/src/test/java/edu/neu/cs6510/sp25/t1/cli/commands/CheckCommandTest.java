package edu.neu.cs6510.sp25.t1.cli.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.List;

import edu.neu.cs6510.sp25.t1.cli.api.CliBackendClient;
import edu.neu.cs6510.sp25.t1.common.api.PipelineCheckResponse;
import picocli.CommandLine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


class CheckCommandTest {
  private CliBackendClient mockBackendClient;
  private CheckCommand checkCommand;
  private String validPipelinePath;
  private String invalidPipelinePath;

  @BeforeEach
  void setUp() throws URISyntaxException {
    mockBackendClient = mock(CliBackendClient.class);
    checkCommand = new CheckCommand(mockBackendClient);

    validPipelinePath = Paths.get(getClass().getClassLoader().getResource(".pipelines/pipeline.yaml").toURI()).toString();
    invalidPipelinePath = Paths.get(getClass().getClassLoader().getResource(".pipelines/invalid_pipeline.yaml").toURI()).toString();
  }

  // Test case: No pipeline file provided (null)
  @Test
  void testCheckCommand_NoPipelineFile() {
    CommandLine cmd = new CommandLine(checkCommand);
    int exitCode = cmd.execute();  // No file argument

    assertEquals(2, exitCode, "Expected exit code 2 for missing pipeline file.");
  }

  // Test case: Empty pipeline file path
  @Test
  void testCheckCommand_EmptyPipelineFile() {
    CommandLine cmd = new CommandLine(checkCommand);
    int exitCode = cmd.execute("");  // Empty string argument

    assertEquals(2, exitCode, "Expected exit code 2 for empty pipeline file path.");
  }

  // Test case: Valid pipeline file (Success case)
  @Test
  void testCheckValidPipeline() throws Exception {
    when(mockBackendClient.checkPipelineConfig(validPipelinePath))
            .thenReturn(new PipelineCheckResponse(true, null));

    CommandLine cmd = new CommandLine(checkCommand);
    int exitCode = cmd.execute(validPipelinePath);

    assertEquals(0, exitCode, "Expected exit code 0 for valid pipeline.");
  }

  // Test case: Invalid pipeline file with multiple errors
  @Test
  void testCheckInvalidPipeline() throws Exception {
    when(mockBackendClient.checkPipelineConfig(invalidPipelinePath))
            .thenReturn(new PipelineCheckResponse(false, List.of("Syntax error", "Missing key")));

    CommandLine cmd = new CommandLine(checkCommand);
    int exitCode = cmd.execute(invalidPipelinePath);

    assertEquals(3, exitCode, "Expected exit code 3 for invalid pipeline.");
  }

  // Test case: Backend exception handling (RuntimeException)
  @Test
  void testCheckCommandHandlesExceptions() throws Exception {
    when(mockBackendClient.checkPipelineConfig(validPipelinePath))
            .thenThrow(new RuntimeException("Backend error"));

    CommandLine cmd = new CommandLine(checkCommand);
    int exitCode = cmd.execute(validPipelinePath);

    assertEquals(1, exitCode, "Expected exit code 1 for backend failure.");
  }

  //Test case: Backend exception handling (IOException)
  @Test
  void testCheckCommandHandlesIOException() throws Exception {
    when(mockBackendClient.checkPipelineConfig(validPipelinePath))
            .thenThrow(new java.io.IOException("Network issue"));

    CommandLine cmd = new CommandLine(checkCommand);
    int exitCode = cmd.execute(validPipelinePath);

    assertEquals(1, exitCode, "Expected exit code 1 for IOException.");
  }
}
