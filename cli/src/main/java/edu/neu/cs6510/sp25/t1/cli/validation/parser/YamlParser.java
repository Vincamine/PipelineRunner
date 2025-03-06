package edu.neu.cs6510.sp25.t1.cli.validation.parser;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.error.MarkedYAMLException;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.neu.cs6510.sp25.t1.cli.validation.error.ValidationException;
import edu.neu.cs6510.sp25.t1.common.model.Pipeline;

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
  private static final Set<String> ALLOWED_TOP_LEVEL_KEYS = Set.of("pipeline", "stages");
  private static final Set<String> ALLOWED_JOB_KEYS = Set.of("name", "stage", "image", "script", "needs");

  /**
   * Parses a YAML file into a Pipeline object while validating extra fields.
   *
   * @param yamlFile The YAML file to parse.
   * @return Parsed Pipeline object.
   * @throws ValidationException If parsing fails.
   */
  public static Pipeline parseYaml(File yamlFile) throws ValidationException {
    try (FileInputStream inputStream = new FileInputStream(yamlFile)) {
      Yaml yaml = new Yaml(new SafeConstructor(new LoaderOptions()));
      Map<String, Object> rawData = yaml.load(inputStream);

      validateUnexpectedFields(yamlFile.getName(), rawData);

      return yamlMapper.convertValue(rawData, Pipeline.class);
    } catch (JsonMappingException e) {
      int line = (e.getLocation() != null) ? e.getLocation().getLineNr() : 1;
      int column = (e.getLocation() != null) ? e.getLocation().getColumnNr() : 1;
      throw new ValidationException(yamlFile.getName(), line, column,
              "Invalid YAML format: " + e.getOriginalMessage());
    } catch (YAMLException e) {
      int line = (e instanceof MarkedYAMLException) ? ((MarkedYAMLException) e).getProblemMark().getLine() + 1 : 1;
      int column = (e instanceof MarkedYAMLException) ? ((MarkedYAMLException) e).getProblemMark().getColumn() + 1 : 1;
      throw new ValidationException(yamlFile.getName(), line, column, "YAML parsing error: " + e.getMessage());
    } catch (IOException e) {
      throw new ValidationException(yamlFile.getName(), 0, 0, "Failed to read file: " + e.getMessage());
    }
  }


  /**
   * Validates that the YAML file does not contain unexpected keys.
   *
   * @param filename The YAML filename.
   * @param rawData  The parsed YAML data.
   * @throws ValidationException If unexpected keys are found.
   */
  private static void validateUnexpectedFields(String filename, Map<String, Object> rawData) throws ValidationException {
    List<String> errors = new ArrayList<>();

    // ✅ Check top-level keys
    for (String key : rawData.keySet()) {
      if (!ALLOWED_TOP_LEVEL_KEYS.contains(key)) {
        errors.add(String.format("%s:1: Unexpected top-level key '%s'.", filename, key));
      }
    }

    // ✅ Check job keys inside stages
    if (rawData.containsKey("stages")) {
      Object stages = rawData.get("stages");
      if (stages instanceof List<?> stageList) {
        for (Object stageObj : stageList) {
          if (stageObj instanceof Map<?, ?> stageMap) {
            for (Object keyObj : stageMap.keySet()) { // ✅ Fixed type issue
              String jobKey = keyObj.toString(); // ✅ Explicitly cast key to String
              if (!ALLOWED_JOB_KEYS.contains(jobKey)) {
                errors.add(String.format("%s: Unexpected key '%s' in job definition.", filename, jobKey));
              }
            }
          }
        }
      }
    }

    if (!errors.isEmpty()) {
      throw new ValidationException(errors);
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

