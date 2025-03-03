package edu.neu.cs6510.sp25.t1.common.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Represents the structure of a CI/CD pipeline configuration.
 * This class is used for defining the pipeline stages and global variables.
 * Static configuration data.
 */
public class Pipeline {
  private final String name;
  private final List<Stage> stages;

  /**
   * Default constructor for Jackson.
   * Needed to allow deserialization.
   */
  public Pipeline() {
    this.name = "";
    this.stages = List.of();
  }

  /**
   * Constructs a new PipelineDefinition instance.
   *
   * @param name   The name of the pipeline
   * @param stages The list of stages in the pipeline
   */
  @JsonCreator
  public Pipeline(
          @JsonProperty("name") String name,
          @JsonProperty("stages") List<Stage> stages) {
    this.name = name;
    this.stages = stages != null ? stages : List.of();
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
  public List<Stage> getStages() {
    return stages;
  }
}
