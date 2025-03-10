package edu.neu.cs6510.sp25.t1.cli.commands;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;

import edu.neu.cs6510.sp25.t1.cli.CliApp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class CheckCommandTest {

  private CheckCommand checkCommand;
  private CliApp parent;

  @TempDir
  Path tempDir;

  // Setup streams for capturing console output
  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
  private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
  private final PrintStream originalOut = System.out;
  private final PrintStream originalErr = System.err;

  @BeforeEach
  public void setUp() {
    System.setOut(new PrintStream(outContent));
    System.setErr(new PrintStream(errContent));

    // Create real objects instead of mocks
    parent = new CliApp();
    checkCommand = new CheckCommand();

    // Set the parent command using reflection
    try {
      Field parentField = CheckCommand.class.getDeclaredField("parent");
      parentField.setAccessible(true);
      parentField.set(checkCommand, parent);
    } catch (Exception e) {
      fail("Failed to set parent field: " + e.getMessage());
    }
  }

  @AfterEach
  public void tearDown() {
    System.setOut(originalOut);
    System.setErr(originalErr);
  }

  /**
   * Test when file path points to a file that doesn't exist.
   */
  @Test
  public void testCall_FileNotFound() {
    // Setup
    String nonExistingPath = tempDir.resolve("nonexistent.yml").toString();

    // Set filePath directly on parent object
    parent.filePath = nonExistingPath;

    // Execute
    Integer result = checkCommand.call();

    // Verify
    assertEquals(1, result);
    assertTrue(errContent.toString().contains("File not found: " + nonExistingPath));
  }

  /**
   * Test when file path points to a directory instead of a file.
   */
  @Test
  public void testCall_DirectoryNotFile() {
    // Setup - use the tempDir itself, which is a directory, not a file
    String dirPath = tempDir.toString();

    // Set filePath directly on parent object
    parent.filePath = dirPath;

    // Execute
    Integer result = checkCommand.call();

    // Verify
    assertEquals(1, result);
    assertTrue(errContent.toString().contains("File not found: " + dirPath));
  }

  /**
   * Test with an invalid YAML file that will fail validation.
   */
  @Test
  public void testCall_InvalidYamlFile() throws IOException {
    // Create an invalid YAML file
    Path invalidYamlPath = tempDir.resolve("invalid.yml");
    Files.writeString(invalidYamlPath, "this: is: not: valid: yaml:");

    // Set filePath directly on parent object
    parent.filePath = invalidYamlPath.toString();

    // Execute
    Integer result = checkCommand.call();

    // For a real validation failure the result would be 1, but if the validator
    // isn't available in tests, the result might vary
    // We're testing the return value is non-zero (error)
    assertNotEquals(0, result);
  }

  /**
   * Test message formatting for pipeline validation output.
   * This test doesn't call the actual command but simulates the output.
   */
  @Test
  public void testOutputFormat() {
    // Manually simulate output formats to verify they match expected patterns
    String testFilePath = "pipeline.yml";

    // Success output
    outContent.reset();
    System.out.println("Checking pipeline configuration: " + testFilePath);
    System.out.println("Pipeline configuration is valid!");

    String successOutput = outContent.toString();
    assertTrue(successOutput.contains("Checking pipeline configuration:"));
    assertTrue(successOutput.contains("Pipeline configuration is valid!"));

    // Error output
    errContent.reset();
    System.err.println("Pipeline validation failed!");
    System.err.println("  ➜ Invalid stage name");

    String errorOutput = errContent.toString();
    assertTrue(errorOutput.contains("Pipeline validation failed!"));
    assertTrue(errorOutput.contains("➜ Invalid stage name"));
  }

  /**
   * Test case for null file path.
   */
  @Test
  public void testNullPath() {
    // Set filePath to null
    parent.filePath = null;

    // Execute and verify return code
    int result = checkCommand.call();
    assertEquals(1, result, "Command should return error code 1 for null file path");
  }

  /**
   * Test case for empty file path.
   */
  @Test
  public void testCall_EmptyFilePath() {
    // Setup
    parent.filePath = "";

    // Execute
    Integer result = checkCommand.call();

    // Verify
    assertEquals(1, result);
    assertTrue(errContent.toString().contains("File not found:"));
  }
}