package edu.neu.cs6510.sp25.t1.cli.validation;

import java.util.List;
import java.util.Map;

/**
 * PipelineStructureValidator checks the structure of a pipeline YAML configuration.
 * It ensures required keys exist and validates pipeline stages.
 */
public class PipelineStructureValidator {

  /**
   * Validates the structure of the pipeline configuration.
   *
   * @param data The parsed YAML data.
   * @return true if the structure is valid, false otherwise.
   */
  public boolean validate(Map<String, Object> data) {
    if (!(data.get("pipeline") instanceof Map<?, ?> pipeline)) {
      System.err.println("Error: Missing or invalid 'pipeline' key.");
      return false;
    }

    if (!(pipeline.get("name") instanceof String)) {
      System.err.println("Error: 'pipeline' must have a valid 'name'.");
      return false;
    }

    if (!(pipeline.get("stages") instanceof List<?> rawStages)) {
      System.err.println("Error: 'stages' must be a list.");
      return false;
    }

    List<String> stages = rawStages.stream()
        .filter(String.class::isInstance)
        .map(String.class::cast)
        .toList();

    if (stages.isEmpty()) {
      System.err.println("Error: At least one stage must be defined.");
      return false;
    }

    return true;
  }
}
