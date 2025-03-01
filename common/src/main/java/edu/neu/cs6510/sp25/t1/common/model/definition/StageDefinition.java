package edu.neu.cs6510.sp25.t1.common.model.definition;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Represents a stage in a CI/CD pipeline configuration.
 */
public class StageDefinition {
  private final String name;
  private final List<JobDefinition> jobs;

  /**
   * Constructor for StageDefinition.
   *
   * @param name Stage name
   * @param jobs List of jobs in the stage
   */
  @JsonCreator
  public StageDefinition(
          @JsonProperty("name") String name,
          @JsonProperty("jobs") List<JobDefinition> jobs) {
    this.name = name;
    this.jobs = jobs;
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
   * Getter for jobs.
   *
   * @return jobs
   */
  public List<JobDefinition> getJobs() {
    return jobs;
  }
}
