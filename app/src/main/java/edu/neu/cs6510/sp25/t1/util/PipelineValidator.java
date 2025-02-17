package edu.neu.cs6510.sp25.t1.util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import edu.neu.cs6510.sp25.t1.validation.YamlPipelineValidator;

/**
 * Utility class for validating pipeline YAML files.
 */
public class PipelineValidator {
  private final YamlPipelineValidator yamlPipelineValidator;
  private static final String PIPELINE_DIRECTORY = ".pipelines";

  public PipelineValidator(YamlPipelineValidator yamlPipelineValidator) {
    this.yamlPipelineValidator = yamlPipelineValidator;
  }

  public boolean validatePipelineFile(String yamlFilePath) {
    try {
      final Path yamlPath = Paths.get(yamlFilePath).toAbsolutePath().normalize();
      final String absolutePath = yamlPath.toString();

      if (!Files.exists(yamlPath)) {
        System.err.println("❌ YAML file not found: " + yamlFilePath);
        return false;
      }

      final Path parentDir = yamlPath.getParent();
      if (parentDir == null || !PIPELINE_DIRECTORY.equals(parentDir.getFileName().toString())) {
        System.err.println("⚠️ YAML file must be inside the '.pipelines/' folder");
        return false;
      }

      return yamlPipelineValidator.validatePipeline(yamlPath.toString());
    } catch (Exception e) {
      System.err.println("❌ Pipeline validation failed: " + e.getMessage());
      return false;
    }
  }
}
