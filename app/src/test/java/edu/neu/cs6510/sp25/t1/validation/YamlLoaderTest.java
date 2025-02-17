package edu.neu.cs6510.sp25.t1.validation;

import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.error.Mark;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class YamlLoaderTest {

  @Test
  void testValidYamlFile() throws IOException, URISyntaxException {
    final Path yamlPath = getResourcePath("yaml/loader/valid_pipeline_loader_test.yml");
    final YamlLoadResult result = YamlLoader.loadYamlWithLocations(yamlPath.toString());

    final Map<String, Object> yamlData = result.getData();
    assertNotNull(yamlData);
    assertTrue(yamlData.containsKey("pipeline"));
    assertTrue(yamlData.containsKey("job"));

    @SuppressWarnings("unchecked")
    final Map<String, Object> pipeline = (Map<String, Object>) yamlData.get("pipeline");
    assertEquals("TestPipeline", pipeline.get("name"));


    final Map<String, Mark> locations = result.getLocations();
    assertNotNull(locations);
    assertTrue(locations.containsKey("pipeline"));
    assertTrue(locations.containsKey("pipeline.name"));
  }

  @Test
  void testValidYamlFileBackwardCompatibility() throws IOException, URISyntaxException {

    final Path yamlPath = getResourcePath("yaml/loader/valid_pipeline_loader_test.yml");
    final Map<String, Object> yamlData = YamlLoader.loadYaml(yamlPath.toString());

    assertNotNull(yamlData);
    assertTrue(yamlData.containsKey("pipeline"));
    assertTrue(yamlData.containsKey("job"));

    @SuppressWarnings("unchecked")
    final Map<String, Object> pipeline = (Map<String, Object>) yamlData.get("pipeline");
    assertEquals("TestPipeline", pipeline.get("name"));
  }

  @Test
  void testInvalidYamlFile() throws URISyntaxException {
    final Path yamlPath = getResourcePath("yaml/loader/invalid_pipeline_loader_test.yml");

    final Exception exception = assertThrows(IllegalArgumentException.class, () ->
        YamlLoader.loadYamlWithLocations(yamlPath.toString()));

    assertTrue(exception.getMessage().contains("YAML parsing error"));
  }

  @Test
  void testEmptyYamlFile() throws URISyntaxException {
    final Path yamlPath = getResourcePath("yaml/loader/empty_pipeline_loader_test.yml");

    final Exception exception = assertThrows(IllegalArgumentException.class, () ->
        YamlLoader.loadYamlWithLocations(yamlPath.toString()));

    assertTrue(exception.getMessage().contains("Empty YAML document"));
  }

  @Test
  void testComplexYamlStructure() throws IOException, URISyntaxException {
    final Path yamlPath = getResourcePath("yaml/loader/complex_pipeline_loader_test.yml");
    final YamlLoadResult result = YamlLoader.loadYamlWithLocations(yamlPath.toString());


    final Map<String, Mark> locations = result.getLocations();
    assertNotNull(locations);


    assertTrue(locations.containsKey("pipeline.stages[0]"));
    assertTrue(locations.containsKey("job[0]"));


    assertTrue(locations.containsKey("pipeline.config.timeout"));
    assertTrue(locations.containsKey("job[0].script[0]"));
  }

  @Test
  void testNonExistentYamlFile() {
    final String fakeFilePath = "src/test/resources/yaml/non_existent.yml";
    assertThrows(IOException.class, () -> YamlLoader.loadYamlWithLocations(fakeFilePath));
  }

  private Path getResourcePath(String resource) throws URISyntaxException {
    return Paths.get(ClassLoader.getSystemResource(resource).toURI());
  }
}