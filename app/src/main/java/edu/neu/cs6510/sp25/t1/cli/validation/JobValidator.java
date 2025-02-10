package edu.neu.cs6510.sp25.t1.cli.validation;

import java.util.*;

/**
 * Validates the job configurations within a pipeline.
 * This validator ensures that all jobs:
 * <ul>
 *   <li>Have required fields (name, stage, image, script)</li>
 *   <li>Use correct data types for each field</li>
 *   <li>Have unique names within their stages</li>
 *   <li>Reference existing stages</li>
 * </ul>
 *
 * <p>Example job configuration:</p>
 * <pre>
 * job:
 *   - name: build-app
 *     stage: build
 *     image: maven:3.8
 *     script:
 *       - mvn clean
 *       - mvn package
 * </pre>
 */
public class JobValidator {
  private final List<String> stages;
  private final Map<String, Set<String>> stageJobs;
  private final Map<String, String> jobStages;

  /**
   * Initializes the validator with a list of valid stage names.
   *
   * @param stages The list of valid stage names defined in the pipeline
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
   * Validates a list of job configurations.
   * Checks for:
   * <ul>
   *   <li>Required fields presence and correct types</li>
   *   <li>Job name uniqueness within stages</li>
   *   <li>Valid stage references</li>
   *   <li>Valid script commands</li>
   * </ul>
   *
   * @param jobs List of job configurations to validate
   * @return true if all jobs are valid, false if any validation fails
   */
  public boolean validateJobs(List<Map<String, Object>> jobs) {
    if (jobs == null || jobs.isEmpty()) {
      System.err.println("pipeline.yaml:1:1: At least one job must be defined");
      return false;
    }

    for (Map<String, Object> job : jobs) {
      // Validate required fields and their types
      if (!validateRequiredFields(job)) {
        return false;
      }

      final String jobName = (String) job.get("name");
      final String jobStage = (String) job.get("stage");

      // Validate stage existence
      if (!validateStage(jobName, jobStage)) {
        return false;
      }

      // Validate job name uniqueness within stage
      if (!validateJobNameUniqueness(jobName, jobStage)) {
        return false;
      }

      // Validate script commands
      if (!validateScript(job)) {
        return false;
      }

      // Store job-stage mapping for future reference
      stageJobs.get(jobStage).add(jobName);
      jobStages.put(jobName, jobStage);
    }

    return true;
  }

  /**
   * Validates the presence and types of all required fields in a job configuration.
   *
   * @param job The job configuration to validate
   * @return true if all required fields are present and of correct type
   */
  private boolean validateRequiredFields(Map<String, Object> job) {
    // Validate name field
    if (!validateFieldType(job, "name", String.class)) {
      final Object value = job.get("name");
      System.err.println(String.format("pipeline.yaml:1:1: Wrong type for value '%s' in key 'name', expected String but got %s",
          value, value != null ? value.getClass().getSimpleName() : "null"));
      return false;
    }

    // Validate stage field
    if (!validateFieldType(job, "stage", String.class)) {
      final Object value = job.get("stage");
      System.err.println(String.format("pipeline.yaml:1:1: Wrong type for value '%s' in key 'stage', expected String but got %s",
          value, value != null ? value.getClass().getSimpleName() : "null"));
      return false;
    }

    // Validate image field
    if (!validateFieldType(job, "image", String.class)) {
      final Object value = job.get("image");
      System.err.println(String.format("pipeline.yaml:1:1: Wrong type for value '%s' in key 'image', expected String but got %s",
          value, value != null ? value.getClass().getSimpleName() : "null"));
      return false;
    }

    // Validate script field
    if (!validateFieldType(job, "script", List.class)) {
      final Object value = job.get("script");
      System.err.println(String.format("pipeline.yaml:1:1: Wrong type for value '%s' in key 'script', expected List but got %s",
          value, value != null ? value.getClass().getSimpleName() : "null"));
      return false;
    }

    return true;
  }

  /**
   * Validates if a field exists and is of the expected type.
   *
   * @param job The job configuration map
   * @param field The field name to validate
   * @param expectedType The expected class type of the field
   * @return true if the field exists and is of the correct type
   */
  private boolean validateFieldType(Map<String, Object> job, String field, Class<?> expectedType) {
    if (!job.containsKey(field)) {
      System.err.println(String.format("pipeline.yaml:1:1: Missing required field '%s'", field));
      return false;
    }

    final Object value = job.get(field);
    return value != null && expectedType.isInstance(value);
  }

  /**
   * Validates if a stage exists in the pipeline configuration.
   *
   * @param jobName The name of the job being validated
   * @param stage The stage to validate
   * @return true if the stage exists
   */
  private boolean validateStage(String jobName, String stage) {
    if (!stages.contains(stage)) {
      System.err.println(String.format("pipeline.yaml:1:1: Job '%s' references non-existent stage '%s'",
          jobName, stage));
      return false;
    }
    return true;
  }

  /**
   * Validates that a job name is unique within its stage.
   *
   * @param jobName The job name to validate
   * @param stage The stage containing the job
   * @return true if the job name is unique within the stage
   */
  private boolean validateJobNameUniqueness(String jobName, String stage) {
    if (stageJobs.get(stage).contains(jobName)) {
      System.err.println(String.format("pipeline.yaml:1:1: Duplicate job name '%s' in stage '%s'",
          jobName, stage));
      return false;
    }
    return true;
  }

  /**
   * Validates the script commands of a job.
   *
   * @param job The job configuration to validate
   * @return true if script commands are valid
   */
  private boolean validateScript(Map<String, Object> job) {
    @SuppressWarnings("unchecked")
    final List<Object> script = (List<Object>) job.get("script");

    if (script.isEmpty()) {
      System.err.println(String.format("pipeline.yaml:1:1: Job '%s' must have at least one script command",
          job.get("name")));
      return false;
    }

    // Validate each script command is a string
    for (Object command : script) {
      if (!(command instanceof String)) {
        System.err.println(String.format("pipeline.yaml:1:1: Script command '%s' must be a string in job '%s'",
            command, job.get("name")));
        return false;
      }
    }

    return true;
  }

  /**
   * Gets the mapping of job names to their stages.
   *
   * @return A map where key is job name and value is stage name
   */
  public Map<String, String> getJobStages() {
    return Collections.unmodifiableMap(jobStages);
  }
}