package edu.neu.cs6510.sp25.t1.cli.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import edu.neu.cs6510.sp25.t1.cli.api.CliBackendClient;
import edu.neu.cs6510.sp25.t1.common.config.PipelineConfig;
import edu.neu.cs6510.sp25.t1.common.validation.parser.YamlParser;
import edu.neu.cs6510.sp25.t1.common.validation.validator.PipelineValidator;
import edu.neu.cs6510.sp25.t1.common.validation.error.ValidationException;
import picocli.CommandLine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

class DryRunCommandTest {
  private DryRunCommand dryRunCommand;
  private CommandLine cmd;
  private CliBackendClient backendClient;

  @TempDir
  private Path tempDir;

  @BeforeEach
  void setUp() {
    backendClient = mock(CliBackendClient.class);
    dryRunCommand = new DryRunCommand(backendClient);
    cmd = new CommandLine(dryRunCommand);
  }

  @Test
  void shouldReturnErrorWhenNoFileProvided() {
    dryRunCommand.configFile = null;
    assertEquals(2, dryRunCommand.call());
  }

  @Test
  void shouldReturnErrorWhenFileDoesNotExist() {
    dryRunCommand.configFile = "nonexistent.yaml";
    assertEquals(2, dryRunCommand.call());
  }

  @Test
  void shouldReturnValidationErrorWhenYamlParsingFails() throws Exception {
    File tempFile = tempDir.resolve("invalid.yaml").toFile();
    Files.write(tempFile.toPath(), "invalid-content".getBytes());
    dryRunCommand.configFile = tempFile.getAbsolutePath();

    try (MockedStatic<YamlParser> parserMock = mockStatic(YamlParser.class)) {
      parserMock.when(() -> YamlParser.parseYaml(tempFile)).thenThrow(new ValidationException("Invalid YAML"));

      assertEquals(3, dryRunCommand.call());
    }
  }

  @Test
  void shouldReturnErrorWhenBackendFails() throws Exception {
    File tempFile = tempDir.resolve("valid.yaml").toFile();
    Files.write(tempFile.toPath(), "pipeline: test-pipeline".getBytes());
    dryRunCommand.configFile = tempFile.getAbsolutePath();

    PipelineConfig mockPipelineConfig = mock(PipelineConfig.class);
    when(mockPipelineConfig.getName()).thenReturn("test-pipeline");

    try (MockedStatic<YamlParser> parserMock = mockStatic(YamlParser.class);
         MockedStatic<PipelineValidator> validatorMock = mockStatic(PipelineValidator.class)) {

      parserMock.when(() -> YamlParser.parseYaml(tempFile)).thenReturn(mockPipelineConfig);
      when(backendClient.dryRunPipeline(tempFile.getAbsolutePath())).thenThrow(new IOException("Backend error"));

      assertEquals(1, dryRunCommand.call());
    }
  }

  @Test
  void shouldReturnSuccessWhenDryRunSucceeds() throws IOException {
    File tempFile = tempDir.resolve("valid.yaml").toFile();
    Files.write(tempFile.toPath(), "pipeline: test-pipeline".getBytes());
    dryRunCommand.configFile = tempFile.getAbsolutePath();

    try (MockedStatic<YamlParser> parserMock = mockStatic(YamlParser.class);
         MockedStatic<PipelineValidator> validatorMock = mockStatic(PipelineValidator.class)) {

      parserMock.when(() -> YamlParser.parseYaml(tempFile)).thenReturn(mock(PipelineConfig.class));
      when(backendClient.dryRunPipeline(tempFile.getAbsolutePath())).thenReturn("Pipeline Dry-Run Successful");

      assertEquals(0, dryRunCommand.call());
    }
  }
}
