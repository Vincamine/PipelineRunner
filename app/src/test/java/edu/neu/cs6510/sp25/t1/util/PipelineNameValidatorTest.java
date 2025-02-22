package edu.neu.cs6510.sp25.t1.util;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class PipelineNameValidatorTest {

  private static Path projectRoot;
  private static Path pipelinesDir;
  private static PipelineNameValidator validator;

  @BeforeAll
  static void setupProjectRoot() {
    projectRoot = Paths.get(System.getProperty("user.dir")).getParent();
    pipelinesDir = projectRoot.resolve(".pipelines");

    System.out.println("Using .pipelines directory at: " + pipelinesDir.toAbsolutePath());

    File pipelineDir = pipelinesDir.toFile();
    assertTrue(pipelineDir.exists() && pipelineDir.isDirectory(),
        ".pipelines directory should exist at project root: " + pipelinesDir);
  }

  @BeforeEach
  void setUp() {
    validator = new PipelineNameValidator();
  }

  @Test
  void testExistingYamlFileNamesAreLoaded() {
    File directory = pipelinesDir.toFile();
    assertTrue(directory.exists() && directory.isDirectory(),
        ".pipelines directory should exist.");
  }

  @Test
  void testUniqueFileName() {
    assertTrue(validator.isYamlFileNameUnique("new_pipeline.yaml"),
        "new_pipeline.yaml should be unique.");
  }

  @Test
  void testDuplicateFileName() {
    assertFalse(validator.isYamlFileNameUnique("pipeline.yaml"),
        "pipeline.yaml already exists and should not be unique.");
  }

  @Test
  void testSuggestUniqueFileName() {
    String suggestedName = validator.suggestUniqueYamlFileName("pipeline.yaml");
    assertNotEquals("pipeline.yaml", suggestedName,
        "Suggested name should not be the same as an existing name.");
    assertTrue(suggestedName.matches("pipeline_\\d+\\.yaml"),
        "Suggested name should have a number appended.");
  }

  @Test
  void testSuggestUniqueFileNameForNewFile() {
    String suggestedName = validator.suggestUniqueYamlFileName("completely_new.yaml");
    assertEquals("completely_new.yaml", suggestedName,
        "If the file is unique, it should return the same name.");
  }
}
