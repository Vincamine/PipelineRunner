package edu.neu.cs6510.sp25.t1.cli.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for CheckCommand.
 * Ensures command line validation of pipeline YAML files works correctly.
 */
class CheckCommandTest {
  private CheckCommand checkCommand;
  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
  private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
  private final PrintStream originalOut = System.out;
  private final PrintStream originalErr = System.err;
  private java.lang.reflect.Field yamlFilePathField;

  @BeforeEach
  void setUp() throws NoSuchFieldException {
    checkCommand = new CheckCommand();

    // Redirect stdout and stderr for testing
    System.setOut(new PrintStream(outContent));
    System.setErr(new PrintStream(errContent));

    // Get access to private field `yamlFilePath`
    yamlFilePathField = CheckCommand.class.getDeclaredField("yamlFilePath");
    yamlFilePathField.setAccessible(true);
  }

  @Test
  void testValidPipelineValidation() throws URISyntaxException, IllegalAccessException {
    final Path yamlPath = getResourcePath("yaml/commands/pipelines/valid_pipeline.yml");
    yamlFilePathField.set(checkCommand, yamlPath.toString());

    assertTrue(checkCommand.call());
    assertTrue(outContent.toString().contains("Pipeline validation successful"));
  }

  @Test
  void testInvalidPipelineValidation() throws URISyntaxException, IllegalAccessException {
    final Path yamlPath = getResourcePath("yaml/commands/pipelines/invalid_pipeline.yml");
    yamlFilePathField.set(checkCommand, yamlPath.toString());

    assertFalse(checkCommand.call());
    assertTrue(errContent.toString().contains("Pipeline validation failed"));
  }

  @Test
  void testNonExistentFile() throws IllegalAccessException {
    yamlFilePathField.set(checkCommand, Path.of("non_existent_file.yml").toString());

    // Clear streams before test
    outContent.reset();
    errContent.reset();

    assertFalse(checkCommand.call(), "Should return false for non-existent file");

    // Get the actual error output
    final String errorOutput = errContent.toString();
    assertTrue(errorOutput.contains("Error") || errorOutput.contains("error"),
        "Should output an error message for non-existent file. Actual output: " + errorOutput);
  }

  @Test
  void testInvalidYamlFormat() throws URISyntaxException, IllegalAccessException {
    final Path yamlPath = getResourcePath("yaml/commands/pipelines/invalid_format_pipeline.yml");
    yamlFilePathField.set(checkCommand, yamlPath.toString());

    assertFalse(checkCommand.call());
    assertTrue(errContent.toString().contains("Error validating pipeline"));
  }

  @Test
  void testMissingPipelineKey() throws URISyntaxException, IllegalAccessException {
    final Path yamlPath = getResourcePath("yaml/commands/pipelines/missing_pipeline.yml");
    yamlFilePathField.set(checkCommand, yamlPath.toString());

    assertFalse(checkCommand.call());
    assertTrue(errContent.toString().contains("Pipeline validation failed"));
  }

  @Test
  void testYamlFileNotInPipelinesFolder() throws URISyntaxException, IllegalAccessException {
    // Providing a YAML file outside of `pipelines/` folder
    final Path yamlPath = getResourcePath("yaml/commands/not_in_pipelines.yml");
    yamlFilePathField.set(checkCommand, yamlPath.toString());

    // Run validation
    final boolean result = checkCommand.call();

    // Assert that validation fails due to incorrect location
    assertFalse(result);
    assertTrue(errContent.toString().contains("YAML file must be inside the 'pipelines/' folder"),
        "Error message should indicate the file is not in pipelines/. Actual: " + errContent.toString());
  }

  /**
   * Helper method to retrieve a test resource file path.
   *
   * @param resource The relative path of the test resource.
   * @return The absolute file path.
   * @throws URISyntaxException If resource path conversion fails.
   */
  private Path getResourcePath(String resource) throws URISyntaxException {
    return Paths.get(ClassLoader.getSystemResource(resource).toURI());
  }

  /**
   * Cleanup after each test to restore System.out and System.err
   */
  @org.junit.jupiter.api.AfterEach
  void restoreStreams() {
    System.setOut(originalOut);
    System.setErr(originalErr);
  }
}
