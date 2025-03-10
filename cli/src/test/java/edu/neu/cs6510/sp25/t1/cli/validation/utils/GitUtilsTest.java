package edu.neu.cs6510.sp25.t1.cli.validation.utils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GitUtilsTest {

  @TempDir
  Path tempDir;

  private String originalUserDir;
  private Path pipelinesDir;

  @BeforeEach
  void setUp() throws Exception {
    // Save original user.dir to restore after test
    originalUserDir = System.getProperty("user.dir");

    // Change to temp directory
    System.setProperty("user.dir", tempDir.toString());

    // Create .pipelines directory at the root of our test directory
    pipelinesDir = tempDir.resolve(".pipelines");
    Files.createDirectory(pipelinesDir);
  }

  @AfterEach
  void tearDown() {
    System.setProperty("user.dir", originalUserDir);
  }


  // Test if hasPipelinesFolder works when the directory doesn't exist
  @Test
  void testHasPipelinesFolder_WhenMissing() throws IOException {
    // Delete the .pipelines directory
    Files.deleteIfExists(pipelinesDir);

    boolean result = GitUtils.hasPipelinesFolder();
    assertFalse(result, "Should not find the deleted .pipelines folder");
  }

  // Test validation with missing pipelines directory
  @Test
  void testValidatePipelineFile_MissingDirectory() throws IOException {
    // Delete the directory
    Files.deleteIfExists(pipelinesDir);

    // Should throw exception for missing directory
    Exception exception = assertThrows(IllegalStateException.class, () -> {
      GitUtils.validatePipelineFile(null);
    });

    assertTrue(exception.getMessage().contains("Missing .pipelines directory"));
  }

  // Test for validateRepo - simplified without mocking
  @Test
  void testValidateRepo_MissingFolder() throws IOException {
    // Delete the directory to force a failure
    Files.deleteIfExists(pipelinesDir);

    // Should throw exception
    Exception exception = assertThrows(IllegalStateException.class, () -> {
      GitUtils.validateRepo("test.yaml");
    });

    // The exception message will depend on which check fails first
    // This will fail at validatePipelineFile since we can't easily mock isInsideGitRepo
    assertTrue(exception.getMessage().contains("Missing .pipelines directory"));
  }

}