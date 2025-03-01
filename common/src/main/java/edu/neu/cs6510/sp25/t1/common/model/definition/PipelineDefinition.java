package edu.neu.cs6510.sp25.t1.common.model.definition;

import java.util.List;
import java.util.Map;

/**
 * Represents the structure of a CI/CD pipeline configuration.
 * This class is used for defining the pipeline stages and global variables.
 * Static configuration data.
 */
public class PipelineDefinition {
  private final String name;
  private final List<StageDefinition> stages;
  private final Map<String, String> globals; // Global variables

  /**
   * Constructs a new PipelineDefinition instance.
   *
   * @param name    The name of the pipeline
   * @param stages  The list of stages in the pipeline
   * @param globals The global variables for the pipeline
   */
  public PipelineDefinition(String name, List<StageDefinition> stages, Map<String, String> globals) {
    this.name = name;
    this.stages = stages;
    this.globals = globals;
  }

  /**
   * Getters for the fields.
   *
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Getters for the fields.
   *
   * @return the stages
   */
  public List<StageDefinition> getStages() {
    return stages;
  }

  /**
   * Getters for the fields.
   *
   * @return the globals
   */
  public Map<String, String> getGlobals() {
    return globals;
  }
}
