package edu.neu.cs6510.sp25.t1.cli.commands;

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.nio.file.Paths;

import edu.neu.cs6510.sp25.t1.cli.api.CliBackendClient;
import picocli.CommandLine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


class DryRunCommandTest {
  private CliBackendClient mockBackendClient;
  private DryRunCommand dryRunCommand;
  private String validPipelinePath;
  private String invalidPipelinePath;

  @BeforeEach
  void setUp() throws URISyntaxException {
    mockBackendClient = mock(CliBackendClient.class);
    dryRunCommand = new DryRunCommand(mockBackendClient);

    validPipelinePath = Paths.get(getClass().getClassLoader()
            .getResource(".pipelines/pipeline.yaml").toURI()).toString();

    invalidPipelinePath = Paths.get(getClass().getClassLoader()
            .getResource(".pipelines/invalid_pipeline.yaml").toURI()).toString();
  }

  // Test case: No pipeline file provided (null)
  @Test
  void testDryRunCommand_NoPipelineFile() {
    CommandLine cmd = new CommandLine(dryRunCommand);
    int exitCode = cmd.execute();  // No file argument

    assertEquals(2, exitCode, "Expected exit code 2 for missing pipeline file.");
  }

  // Test case: Empty pipeline file path
  @Test
  void testDryRunCommand_EmptyPipelineFile() {
    CommandLine cmd = new CommandLine(dryRunCommand);
    int exitCode = cmd.execute("");  // Empty string argument

    assertEquals(2, exitCode, "Expected exit code 2 for empty pipeline file path.");
  }

  @Test
  void testDryRunValidPipeline() throws Exception {
    String mockResponse = "execution: success";
    when(mockBackendClient.dryRunPipeline(validPipelinePath)).thenReturn(mockResponse);

    CommandLine cmd = new CommandLine(dryRunCommand);
    int exitCode = cmd.execute("-f", validPipelinePath);  // 🔹 FIXED ARGUMENT FORMAT

    assertEquals(0, exitCode, "Expected exit code 0 for valid dry-run.");
  }

  @Test
  void testDryRunInvalidPipeline() throws Exception {
    String mockResponse = "Error: Invalid pipeline syntax";
    when(mockBackendClient.dryRunPipeline(invalidPipelinePath)).thenReturn(mockResponse);

    CommandLine cmd = new CommandLine(dryRunCommand);
    int exitCode = cmd.execute("-f", invalidPipelinePath);  // 🔹 FIXED ARGUMENT FORMAT

    assertEquals(3, exitCode, "Expected exit code 3 for invalid pipeline.");
  }

  @Test
  void testDryRunHandlesRuntimeException() throws Exception {
    when(mockBackendClient.dryRunPipeline(anyString()))
            .thenThrow(new RuntimeException("Backend error"));

    CommandLine cmd = new CommandLine(dryRunCommand);
    int exitCode = cmd.execute("-f", validPipelinePath);  // 🔹 FIXED ARGUMENT FORMAT

    assertEquals(1, exitCode, "Expected exit code 1 for RuntimeException.");
  }

  @Test
  void testDryRunHandlesIOException() throws Exception {
    when(mockBackendClient.dryRunPipeline(anyString()))
            .thenThrow(new java.io.IOException("Network issue"));

    CommandLine cmd = new CommandLine(dryRunCommand);
    int exitCode = cmd.execute("-f", validPipelinePath);  // 🔹 FIXED ARGUMENT FORMAT

    assertEquals(1, exitCode, "Expected exit code 1 for IOException.");
  }

  @Test
  void testDryRunYAMLOutput() throws Exception {
    String jsonResponse = "{\"pipeline\":\"test\"}";
    YAMLMapper yamlMapper = new YAMLMapper();
    String expectedYamlResponse = yamlMapper.writeValueAsString(jsonResponse);

    when(mockBackendClient.dryRunPipeline(validPipelinePath)).thenReturn(jsonResponse);

    DryRunCommand yamlCommand = new DryRunCommand(mockBackendClient);
    yamlCommand.outputFormat = "yaml";  // Set output format

    CommandLine cmd = new CommandLine(yamlCommand);
    int exitCode = cmd.execute("-f", validPipelinePath, "-o", "yaml");  // 🔹 FIXED ARGUMENT FORMAT

    assertEquals(0, exitCode, "Expected exit code 0 for valid YAML output.");
  }
}
