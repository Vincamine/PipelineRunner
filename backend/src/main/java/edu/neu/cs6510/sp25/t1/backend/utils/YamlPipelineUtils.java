package edu.neu.cs6510.sp25.t1.backend.utils;

import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import edu.neu.cs6510.sp25.t1.common.logging.PipelineLogger;

/**
 * Utility class for reading and validating pipeline YAML files.
 */
public class YamlPipelineUtils {

  /**
   * Reads and parses a pipeline YAML file.
   *
   * @param filePath The path to the pipeline YAML file.
   * @return Parsed YAML as a Map.
   * @throws IOException If reading the file fails.
   */
  public static Map<String, Object> readPipelineYaml(String filePath) throws IOException {
    Path path = Paths.get(filePath).isAbsolute()
            ? Paths.get(filePath)
            : Paths.get(System.getProperty("user.dir")).resolve(filePath).normalize();
    PipelineLogger.info("Checking pipeline file: " + path);


    if (!Files.exists(path)) {
      PipelineLogger.error("Pipeline configuration file not found: " + filePath);
      throw new IllegalArgumentException("Pipeline configuration file not found: " + filePath);
    }

    try (FileInputStream fileInputStream = new FileInputStream(path.toFile())) {
      Yaml yaml = new Yaml();
      Map<String, Object> pipelineConfig = yaml.load(fileInputStream);

      if (pipelineConfig == null || pipelineConfig.isEmpty()) {
        PipelineLogger.error("Pipeline configuration is empty or malformed.");
        throw new IllegalArgumentException("Pipeline configuration is empty or malformed.");
      }

      PipelineLogger.info("Pipeline configuration successfully loaded from: " + filePath);
      return pipelineConfig;
    } catch (Exception e) {
      PipelineLogger.error("Error parsing pipeline YAML: " + e.getMessage());
      throw new IOException("Error parsing pipeline YAML: " + e.getMessage(), e);
    }
  }

  /**
   * Performs a minimal validation of the pipeline YAML structure.
   *
   * @param pipelineConfig The parsed pipeline configuration.
   * @throws IllegalArgumentException If validation fails.
   */
  public static void validatePipelineConfig(Map<String, Object> pipelineConfig) {
    if (!pipelineConfig.containsKey("stages")) {
      PipelineLogger.error("Invalid pipeline.yaml: Missing 'stages' field.");
      throw new IllegalArgumentException("Invalid pipeline.yaml: Missing 'stages' field.");
    }

    PipelineLogger.info("Pipeline YAML minimal validation successful: Structure is valid.");
  }
}
