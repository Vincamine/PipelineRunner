package edu.neu.cs6510.sp25.t1.cli.validation;

import edu.neu.cs6510.sp25.t1.cli.validation.ValidationResult;
import org.yaml.snakeyaml.Yaml;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PipelineValidator {
  private static final Logger LOGGER = Logger.getLogger(PipelineValidator.class.getName());

  public ValidationResult validatePipelineConfig(File yamlFile) {
    List<String> errors = new ArrayList<>();

    try {
      // Load and parse YAML
      Yaml yaml = new Yaml();
      Map<String, Object> config = yaml.load(new FileInputStream(yamlFile));

      // Validate required fields
      validateRequiredFields(config, errors);

      // Validate structure
      validateStructure(config, errors);

      return errors.isEmpty() ?
          ValidationResult.success() :
          ValidationResult.failure(errors);

    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Error parsing YAML file", e);
      errors.add("Failed to parse YAML file: " + e.getMessage());
      return ValidationResult.failure(errors);
    }
  }

  private void validateRequiredFields(Map<String, Object> config, List<String> errors) {
    if (config == null) {
      errors.add("Pipeline configuration is empty");
      return;
    }

    // Check for required top-level fields
    String[] requiredFields = {"name", "stages"};
    for (String field : requiredFields) {
      if (!config.containsKey(field)) {
        errors.add("Missing required field: " + field);
      }
    }
  }

  private void validateStructure(Map<String, Object> config, List<String> errors) {
    // Validate stages structure
    if (config.containsKey("stages")) {
      Object stages = config.get("stages");
      if (!(stages instanceof List)) {
        errors.add("'stages' must be a list");
        return;
      }

      List<?> stagesList = (List<?>) stages;
      for (int i = 0; i < stagesList.size(); i++) {
        Object stage = stagesList.get(i);
        if (!(stage instanceof Map)) {
          errors.add("Stage " + (i + 1) + " must be a map");
          continue;
        }

        Map<?, ?> stageMap = (Map<?, ?>) stage;
        if (!stageMap.containsKey("name")) {
          errors.add("Stage " + (i + 1) + " is missing required field 'name'");
        }
        if (!stageMap.containsKey("jobs")) {
          errors.add("Stage " + (i + 1) + " is missing required field 'jobs'");
        }
      }
    }
  }
}