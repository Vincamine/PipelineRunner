package edu.neu.cs6510.sp25.t1.cli.commands;

import edu.neu.cs6510.sp25.t1.cli.util.PipelineExecutionOrderGenerator;
import edu.neu.cs6510.sp25.t1.cli.util.PipelineValidator;
import edu.neu.cs6510.sp25.t1.cli.validation.YamlPipelineValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;
import org.yaml.snakeyaml.Yaml;
import picocli.CommandLine;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DryRunCommandTest {
  @TempDir
  Path tempDir; // Temporary directory for test files

  private PipelineValidator mockValidator;
  private PipelineExecutionOrderGenerator mockExecutionGenerator;
  private DryRunCommand dryRunCommand;
  private CommandLine commandLine;
  private ByteArrayOutputStream outputStream;
  private Path yamlFilePath;

  @BeforeEach
  void setUp() throws IOException {
    // Create .pipelines directory inside tempDir
    Path pipelinesDir = tempDir.resolve(".pipelines");
    Files.createDirectory(pipelinesDir);

    // Copy valid YAML file from test resources to tempDir/.pipelines/
    yamlFilePath = pipelinesDir.resolve("valid_pipeline.yml");
    copyTestResource("yaml/commands/execution_pipelines/valid_pipeline.yml", yamlFilePath);

    // Mock dependencies
    mockValidator = mock(PipelineValidator.class);
    mockExecutionGenerator = mock(PipelineExecutionOrderGenerator.class);

    // Configure mocks
    when(mockValidator.validatePipelineFile(yamlFilePath.toString())).thenReturn(true);

    Map<String, Map<String, Object>> mockExecutionOrder = new LinkedHashMap<>();
    mockExecutionOrder.put("build", Map.of("BuildJob", new LinkedHashMap<>()));
    mockExecutionOrder.put("test", Map.of("TestJob", new LinkedHashMap<>()));
    mockExecutionOrder.put("deploy", Map.of("DeployJob", new LinkedHashMap<>()));

    when(mockExecutionGenerator.generateExecutionOrder(yamlFilePath.toString()))
        .thenReturn(mockExecutionOrder);

    // Set up DryRunCommand
    dryRunCommand = new DryRunCommand();
    dryRunCommand.yamlFilePath = yamlFilePath.toString();

    commandLine = new CommandLine(dryRunCommand);

    // Capture console output
    outputStream = new ByteArrayOutputStream();
    System.setOut(new PrintStream(outputStream));
  }

  @Test
  void testDryRunCommand_ValidYaml() {
    // Execute the command
    int exitCode = commandLine.execute("-f", yamlFilePath.toString());

    // Capture console output
    String output = outputStream.toString().trim();
    System.out.println("Raw DryRun Output:\n" + output);

    String[] lines = output.split("\n");
    StringBuilder yamlOutputBuilder = new StringBuilder();
    boolean foundYamlStart = false;

    for (String line : lines) {
      if (line.startsWith("build:") || line.startsWith("test:") || line.startsWith("deploy:")) {
        foundYamlStart = true;
      }
      if (foundYamlStart) {
        yamlOutputBuilder.append(line).append("\n");
      }
    }

    String yamlOutput = yamlOutputBuilder.toString().trim();
    System.out.println("Extracted YAML Output:\n" + yamlOutput);

    assertFalse(yamlOutput.isEmpty(), "Extracted YAML output should not be empty.");

    Yaml yaml = new Yaml();
    Map<String, Object> parsedYaml;
    try {
      parsedYaml = yaml.load(yamlOutput);
    } catch (Exception e) {
      fail("YAML parsing failed. Extracted Output:\n" + yamlOutput);
      return;
    }

    assertNotNull(parsedYaml, "Execution order YAML should not be null.");
    assertTrue(parsedYaml.containsKey("build"));
    assertTrue(parsedYaml.containsKey("test"));
    assertTrue(parsedYaml.containsKey("deploy"));

    assertEquals(0, exitCode);
  }



  /**
   * Copies a test resource file from `src/test/resources` to the temp directory.
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
