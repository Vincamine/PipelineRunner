package edu.neu.cs6510.sp25.t1.cli.validation;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class PipelineStructureValidator {
//  private final Set<String> pipelineNames;

  public PipelineStructureValidator(Set<String> pipelineNames) {
//    this.pipelineNames = pipelineNames;
  }

  public boolean validate(Map<String, Object> data) {
    if (!data.containsKey("pipeline") || !(data.get("pipeline") instanceof Map)) {
      System.err.println("Error: Missing or invalid 'pipeline' key.");
      return false;
    }

    Map<String, Object> pipeline = (Map<String, Object>) data.get("pipeline");

    if (!pipeline.containsKey("name") || !(pipeline.get("name") instanceof String)) {
      System.err.println("Error: 'pipeline' must have a valid 'name'.");
      return false;
    }

//    String pipelineName = (String) pipeline.get("name");
//    if (!pipelineNames.add(pipelineName)) {
//      System.err.println("Error: Pipeline name '" + pipelineName + "' is not unique.");
//      return false;
//    }

    if (!pipeline.containsKey("stages") || !(pipeline.get("stages") instanceof List<?> rawStages)) {
      System.err.println("Error: 'stages' must be a list.");
      return false;
    }

    try {
      List<String> stages = rawStages.stream()
          .filter(item -> item instanceof String)
          .map(item -> (String) item)
          .toList();

      // check yml stages
      if (stages.isEmpty()) {
        System.err.println("Error: At least one stage must be defined.");
        return false;
      }
      return true;

    } catch (ClassCastException e) {
      System.err.println("Error: 'stages' contains non-string values.");
      return false;
    }
  }
}


