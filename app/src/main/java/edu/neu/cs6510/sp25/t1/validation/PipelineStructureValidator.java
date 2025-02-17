package edu.neu.cs6510.sp25.t1.validation;

import org.yaml.snakeyaml.error.Mark;

import edu.neu.cs6510.sp25.t1.util.ErrorHandler;
import edu.neu.cs6510.sp25.t1.util.ErrorHandler.Location;

import java.util.List;
import java.util.Map;

/**
 * Validates the structure of a pipeline YAML configuration.
 * This validator ensures that:
 * <ul>
 *   <li>The root pipeline key exists and is a map.</li>
 *   <li>The pipeline has a valid name.</li>
 *   <li>The stages section exists and is a non-empty list of strings.</li>
 * </ul>
 *
 * <h2>Example of a valid pipeline structure:</h2>
 * <pre>
 * pipeline:
 *   name: my-pipeline
 *   stages:
 *     - build
 *     - test
 *     - deploy
 * </pre>
 */
public class PipelineStructureValidator {

  /**
   * Validates the structure of the given pipeline YAML configuration.
   * Ensures that the required keys (`pipeline`, `pipeline.name`, `pipeline.stages`) exist
   * and have the correct types.
   *
   * @param data      The parsed YAML data as a map containing pipeline definitions.
   * @param locations A map containing source locations for all elements in the configuration.
   * @param filePath  The path to the YAML file being validated.
   * @return {@code true} if the pipeline structure is valid, {@code false} otherwise.
   */
  public boolean validate(Map<String, Object> data, Map<String, Mark> locations, String filePath) {
    // Validate that the root "pipeline" key exists and is a Map
    final Location rootLocation = ErrorHandler.createLocation(
        filePath,
        locations.get("pipeline"),
        "pipeline"
    );

    if (!(data.get("pipeline") instanceof Map<?, ?> pipeline)) {
      System.err.println(ErrorHandler.formatTypeError(
          rootLocation,
          "pipeline",
          data.get("pipeline"),
          Map.class
      ));
      return false;
    }

    // Validate the presence of "name" within "pipeline"
    final Location nameLocation = ErrorHandler.createLocation(
        filePath,
        locations.get("pipeline.name"),
        "pipeline.name"
    );

    if (!pipeline.containsKey("name") || !(pipeline.get("name") instanceof String)) {
      System.err.println(ErrorHandler.formatTypeError(
          nameLocation,
          "name",
          pipeline.get("name"),
          String.class
      ));
      return false;
    }

    // Validate the presence of "stages" within "pipeline"
    final Location stagesLocation = ErrorHandler.createLocation(
        filePath,
        locations.get("pipeline.stages"),
        "pipeline.stages"
    );

    if (!pipeline.containsKey("stages") || !(pipeline.get("stages") instanceof List<?> rawStages)) {
      System.err.println(ErrorHandler.formatTypeError(
          stagesLocation,
          "stages",
          pipeline.get("stages"),
          List.class
      ));
      return false;
    }

    // Validate that all stages are non-empty strings
    final List<String> stages = rawStages.stream()
        .filter(String.class::isInstance)
        .map(String.class::cast)
        .toList();

    if (stages.size() != rawStages.size()) {
      // Find the first invalid stage (non-string) for precise error reporting
      for (int i = 0; i < rawStages.size(); i++) {
        final Object stage = rawStages.get(i);
        if (!(stage instanceof String)) {
          final Location stageLocation = ErrorHandler.createLocation(
              filePath,
              locations.get(String.format("pipeline.stages[%d]", i)),
              String.format("pipeline.stages[%d]", i)
          );
          System.err.println(ErrorHandler.formatTypeError(
              stageLocation,
              "stage",
              stage,
              String.class
          ));
          return false;
        }
      }
    }

    // Ensure that at least one stage is defined
    if (stages.isEmpty()) {
      System.err.println(ErrorHandler.formatException(
          stagesLocation,
          "Pipeline must define at least one stage."
      ));
      return false;
    }

    return true;
  }
}
