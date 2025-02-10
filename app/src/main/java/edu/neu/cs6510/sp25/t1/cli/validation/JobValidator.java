package edu.neu.cs6510.sp25.t1.cli.validation;

import edu.neu.cs6510.sp25.t1.cli.util.ErrorHandler;
import edu.neu.cs6510.sp25.t1.cli.util.ErrorHandler.Location;
import org.yaml.snakeyaml.error.Mark;
import java.util.*;

/**
 * Validates job configurations within a pipeline definition.
 * This validator ensures that all jobs meet the required criteria and constraints.
 *
 * <p>The validator performs the following checks:</p>
 * <ul>
 *   <li>Required fields (name, stage, image, script) are present and have correct types</li>
 *   <li>Job names are unique within their respective stages</li>
 *   <li>Referenced stages exist in the pipeline configuration</li>
 *   <li>Script commands are properly formatted and non-empty</li>
 * </ul>
 *
 * <p>Example job configuration:</p>
 * <pre>
 * jobs:
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
   * Initializes a new job validator with the specified list of valid stage names.
   *
   * @param stages The list of valid stage names defined in the pipeline configuration.
   *              These stages will be used to validate job stage references.
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
   * Validates a list of job configurations against the defined validation rules.
   *
   * @param jobs The list of job configurations to validate, where each job is represented
   *            as a Map containing the job's properties and their values
   * @param locations A map containing the source location information (line and column numbers)
   *                 for each element in the YAML configuration, keyed by their path
   * @return true if all jobs are valid according to the validation rules,
   *         false if any validation check fails
   */
  public boolean validateJobs(List<Map<String, Object>> jobs, Map<String, Mark> locations) {
    if (jobs == null || jobs.isEmpty()) {
      final Location location = ErrorHandler.createLocation(locations.get("jobs"), "jobs");
      System.err.println(ErrorHandler.formatMissingFieldError(location, "jobs"));
      return false;
    }

    for (int i = 0; i < jobs.size(); i++) {
      final Map<String, Object> job = jobs.get(i);
      final String jobPath = String.format("jobs[%d]", i);
      final Location jobLocation = ErrorHandler.createLocation(
          locations.get(jobPath),
          jobPath
      );

      if (!validateRequiredFields(job, jobLocation, locations)) {
        return false;
      }

      final String jobName = (String) job.get("name");
      final String jobStage = (String) job.get("stage");

      if (!validateStage(jobName, jobStage, jobLocation)) {
        return false;
      }

      if (!validateJobNameUniqueness(jobName, jobStage, jobLocation)) {
        return false;
      }

      if (!validateScript(job, jobLocation, locations)) {
        return false;
      }

      stageJobs.get(jobStage).add(jobName);
      jobStages.put(jobName, jobStage);
    }

    return true;
  }

  /**
   * Validates the presence and types of all required fields in a job configuration.
   *
   * @param job The job configuration map to validate
   * @param jobLocation The location information for the current job in the YAML file
   * @param locations Map containing source locations for all elements in the configuration
   * @return true if all required fields are present and of correct type, false otherwise
   */
  private boolean validateRequiredFields(
      Map<String, Object> job,
      Location jobLocation,
      Map<String, Mark> locations) {
    final String[] requiredFields = {"name", "stage", "image", "script"};
    final Class<?>[] expectedTypes = {String.class, String.class, String.class, List.class};

    for (int i = 0; i < requiredFields.length; i++) {
      final String field = requiredFields[i];
      final Class<?> expectedType = expectedTypes[i];

      if (!job.containsKey(field)) {
        System.err.println(ErrorHandler.formatMissingFieldError(
            jobLocation,
            field
        ));
        return false;
      }

      final Object value = job.get(field);
      final String fieldPath = jobLocation.getPath() + "." + field;
      final Location fieldLocation = ErrorHandler.createLocation(
          locations.get(fieldPath),
          fieldPath
      );

      if (value == null || !expectedType.isInstance(value)) {
        System.err.println(ErrorHandler.formatTypeError(
            fieldLocation,
            field,
            value,
            expectedType
        ));
        return false;
      }
    }

    return true;
  }

  /**
   * Validates that a job references an existing stage in the pipeline configuration.
   *
   * @param jobName The name of the job being validated
   * @param stage The stage name referenced by the job
   * @param location The location information for error reporting
   * @return true if the referenced stage exists, false otherwise
   */
  private boolean validateStage(String jobName, String stage, Location location) {
    if (!stages.contains(stage)) {
      System.err.println(ErrorHandler.formatException(
          location,
          String.format("Job '%s' references non-existent stage '%s'", jobName, stage)
      ));
      return false;
    }
    return true;
  }

  /**
   * Validates that a job name is unique within its stage.
   *
   * @param jobName The name of the job to validate
   * @param stage The stage containing the job
   * @param location The location information for error reporting
   * @return true if the job name is unique within its stage, false otherwise
   */
  private boolean validateJobNameUniqueness(String jobName, String stage, Location location) {
    if (stageJobs.get(stage).contains(jobName)) {
      System.err.println(ErrorHandler.formatException(
          location,
          String.format("Duplicate job name '%s' in stage '%s'", jobName, stage)
      ));
      return false;
    }
    return true;
  }

  /**
   * Validates the script commands of a job.
   * Ensures that:
   * <ul>
   *   <li>The script section is not empty</li>
   *   <li>All script commands are strings</li>
   * </ul>
   *
   * @param job The job configuration containing the script to validate
   * @param jobLocation The location information for the job
   * @param locations Map containing source locations for all elements
   * @return true if the script is valid, false otherwise
   */
  private boolean validateScript(
      Map<String, Object> job,
      Location jobLocation,
      Map<String, Mark> locations) {
    @SuppressWarnings("unchecked")
    final List<Object> script = (List<Object>) job.get("script");
    final String scriptPath = jobLocation.getPath() + ".script";
    final Location scriptLocation = ErrorHandler.createLocation(
        locations.get(scriptPath),
        scriptPath
    );

    if (script.isEmpty()) {
      System.err.println(ErrorHandler.formatException(
          scriptLocation,
          String.format("Job '%s' must have at least one script command", job.get("name"))
      ));
      return false;
    }

    for (int i = 0; i < script.size(); i++) {
      final Object command = script.get(i);
      final String commandPath = String.format("%s[%d]", scriptPath, i);
      final Location commandLocation = ErrorHandler.createLocation(
          locations.get(commandPath),
          commandPath
      );

      if (!(command instanceof String)) {
        System.err.println(ErrorHandler.formatTypeError(
            commandLocation,
            String.format("script[%d]", i),
            command,
            String.class
        ));
        return false;
      }
    }

    return true;
  }

  /**
   * Returns an unmodifiable map of job names to their corresponding stages.
   *
   * @return A map where the key is the job name and the value is the stage name
   */
  public Map<String, String> getJobStages() {
    return Collections.unmodifiableMap(jobStages);
  }
}