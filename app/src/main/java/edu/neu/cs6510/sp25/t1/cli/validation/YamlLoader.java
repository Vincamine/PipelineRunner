package edu.neu.cs6510.sp25.t1.cli.validation;

import edu.neu.cs6510.sp25.t1.cli.util.ErrorHandler;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.error.MarkedYAMLException;

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
      // Create location for error reporting
      final ErrorHandler.Location location;
      if (e instanceof MarkedYAMLException) {
        final MarkedYAMLException markedE = (MarkedYAMLException) e;
        location = new ErrorHandler.Location(
            filePath,
            markedE.getProblemMark().getLine() + 1,
            markedE.getProblemMark().getColumn() + 1,
            "yaml"
        );
      } else {
        location = new ErrorHandler.Location(filePath, 1, 1, "yaml");
      }

      final String errorMessage = ErrorHandler.formatMissingFieldError(
          location,
          "YAML parsing error: " + e.getMessage()
      );
      throw new IllegalArgumentException(errorMessage);
    }
  }
}