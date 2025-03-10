package edu.neu.cs6510.sp25.t1.cli.validation.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import edu.neu.cs6510.sp25.t1.cli.validation.error.ValidationException;
import edu.neu.cs6510.sp25.t1.cli.validation.parser.YamlParser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class YamlPipelineValidatorTest {

  @TempDir
  Path tempDir;

  @Test
  public void testValidatePipeline_FileNotFound_ThrowsValidationException() {
    // Arrange
    String nonExistentFilePath = "/path/to/nonexistent/pipeline.yml";

    // Act & Assert
    ValidationException exception = assertThrows(ValidationException.class,
            () -> YamlPipelineValidator.validatePipeline(nonExistentFilePath));

    assertTrue(exception.getMessage().contains("File not found"));
  }

  @Test
  public void testValidatePipeline_YamlParsingFails_ThrowsValidationException() throws IOException {
    // Arrange
    // Create a temporary YAML file with invalid content
    Path yamlFile = tempDir.resolve("invalid-yaml.yml");
    Files.writeString(yamlFile, "name: test-pipeline\ninvalid: :");

    // Use try-with-resources for all static mocks
    try (MockedStatic<YamlParser> yamlParserMock = Mockito.mockStatic(YamlParser.class)) {

      // Throw exception from YamlParser
      ValidationException parsingException = new ValidationException(yamlFile.toString(), 2, 10, "Invalid YAML syntax");
      yamlParserMock.when(() -> YamlParser.parseYaml(any(File.class)))
              .thenThrow(parsingException);

      // Act & Assert
      ValidationException exception = assertThrows(ValidationException.class,
              () -> YamlPipelineValidator.validatePipeline(yamlFile.toString()));

      assertEquals(parsingException, exception);
      assertTrue(exception.getMessage().contains("Invalid YAML syntax"));
    }
  }
}