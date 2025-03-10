package edu.neu.cs6510.sp25.t1.cli.validation.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PipelineNameManagerTest {

  private PipelineNameManager manager;

  @TempDir
  Path tempDir;

  private Path pipelinesDir;

  @BeforeEach
  void setUp() throws Exception {
    // Create a temporary .pipelines directory for testing
    pipelinesDir = tempDir.resolve(".pipelines");
    Files.createDirectory(pipelinesDir);

    // Set the system property to use our temp directory
    String originalUserDir = System.getProperty("user.dir");
    System.setProperty("user.dir", tempDir.toString());

    // Create the manager instance
    manager = new PipelineNameManager();

    // Reset the system property
    System.setProperty("user.dir", originalUserDir);
  }

  @Test
  void testConstructorAndInitialization() throws Exception {
    // Verify the manager was initialized correctly
    Field existingFileNamesField = PipelineNameManager.class.getDeclaredField("existingFileNames");
    existingFileNamesField.setAccessible(true);

    @SuppressWarnings("unchecked")
    Set<String> existingFileNames = (Set<String>) existingFileNamesField.get(manager);

    assertNotNull(existingFileNames);
    assertTrue(existingFileNames.isEmpty(), "No YAML files should be loaded initially in the test environment");
  }

  @Test
  void testLoadExistingYamlFileNames() throws Exception {
    // Create some test YAML files
    createTestYamlFile("pipeline1.yaml");
    createTestYamlFile("pipeline2.yml");
    createTestYamlFile("test.txt"); // Non-YAML file that should be ignored

    // Create a new manager instance that will load these files
    String originalUserDir = System.getProperty("user.dir");
    System.setProperty("user.dir", tempDir.toString());
    PipelineNameManager newManager = new PipelineNameManager();
    System.setProperty("user.dir", originalUserDir);

    // Get the existingFileNames set via reflection
    Field existingFileNamesField = PipelineNameManager.class.getDeclaredField("existingFileNames");
    existingFileNamesField.setAccessible(true);

    @SuppressWarnings("unchecked")
    Set<String> existingFileNames = (Set<String>) existingFileNamesField.get(newManager);

    // Verify that only YAML files were loaded
    assertEquals(2, existingFileNames.size());
    assertTrue(existingFileNames.contains("pipeline1.yaml"));
    assertTrue(existingFileNames.contains("pipeline2.yml"));
    assertFalse(existingFileNames.contains("test.txt"));
  }

  @Test
  void testIsPipelineNameUnique_WithUniqueNames() throws Exception {
    // Setup existing files via reflection to avoid creating actual files
    setupExistingFileNames("pipeline1.yaml", "pipeline2.yml");

    // Test unique names
    assertTrue(manager.isPipelineNameUnique("new_pipeline.yaml"));
    assertTrue(manager.isPipelineNameUnique("another_pipeline.yml"));
  }

  @Test
  void testIsPipelineNameUnique_WithDuplicateNames() throws Exception {
    // Setup existing files via reflection
    setupExistingFileNames("pipeline1.yaml", "pipeline2.yml");

    // Test duplicates (case-insensitive)
    assertFalse(manager.isPipelineNameUnique("pipeline1.yaml"));
    assertFalse(manager.isPipelineNameUnique("Pipeline1.YAML")); // Different case
    assertFalse(manager.isPipelineNameUnique("pipeline2.yml"));
  }

  @Test
  void testSuggestUniquePipelineName_WithUniqueBaseName() throws Exception {
    // Setup existing files via reflection
    setupExistingFileNames("pipeline1.yaml", "pipeline2.yml");

    // Should return the original name if it's unique
    assertEquals("new_pipeline.yaml", manager.suggestUniquePipelineName("new_pipeline.yaml"));
    assertEquals("test.yml", manager.suggestUniquePipelineName("test.yml"));
  }

  @Test
  void testSuggestUniquePipelineName_WithDuplicateBaseName() throws Exception {
    // Setup existing files via reflection
    setupExistingFileNames("pipeline1.yaml", "pipeline1_1.yaml", "test.yml");

    // Should suggest incremented names
    assertEquals("pipeline1_2.yaml", manager.suggestUniquePipelineName("pipeline1.yaml"));
    assertEquals("test_1.yml", manager.suggestUniquePipelineName("test.yml"));
  }

  @Test
  void testSuggestUniquePipelineName_WithMultipleDuplicates() throws Exception {
    // Setup existing files via reflection
    setupExistingFileNames("duplicate.yaml", "duplicate_1.yaml", "duplicate_2.yaml");

    // Should find the next available number
    assertEquals("duplicate_3.yaml", manager.suggestUniquePipelineName("duplicate.yaml"));
  }

  @Test
  void testLoadExistingYamlFileNames_DirectoryDoesNotExist() throws Exception {
    // Delete the .pipelines directory
    Files.delete(pipelinesDir);

    // Create a new manager instance - this should log a warning but not crash
    String originalUserDir = System.getProperty("user.dir");
    System.setProperty("user.dir", tempDir.toString());
    PipelineNameManager newManager = new PipelineNameManager();
    System.setProperty("user.dir", originalUserDir);

    // Verify that isPipelineNameUnique still works (should return true since no files exist)
    assertTrue(newManager.isPipelineNameUnique("any_name.yaml"));
  }

  @Test
  void testSuggestUniquePipelineName_PreservesExtension() throws Exception {
    setupExistingFileNames("test.yaml");

    // Should keep .yaml extension
    assertEquals("test_1.yaml", manager.suggestUniquePipelineName("test.yaml"));

    // Setup a .yml file and verify it keeps .yml extension
    setupExistingFileNames("test.yml");
    assertEquals("another_test.yml", manager.suggestUniquePipelineName("another_test.yml"));
  }

  // Helper method to create a test YAML file
  private void createTestYamlFile(String fileName) throws IOException {
    Path filePath = pipelinesDir.resolve(fileName);
    Files.createFile(filePath);
  }

  // Helper method to set up existing file names via reflection
  private void setupExistingFileNames(String... fileNames) throws Exception {
    Field existingFileNamesField = PipelineNameManager.class.getDeclaredField("existingFileNames");
    existingFileNamesField.setAccessible(true);

    Set<String> existingFileNames = new HashSet<>();
    for (String fileName : fileNames) {
      existingFileNames.add(fileName.toLowerCase());
    }

    existingFileNamesField.set(manager, existingFileNames);
  }
}