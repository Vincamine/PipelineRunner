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
  private java.lang.reflect.Field yamlPathField;

  @BeforeEach
  void setUp() throws NoSuchFieldException {
    checkCommand = new CheckCommand();
    // Clear the output streams
    outContent.reset();
    errContent.reset();
    // Redirect stdout and stderr for testing
    System.setOut(new PrintStream(outContent));
    System.setErr(new PrintStream(errContent));

    yamlPathField = CheckCommand.class.getDeclaredField("yamlPath");
    yamlPathField.setAccessible(true);
  }

  @Test
  void testValidPipelineValidation() throws URISyntaxException, IllegalAccessException {
    final Path yamlPath = getResourcePath("yaml/commands/valid_pipeline.yml");
    yamlPathField.set(checkCommand, yamlPath);

    assertEquals(0, checkCommand.call());
    assertTrue(outContent.toString().contains("Pipeline validation successful"));
  }

  @Test
  void testInvalidPipelineValidation() throws URISyntaxException, IllegalAccessException {
    final Path yamlPath = getResourcePath("yaml/commands/invalid_pipeline.yml");
    yamlPathField.set(checkCommand, yamlPath);

    assertEquals(1, checkCommand.call());
    assertTrue(errContent.toString().contains("Pipeline validation failed"));
  }

  @Test
  void testNonExistentFile() throws IllegalAccessException {
    yamlPathField.set(checkCommand, Path.of("non_existent_file.yml"));

    // Clear streams before test
    outContent.reset();
    errContent.reset();

    assertEquals(1, checkCommand.call(), "Should return 1 for non-existent file");

    // Get the actual error output
    final String errorOutput = errContent.toString();
//    System.out.println("Actual error output: " + errorOutput); // Debug output

    assertTrue(errorOutput.contains("Error") || errorOutput.contains("error"),
        "Should output an error message for non-existent file. Actual output: " + errorOutput);
  }

  @Test
  void testInvalidYamlFormat() throws URISyntaxException, IllegalAccessException {
    final Path yamlPath = getResourcePath("yaml/commands/invalid_format_pipeline.yml");
    yamlPathField.set(checkCommand, yamlPath);

    assertEquals(1, checkCommand.call());
    assertTrue(errContent.toString().contains("Error validating pipeline"));
  }

  @Test
  void testMissingPipelineKey() throws URISyntaxException, IllegalAccessException {
    final Path yamlPath = getResourcePath("yaml/commands/missing_pipeline.yml");
    yamlPathField.set(checkCommand, yamlPath);

    assertEquals(1, checkCommand.call());
    assertTrue(errContent.toString().contains("Pipeline validation failed"));
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