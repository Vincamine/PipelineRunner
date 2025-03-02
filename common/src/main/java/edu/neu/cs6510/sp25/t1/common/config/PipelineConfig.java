package edu.neu.cs6510.sp25.t1.common.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/**
 * Represents the structure of a CI/CD pipeline configuration.
 * This class is used for defining the pipeline stages and global variables.
 * Static configuration data.
 */
public class PipelineConfig {
  private final String name;
  private final List<StageConfig> stages;
  private final Map<String, String> globals; // Global variables

  /**
   * Default constructor for Jackson.
   * Needed to allow deserialization.
   */
  public PipelineConfig() {
    this.name = "";
    this.stages = List.of();
    this.globals = Map.of();
  }

  /**
   * Constructs a new PipelineDefinition instance.
   *
   * @param name    The name of the pipeline
   * @param stages  The list of stages in the pipeline
   * @param globals The global variables for the pipeline
   */
  @JsonCreator
  public PipelineConfig(
          @JsonProperty("name") String name,
          @JsonProperty("stages") List<StageConfig> stages,
          @JsonProperty("globals") Map<String, String> globals) {
    this.name = name;
    this.stages = stages != null ? stages : List.of();
    this.globals = globals != null ? globals : Map.of();
  }

  /**
   * Getter for name.
   *
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Getter for stages.
   *
   * @return the stages
   */
  public List<StageConfig> getStages() {
    return stages;
  }

  /**
   * Getter for globals.
   *
   * @return the globals
   */
  public Map<String, String> getGlobals() {
    return globals;
  }
}
