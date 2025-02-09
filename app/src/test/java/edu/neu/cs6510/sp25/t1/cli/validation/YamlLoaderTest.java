package edu.neu.cs6510.sp25.t1.cli.validation;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for YamlLoader.
 * Ensures YAML files are correctly loaded and parsed.
 */
class YamlLoaderTest {

  /**
   * Tests loading a valid YAML file.
   */
  @Test
  void testValidYamlFile() throws IOException, URISyntaxException {
    Path yamlPath = getResourcePath("yaml/loader/valid_pipeline_loader_test.yml");
    Map<String, Object> yamlData = YamlLoader.loadYaml(yamlPath.toString());

    assertNotNull(yamlData);
    assertTrue(yamlData.containsKey("pipeline"));
    assertTrue(yamlData.containsKey("job"));

    // Validate pipeline name
    Map<String, Object> pipeline = (Map<String, Object>) yamlData.get("pipeline");
    assertEquals("TestPipeline", pipeline.get("name"));
  }

  /**
   * Tests loading an invalid YAML file.
   */
  @Test
  void testInvalidYamlFile() throws URISyntaxException {
    Path yamlPath = getResourcePath("yaml/loader/invalid_pipeline_loader_test.yml");

    Exception exception = assertThrows(IllegalArgumentException.class, () ->
        YamlLoader.loadYaml(yamlPath.toString()));

    assertTrue(exception.getMessage().contains("Invalid YAML format"));
  }

  /**
   * Tests loading a non-existent YAML file.
   */
  @Test
  void testNonExistentYamlFile() {
    String fakeFilePath = "src/test/resources/yaml/non_existent.yml";
    assertThrows(IOException.class, () -> YamlLoader.loadYaml(fakeFilePath));
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
