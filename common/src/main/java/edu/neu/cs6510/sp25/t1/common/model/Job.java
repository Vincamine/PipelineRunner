package edu.neu.cs6510.sp25.t1.common.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Represents a job in a CI/CD pipeline configuration.
 * Static configuration data.
 */
public class Job {
  private final String name;
  private final String image;
  private final List<String> script;
  private final List<String> needs; // Dependencies
  private final boolean allowFailure;

  /**
   * Constructor for JobConfig.
   *
   * @param name         Job name
   * @param image        Docker image
   * @param script       List of commands to run
   * @param needs        List of dependencies
   * @param allowFailure Whether the job can fail without failing the pipeline
   */
  @JsonCreator
  public Job(
          @JsonProperty("name") String name,
          @JsonProperty("image") String image,
          @JsonProperty("script") List<String> script,
          @JsonProperty("needs") List<String> needs,
          @JsonProperty("allowFailure") boolean allowFailure) {
    this.name = name;
    this.image = image;
    this.script = script;
    this.needs = needs != null ? needs : List.of();
    this.allowFailure = allowFailure;
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
}