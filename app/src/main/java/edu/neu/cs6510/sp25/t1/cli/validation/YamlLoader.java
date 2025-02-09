package edu.neu.cs6510.sp25.t1.cli.validation;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

/**
 * YamlLoader is responsible for reading and parsing a YAML file.
 * It ensures the file is correctly formatted and returns the parsed content.
 */
public class YamlLoader {
  /**
   * Loads and parses a YAML file into a Map representation.
   *
   * @param filePath The path to the YAML file.
   * @return A Map containing the parsed YAML data.
   * @throws IOException If the file cannot be read.
   * @throws IllegalArgumentException If the YAML format is invalid.
   */
  public static Map<String, Object> loadYaml(String filePath) throws IOException {
    final Yaml yaml = new Yaml();
    try (FileInputStream inputStream = new FileInputStream(filePath)) {
      return yaml.load(inputStream);
    } catch (YAMLException e) {
      throw new IllegalArgumentException("Invalid YAML format: " + e.getMessage());
    }
  }
}