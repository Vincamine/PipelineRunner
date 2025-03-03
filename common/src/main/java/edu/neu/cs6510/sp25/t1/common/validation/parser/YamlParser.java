package edu.neu.cs6510.sp25.t1.common.validation.parser;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.error.MarkedYAMLException;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.nodes.*;

import edu.neu.cs6510.sp25.t1.common.model.Pipeline;
import edu.neu.cs6510.sp25.t1.common.validation.error.ValidationException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Parses YAML files into Java objects while tracking exact line/column numbers.
 * <p>
 * Enhancements:
 * - Extracts exact error locations using SnakeYAML's AST.
 * - Provides precise error messages with line/column information.
 * - Ensures better validation feedback for pipeline configurations.
 */
public class YamlParser {
  private static final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
  private static final Map<String, Mark> fieldLocations = new HashMap<>();

  /**
   * Parses a YAML file into a PipelineConfig object while tracking field locations.
   *
   * @param yamlFile The YAML file to parse.
   * @return Parsed PipelineConfig object.
   * @throws ValidationException If parsing fails.
   */
  public static Pipeline parseYaml(File yamlFile) throws ValidationException {
    try (FileInputStream inputStream = new FileInputStream(yamlFile)) {
      fieldLocations.clear();

      // Load raw content for line tracking
      String content = new String(inputStream.readAllBytes());
      Yaml yaml = new Yaml(new SafeConstructor(new LoaderOptions()));

      // First pass: Load YAML data into a Map
      Map<String, Object> data = yaml.load(content);

      // Second pass: Extract line/column locations
      Node rootNode = yaml.compose(new StringReader(content));
      if (rootNode == null) {
        throw new ValidationException(yamlFile.getName(), 1, "Empty YAML file.");
      }
      processNode(rootNode, "", fieldLocations);

      // Convert to PipelineConfig
      return yamlMapper.convertValue(data, Pipeline.class);

    } catch (JsonMappingException e) {
      int line = e.getLocation().getLineNr();
      throw new ValidationException(yamlFile.getName(), line, "Invalid YAML format: " + e.getOriginalMessage());
    } catch (YAMLException e) {
      int line = (e instanceof MarkedYAMLException markedE) ? markedE.getProblemMark().getLine() + 1 : 1;
      throw new ValidationException(yamlFile.getName(), line, "YAML parsing error: " + e.getMessage());
    } catch (IOException e) {
      throw new ValidationException(yamlFile.getName(), 0, "Failed to read file: " + e.getMessage());
    }
  }

  /**
   * Extracts the line number of a field from the YAML file.
   *
   * @param filename  The YAML file name.
   * @param fieldName The field whose line number is needed.
   * @return The line number or 1 if unknown.
   */
  public static int getFieldLineNumber(String filename, String fieldName) {
    return fieldLocations
            .getOrDefault(fieldName, new Mark(filename, 0, 1, 0, new char[]{}, 0)) // FIXED: Provide an empty char array
            .getLine() + 1;
  }


  /**
   * Recursively processes a YAML node to store field locations.
   *
   * @param node      The YAML node to process.
   * @param path      The current path in the YAML structure.
   * @param locations Map to store field locations.
   */
  private static void processNode(Node node, String path, Map<String, Mark> locations) {
    locations.put(path, node.getStartMark());

    if (node instanceof MappingNode mappingNode) {
      for (NodeTuple tuple : mappingNode.getValue()) {
        String key = ((ScalarNode) tuple.getKeyNode()).getValue();
        String newPath = path.isEmpty() ? key : path + "." + key;
        processNode(tuple.getValueNode(), newPath, locations);
      }
    } else if (node instanceof SequenceNode sequenceNode) {
      int index = 0;
      for (Node itemNode : sequenceNode.getValue()) {
        String newPath = path + "[" + index + "]";
        processNode(itemNode, newPath, locations);
        index++;
      }
    }
  }
}

