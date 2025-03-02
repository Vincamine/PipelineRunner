package edu.neu.cs6510.sp25.t1.common.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Represents a stage in a CI/CD pipeline configuration.
 */
public class StageConfig {
  private final String name;
  private final List<JobConfig> jobs;

  /**
   * Constructor for StageDefinition.
   *
   * @param name Stage name
   * @param jobs List of jobs in the stage
   */
  @JsonCreator
  public StageConfig(
          @JsonProperty("name") String name,
          @JsonProperty("jobs") List<JobConfig> jobs) {
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
  public List<JobConfig> getJobs() {
    return jobs;
  }
}
