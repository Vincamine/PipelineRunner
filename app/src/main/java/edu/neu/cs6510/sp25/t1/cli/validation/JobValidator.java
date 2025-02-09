package edu.neu.cs6510.sp25.t1.cli.validation;

import java.util.*;

/**
 * JobValidator checks the validity of jobs within the pipeline configuration.
 * It ensures jobs have required fields, exist in valid stages, and have unique names.
 */
public class JobValidator {
  private final List<String> stages;
  private final Map<String, Set<String>> stageJobs;
  private final Map<String, String> jobStages;

  /**
   * Initializes the validator with known stages.
   *
   * @param stages A list of valid stage names.
   */
  public JobValidator(List<String> stages) {
    this.stages = stages;
    this.stageJobs = new HashMap<>();
    this.jobStages = new HashMap<>();

    for (String stage : stages) {
      stageJobs.put(stage, new HashSet<>());
    }
  }

  /**
   * Validates the jobs and ensures:
   * - Job names are unique within a stage.
   * - Jobs reference existing stages.
   * - Required fields exist.
   *
   * @param jobs A list of job definitions.
   * @return true if all jobs are valid, false otherwise.
   */
  public boolean validateJobs(List<Map<String, Object>> jobs) {
    for (Map<String, Object> job : jobs) {
      if (!job.containsKey("name") || !(job.get("name") instanceof String jobName)) {
        System.err.println("Error: Each job must have a 'name'.");
        return false;
      }
      if (!job.containsKey("stage") || !(job.get("stage") instanceof String jobStage)) {
        System.err.println("Error: Each job must have a 'stage'.");
        return false;
      }
      if (!job.containsKey("image") || !(job.get("image") instanceof String)) {
        System.err.println("Error: Each job must specify a Docker 'image'.");
        return false;
      }
      if (!job.containsKey("script") || !(job.get("script") instanceof List)) {
        System.err.println("Error: Each job must have a 'script'.");
        return false;
      }

      if (!stages.contains(jobStage)) {
        System.err.println("Error: Job '" + jobName + "' references a non-existent stage '" + jobStage + "'.");
        return false;
      }

      if (!stageJobs.get(jobStage).add(jobName)) {
        System.err.println("Error: Duplicate job name '" + jobName + "' in stage '" + jobStage + "'.");
        return false;
      }

      jobStages.put(jobName, jobStage);
    }
    return true;
  }

  /**
   * Returns a map of job names and their corresponding stages.
   *
   * @return A map where the key is the job name, and the value is the stage name.
   */
  public Map<String, String> getJobStages() {
    return jobStages;
  }
}
