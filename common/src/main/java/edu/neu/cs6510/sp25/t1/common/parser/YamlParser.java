package edu.neu.cs6510.sp25.t1.common.parser;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.IOException;

import edu.neu.cs6510.sp25.t1.common.config.PipelineConfig;
import edu.neu.cs6510.sp25.t1.common.validation.ValidationException;

/**
 * Parses YAML files into Java objects using Jackson.
 */
public class YamlParser {
  private static final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

  /**
   * Parses a YAML file into a PipelineDefinition object.
   *
   * @param yamlFile The YAML file to parse.
   * @return Parsed PipelineDefinition object.
   * @throws ValidationException If parsing fails.
   */
  public static PipelineConfig parseYaml(File yamlFile) throws ValidationException {
    try {
      return yamlMapper.readValue(yamlFile, PipelineConfig.class);
    } catch (IOException e) {
      throw new ValidationException("Failed to parse YAML file: " + yamlFile.getName(), e);
    }
  }
}
