package edu.neu.cs6510.sp25.t1.cli.validation;

import edu.neu.cs6510.sp25.t1.cli.util.ErrorHandler;
import edu.neu.cs6510.sp25.t1.cli.util.ErrorHandler.Location;
import org.yaml.snakeyaml.error.Mark;
import java.util.List;
import java.util.Map;

/**
 * Validates the structure of a pipeline YAML configuration.
 * This validator ensures that:
 * <ul>
 *   <li>The root pipeline key exists and is a map</li>
 *   <li>The pipeline has a valid name</li>
 *   <li>The stages section exists and is a non-empty list of strings</li>
 * </ul>
 *
 * <p>Example of a valid pipeline structure:</p>
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
   * Validates the overall structure of the pipeline configuration.
   *
   * @param data The parsed YAML data as a map
   * @param locations Map containing source locations for all elements in the configuration
   * @return true if the structure is valid, false otherwise
   */
  public boolean validate(Map<String, Object> data, Map<String, Mark> locations, String filePath) {
    // Validate pipeline key
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

    // Validate pipeline name
    final Location nameLocation = ErrorHandler.createLocation(
        filePath,
        locations.get("pipeline.name"),
        "pipeline.name"
    );

    if (!(pipeline.get("name") instanceof String)) {
      System.err.println(ErrorHandler.formatTypeError(
          nameLocation,
          "name",
          pipeline.get("name"),
          String.class
      ));
      return false;
    }

    // Validate stages list
    final Location stagesLocation = ErrorHandler.createLocation(
        filePath,
        locations.get("pipeline.stages"),
        "pipeline.stages"
    );

    if (!(pipeline.get("stages") instanceof List<?> rawStages)) {
      System.err.println(ErrorHandler.formatTypeError(
          stagesLocation,
          "stages",
          pipeline.get("stages"),
          List.class
      ));
      return false;
    }

    // Validate stage entries
    final List<String> stages = rawStages.stream()
        .filter(String.class::isInstance)
        .map(String.class::cast)
        .toList();

    if (stages.size() != rawStages.size()) {
      // Find the first non-string stage for error reporting
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

    if (stages.isEmpty()) {
      System.err.println(ErrorHandler.formatException(
          stagesLocation,
          "At least one stage must be defined"
      ));
      return false;
    }

    return true;
  }
}