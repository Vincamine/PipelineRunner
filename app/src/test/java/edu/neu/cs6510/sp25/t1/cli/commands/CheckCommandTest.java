package edu.neu.cs6510.sp25.t1.cli.commands;

import edu.neu.cs6510.sp25.t1.cli.util.PipelineValidator;
import edu.neu.cs6510.sp25.t1.cli.validation.YamlPipelineValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import picocli.CommandLine;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CheckCommandTest {
  @TempDir
  Path tempDir; // Temporary directory for test files

  private Path validYamlPath;
  private PipelineValidator mockValidator;
  private CheckCommand checkCommand;
  private CommandLine commandLine;
  private ByteArrayOutputStream outputStream;

  @BeforeEach
  void setUp() throws IOException {
    // Create .pipelines directory inside tempDir
    Path pipelinesDir = tempDir.resolve(".pipelines");
    Files.createDirectory(pipelinesDir);

    // Copy valid YAML file from test resources (updated path)
    validYamlPath = pipelinesDir.resolve("valid_pipeline.yml");
    copyTestResource("yaml/commands/pipelines/valid_pipeline.yml", validYamlPath);

    // Mock the pipeline validator
    mockValidator = mock(PipelineValidator.class);
    checkCommand = new CheckCommand();
    commandLine = new CommandLine(checkCommand);

    // Capture console output
    outputStream = new ByteArrayOutputStream();
    System.setOut(new PrintStream(outputStream));
  }

  @Test
  void testCheckCommand_ValidYaml() {
    // Mock successful validation
    when(mockValidator.validatePipelineFile(validYamlPath.toString())).thenReturn(true);

    // Execute the command with the actual temporary file path
    int exitCode = commandLine.execute("-f", validYamlPath.toString());

    // Verify output and return value
    String output = outputStream.toString();
    assertTrue(output.contains("Pipeline validation successful"));
    assertEquals(0, exitCode);
  }

  /**
   * Copies a test resource file from src/test/resources to a destination path.
   */
  private void copyTestResource(String resourcePath, Path destination) throws IOException {
    try (InputStream resourceStream = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
      if (resourceStream == null) {
        throw new IOException("Test resource not found: " + resourcePath);
      }
      Files.copy(resourceStream, destination, StandardCopyOption.REPLACE_EXISTING);
    }
  }
}
