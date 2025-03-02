package edu.neu.cs6510.sp25.t1.cli.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import edu.neu.cs6510.sp25.t1.common.config.PipelineConfig;
import edu.neu.cs6510.sp25.t1.common.parser.YamlParser;
import edu.neu.cs6510.sp25.t1.common.validation.PipelineValidator;
import edu.neu.cs6510.sp25.t1.common.validation.ValidationException;
import picocli.CommandLine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;


class CheckCommandTest {
  private CheckCommand checkCommand;
  private CommandLine cmd;

  @TempDir
  private Path tempDir; // Temporary directory for file creation

  @BeforeEach
  void setUp() {
    checkCommand = new CheckCommand();
    cmd = new CommandLine(checkCommand);
  }

  @Test
  void shouldReturnErrorWhenNoFileProvided() {
    checkCommand.configFile = null;
    assertEquals(2, checkCommand.call());
  }

  @Test
  void shouldReturnErrorWhenFileDoesNotExist() {
    checkCommand.configFile = "nonexistent.yaml";
    assertEquals(2, checkCommand.call());
  }

  @Test
  void shouldReturnSuccessWhenFileIsValid() throws IOException {
    // Create a temporary YAML file
    File tempFile = tempDir.resolve("valid.yaml").toFile();
    Files.write(tempFile.toPath(), "pipeline: test-pipeline".getBytes());
    checkCommand.configFile = tempFile.getAbsolutePath();

    try (MockedStatic<YamlParser> parserMock = mockStatic(YamlParser.class);
         MockedStatic<PipelineValidator> validatorMock = mockStatic(PipelineValidator.class)) {

      parserMock.when(() -> YamlParser.parseYaml(tempFile)).thenReturn(mock(PipelineConfig.class));

      assertEquals(0, checkCommand.call());
    }
  }

  @Test
  void shouldReturnValidationErrorWhenInvalidFile() throws IOException {
    // Create a temporary YAML file
    File tempFile = tempDir.resolve("invalid.yaml").toFile();
    Files.write(tempFile.toPath(), "pipeline: invalid".getBytes());
    checkCommand.configFile = tempFile.getAbsolutePath();

    try (MockedStatic<YamlParser> parserMock = mockStatic(YamlParser.class);
         MockedStatic<PipelineValidator> validatorMock = mockStatic(PipelineValidator.class)) {

      parserMock.when(() -> YamlParser.parseYaml(tempFile)).thenReturn(mock(PipelineConfig.class));
      validatorMock.when(() -> PipelineValidator.validate(any())).thenThrow(new ValidationException("Invalid"));

      assertEquals(3, checkCommand.call());
    }
  }
}
