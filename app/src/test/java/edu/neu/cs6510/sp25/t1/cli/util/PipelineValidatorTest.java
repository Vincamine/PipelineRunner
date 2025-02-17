package edu.neu.cs6510.sp25.t1.cli.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import edu.neu.cs6510.sp25.t1.util.PipelineValidator;
import edu.neu.cs6510.sp25.t1.validation.YamlPipelineValidator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PipelineValidatorTest {

  @TempDir
  Path tempDir;

  private Path validYamlPath;
  private Path invalidYamlPath;
  private Path wrongDirYamlPath;
  private YamlPipelineValidator mockValidator;
  private PipelineValidator pipelineValidator;

  @BeforeEach
  void setUp() throws IOException {
    final Path pipelinesDir = tempDir.resolve(".pipelines");
    Files.createDirectory(pipelinesDir);

    validYamlPath = pipelinesDir.resolve("valid_pipeline.yml");
    Files.writeString(validYamlPath, "valid: pipeline");

    invalidYamlPath = pipelinesDir.resolve("invalid_pipeline.yml");
    Files.writeString(invalidYamlPath, "invalid: : pipeline");

    wrongDirYamlPath = tempDir.resolve("wrong_pipeline.yml");
    Files.writeString(wrongDirYamlPath, "valid: pipeline");

    // Mock YamlPipelineValidator
    mockValidator = mock(YamlPipelineValidator.class);
    pipelineValidator = new PipelineValidator(mockValidator);
  }

  @Test
  void testValidatePipelineFile_FileNotFound() {
    assertFalse(pipelineValidator.validatePipelineFile(tempDir.resolve("nonexistent.yml").toString()));
  }

  @Test
  void testValidatePipelineFile_FileNotInPipelinesDirectory() {
    assertFalse(pipelineValidator.validatePipelineFile(wrongDirYamlPath.toString()));
  }

  @Test
  void testValidatePipelineFile_ValidYaml() {
    when(mockValidator.validatePipeline(validYamlPath.toString())).thenReturn(true);
    assertTrue(pipelineValidator.validatePipelineFile(validYamlPath.toString()));
  }

  @Test
  void testValidatePipelineFile_InvalidYaml() {
    when(mockValidator.validatePipeline(invalidYamlPath.toString())).thenReturn(false);
    assertFalse(pipelineValidator.validatePipelineFile(invalidYamlPath.toString()));
  }

  @Test
  void testValidatePipelineFile_ExceptionHandling() {
    when(mockValidator.validatePipeline(anyString())).thenThrow(new RuntimeException("Unexpected error"));
    assertFalse(pipelineValidator.validatePipelineFile(validYamlPath.toString()));
  }
}
