package edu.neu.cs6510.sp25.t1.common.parser;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import edu.neu.cs6510.sp25.t1.common.config.PipelineConfig;
import edu.neu.cs6510.sp25.t1.common.validation.error.ValidationException;
import edu.neu.cs6510.sp25.t1.common.validation.parser.YamlParser;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class YamlParserTest {
  @TempDir
  private Path tempDir;

  @Test
  void shouldParseValidYamlSuccessfully() throws IOException, ValidationException {
    File yamlFile = tempDir.resolve("valid.yaml").toFile();
    String yamlContent = "name: test-pipeline\nstages: []";
    Files.write(yamlFile.toPath(), yamlContent.getBytes());

    PipelineConfig pipeline = YamlParser.parseYaml(yamlFile);
    assertNotNull(pipeline);
  }

  @Test
  void shouldThrowValidationExceptionForInvalidYaml() throws IOException {
    File yamlFile = tempDir.resolve("invalid.yaml").toFile();
    String yamlContent = "invalid-content";
    Files.write(yamlFile.toPath(), yamlContent.getBytes());

    ValidationException exception = assertThrows(ValidationException.class, () -> {
      YamlParser.parseYaml(yamlFile);
    });

    assertTrue(exception.getMessage().contains("Failed to parse YAML"));
  }
}
