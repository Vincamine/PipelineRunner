package edu.neu.cs6510.sp25.t1.validation;

import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.error.MarkedYAMLException;
import org.yaml.snakeyaml.nodes.*;

import edu.neu.cs6510.sp25.t1.util.ErrorHandler;

import org.yaml.snakeyaml.error.Mark;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;

/**
 * YamlLoader is responsible for reading and parsing YAML files with location tracking.
 * It provides methods to:
 * <ul>
 *   <li>Load and parse YAML content</li>
 *   <li>Track source locations of YAML elements</li>
 *   <li>Handle YAML parsing errors with detailed location information</li>
 * </ul>
 */
public class YamlLoader {

  /**
   * Loads and parses a YAML file into a Map representation.
   * This method is maintained for backward compatibility.
   *
   * @param filePath The path to the YAML file
   * @return A Map containing the parsed YAML data
   * @throws IOException If the file cannot be read
   * @throws IllegalArgumentException If the YAML format is invalid
   */
  public static Map<String, Object> loadYaml(String filePath) throws IOException {
    final YamlLoadResult result = loadYamlWithLocations(filePath);
    return result.getData();
  }

  /**
   * Loads and parses a YAML file with location tracking.
   *
   * @param filePath The path to the YAML file
   * @return A YamlLoadResult containing both the parsed data and location information
   * @throws IOException If the file cannot be read
   * @throws IllegalArgumentException If the YAML format is invalid
   */
  public static YamlLoadResult loadYamlWithLocations(String filePath) throws IOException {
    try (FileInputStream inputStream = new FileInputStream(filePath)) {
      final String content = new String(inputStream.readAllBytes());

      final LoaderOptions options = new LoaderOptions();
      final Yaml yaml = new Yaml(new SafeConstructor(options));

      // First load to get the raw data
      final Map<String, Object> data = yaml.load(content);

      // Second parse to get the nodes and marks
      final Node rootNode = yaml.compose(new StringReader(content));
      final Map<String, Mark> locations = new HashMap<>();

      if (rootNode == null) {
        throw new IllegalArgumentException("Empty YAML document");
      }

      processNode(rootNode, "", locations);
      return new YamlLoadResult(data, locations);

    } catch (YAMLException e) {
      // Create location for error reporting
      final ErrorHandler.Location location;
      if (e instanceof MarkedYAMLException markedE) {
        location = new ErrorHandler.Location(
            filePath,
            markedE.getProblemMark().getLine() + 1,
            markedE.getProblemMark().getColumn() + 1,
            "yaml"
        );
      } else {
        location = new ErrorHandler.Location(filePath, 1, 1, "yaml");
      }

      final String errorMessage = ErrorHandler.formatException(
          location,
          "YAML parsing error: " + e.getMessage()
      );
      throw new IllegalArgumentException(errorMessage);
    }
  }

  /**
   * Processes a YAML node recursively, collecting location information.
   *
   * @param node The YAML node to process
   * @param path The current path in the YAML structure
   * @param locations Map to collect location information
   */
  private static void processNode(Node node, String path, Map<String, Mark> locations) {
    // Store the location information
    locations.put(path, node.getStartMark());

    if (node instanceof MappingNode mappingNode) {
      for (NodeTuple tuple : mappingNode.getValue()) {
        final String key = ((ScalarNode) tuple.getKeyNode()).getValue();
        final String newPath = path.isEmpty() ? key : path + "." + key;
        processNode(tuple.getValueNode(), newPath, locations);
      }
    } else if (node instanceof SequenceNode sequenceNode) {
      int index = 0;
      for (Node itemNode : sequenceNode.getValue()) {
        final String newPath = path + "[" + index + "]";
        processNode(itemNode, newPath, locations);
        index++;
      }
    }
  }
}