package edu.neu.cs6510.sp25.t1.cli.validation;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;

public class JobValidator {
  private final List<String> stages;
  private final Map<String, Set<String>> stageJobs;
  private final Map<String, String> jobStages;

  public JobValidator(List<String> stages) {
    this.stages = stages;
    this.stageJobs = new HashMap<>();
    this.jobStages = new HashMap<>();

    for (String stage : stages) {
      stageJobs.put(stage, new HashSet<>());
    }
  }

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

  public Map<String, String> getJobStages() {
    return jobStages;
  }
}
