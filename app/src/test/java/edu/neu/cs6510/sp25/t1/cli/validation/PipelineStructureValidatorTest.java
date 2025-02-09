package edu.neu.cs6510.sp25.t1.cli.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PipelineStructureValidator.
 * Ensures the pipeline structure is properly validated.
 */
class PipelineStructureValidatorTest {
  private PipelineStructureValidator validator;

  @BeforeEach
  void setUp() {
    validator = new PipelineStructureValidator();
  }

  /**
   * Tests a valid YAML pipeline structure.
   */
  @Test
  void testValidPipelineStructure() throws IOException, URISyntaxException {
    Path yamlPath = getResourcePath("yaml/valid_structure.yml");
    Map<String, Object> yamlData = YamlLoader.loadYaml(yamlPath.toString());

    assertTrue(validator.validate(yamlData), "Valid pipeline structure should pass validation.");
  }

  /**
   * Tests a YAML file that is missing the 'pipeline' key.
   */
  @Test
  void testMissingPipelineKey() throws IOException, URISyntaxException {
    Path yamlPath = getResourcePath("yaml/missing_pipeline.yml");
    Map<String, Object> yamlData = YamlLoader.loadYaml(yamlPath.toString());

    assertFalse(validator.validate(yamlData), "Pipeline key is missing and should fail validation.");
  }

  /**
   * Tests a YAML file where 'pipeline' exists but 'name' is missing.
   */
  @Test
  void testMissingPipelineName() throws IOException, URISyntaxException {
    Path yamlPath = getResourcePath("yaml/missing_name.yml");
    Map<String, Object> yamlData = YamlLoader.loadYaml(yamlPath.toString());

    assertFalse(validator.validate(yamlData), "Missing 'name' key should fail validation.");
  }

  /**
   * Tests a YAML file where 'pipeline' exists but 'stages' is missing.
   */
  @Test
  void testMissingStagesKey() throws IOException, URISyntaxException {
    Path yamlPath = getResourcePath("yaml/missing_stages.yml");
    Map<String, Object> yamlData = YamlLoader.loadYaml(yamlPath.toString());

    assertFalse(validator.validate(yamlData), "Missing 'stages' key should fail validation.");
  }

  /**
   * Tests a YAML file where 'stages' is an empty list.
   */
  @Test
  void testEmptyStages() throws IOException, URISyntaxException {
    Path yamlPath = getResourcePath("yaml/empty_stages.yml");
    Map<String, Object> yamlData = YamlLoader.loadYaml(yamlPath.toString());

    assertFalse(validator.validate(yamlData), "Empty 'stages' list should fail validation.");
  }

  /**
   * Tests a YAML file where 'stages' is not a list.
   */
  @Test
  void testInvalidStagesType() throws IOException, URISyntaxException {
    Path yamlPath = getResourcePath("yaml/invalid_stages.yml");
    Map<String, Object> yamlData = YamlLoader.loadYaml(yamlPath.toString());

    assertFalse(validator.validate(yamlData), "'stages' should be a list, incorrect type should fail validation.");
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
}
