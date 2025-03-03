package edu.neu.cs6510.sp25.t1.common.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Represents a job in a CI/CD pipeline configuration.
 * Static configuration data.
 */
public class JobConfig {
  private final String name;
  private final String stageName;
  private final String image;
  private final List<String> script;
  private final List<String> needs; // Dependencies
  private final boolean allowFailure;
  private List<String> artifacts; // Optional artifacts

  /**
   * Constructor for JobConfig.
   *
   * @param name         Job name
   * @param stageName    Stage name
   * @param image        Docker image
   * @param script       List of commands to run
   * @param needs        List of dependencies
   * @param allowFailure Whether the job can fail without failing the pipeline
   * @param artifacts    Optional list of artifact paths to be stored
   */
  @JsonCreator
  public JobConfig(
          @JsonProperty("name") String name,
          @JsonProperty("stage") String stageName,
          @JsonProperty("image") String image,
          @JsonProperty("script") List<String> script,
          @JsonProperty("needs") List<String> needs,
          @JsonProperty("allowFailure") boolean allowFailure,
          @JsonProperty("artifacts") List<String> artifacts) {
    this.name = name;
    this.stageName = stageName;
    this.image = image;
    this.script = script;
    this.needs = needs != null ? needs : List.of();
    this.allowFailure = allowFailure;
    this.artifacts = artifacts != null ? artifacts : List.of();
  }

  /**
   * Getter for name.
   *
   * @return name
   */
  public String getName() {
    return name;
  }

  /**
   * Getter for stage name.
   *
   * @return stageName
   */
  public String getStageName() {
    return stageName;
  }

  /**
   * Getter for image.
   *
   * @return image
   */
  public String getImage() {
    return image;
  }

  /**
   * Getter for script.
   *
   * @return script
   */
  public List<String> getScript() {
    return script;
  }

  /**
   * Getter for needs.
   *
   * @return needs
   */
  public List<String> getNeeds() {
    return needs;
  }

  /**
   * Getter for allowFailure.
   *
   * @return allowFailure
   */
  public boolean isAllowFailure() {
    return allowFailure;
  }

  /**
   * Getter for artifacts.
   *
   * @return List of artifacts (empty list if none exist)
   */
  public List<String> getArtifacts() {
    return artifacts;
  }
}
