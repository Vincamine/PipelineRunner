package edu.neu.cs6510.sp25.t1.common.validation;
// package edu.neu.cs6510.sp25.t1.validation;

// import org.yaml.snakeyaml.error.Mark;
// import edu.neu.cs6510.sp25.t1.util.ErrorHandler;
// import edu.neu.cs6510.sp25.t1.util.ErrorHandler.Location;

// import java.util.List;
// import java.util.Map;
// import java.util.Set;
// import java.util.ArrayList;
// import java.util.HashMap;
// import java.util.HashSet;
// import java.util.Collections;

// /**
//  * Validates job configurations within a pipeline definition.
//  * This validator ensures that all jobs meet the required criteria and
//  * constraints.
//  *
//  * The validator performs the following checks:
//  * - Ensures required fields ({@code name}, {@code stage}, {@code image},
//  * {@code script}) are present and correctly typed.
//  * - Ensures job names are unique within each stage.
//  * - Ensures referenced stages exist in the pipeline configuration.
//  * - Ensures script commands are properly formatted and non-empty.
//  *
//  */
// public class JobValidator {
//   private final List<String> stages;
//   private final Map<String, Set<String>> stageJobs;
//   private final Map<String, String> jobStages;

//   /**
//    * Initializes a new job validator with the specified list of valid stage names.
//    *
//    * @param stages The list of valid stage names defined in the pipeline
//    *               configuration.
//    *               These stages will be used to validate job stage references.
//    */
//   public JobValidator(List<String> stages) {
//     this.stages = stages != null ? new ArrayList<>(stages) : new ArrayList<>();
//     this.stageJobs = new HashMap<>();
//     this.jobStages = new HashMap<>();

//     for (String stage : this.stages) {
//       stageJobs.put(stage, new HashSet<>());
//     }
//   }

//   /**
//    * Validates a list of job configurations against the defined validation rules.
//    *
//    * @param jobs      The list of job configurations to validate, where each job
//    *                  is represented
//    *                  as a Map containing the job's properties and their values.
//    * @param locations A map containing the source location information (line and
//    *                  column numbers)
//    *                  for each element in the YAML configuration, keyed by their
//    *                  path.
//    * @param filename  The YAML filename for error reporting.
//    * @return {@code true} if all jobs are valid according to the validation rules,
//    *         {@code false} if any validation check fails.
//    */
//   public boolean validateJobs(List<Map<String, Object>> jobs, Map<String, Mark> locations, String filename) {
//     if (jobs == null || jobs.isEmpty()) {
//       final Location location = ErrorHandler.createLocation(filename, locations.get("jobs"), "jobs");
//       System.err.println(ErrorHandler.formatMissingFieldError(location, "jobs"));
//       return false;
//     }

//     for (int i = 0; i < jobs.size(); i++) {
//       final Map<String, Object> job = jobs.get(i);
//       final String jobPath = String.format("jobs[%d]", i);
//       final Location jobLocation = ErrorHandler.createLocation(filename, locations.getOrDefault(jobPath, null),
//           jobPath);

//       if (!validateRequiredFields(job, jobLocation, locations, filename)) {
//         return false;
//       }
//       if (!validateStage(job, jobLocation)) {
//         return false;
//       }
//       if (!validateJobNameUniqueness(job, jobLocation)) {
//         return false;
//       }
//       if (!validateScript(job, jobLocation, locations, filename)) {
//         return false;
//       }

//       final String jobName = (String) job.get("name");
//       final String jobStage = (String) job.get("stage");

//       stageJobs.get(jobStage).add(jobName);
//       jobStages.put(jobName, jobStage);
//     }

//     return true;
//   }

//   /**
//    * Validates the presence and types of all required fields in a job
//    * configuration.
//    *
//    * @param job         The job configuration map to validate.
//    * @param jobLocation The location information for the current job in the YAML
//    *                    file.
//    * @param locations   Map containing source locations for all elements in the
//    *                    configuration.
//    * @param filename    The YAML filename for error reporting.
//    * @return {@code true} if all required fields are present and correctly typed,
//    *         {@code false} otherwise.
//    */
//   private boolean validateRequiredFields(Map<String, Object> job, Location jobLocation, Map<String, Mark> locations,
//       String filename) {
//     final String[] requiredFields = { "name", "stage", "image", "script" };
//     final Class<?>[] expectedTypes = { String.class, String.class, String.class, List.class };

//     for (int i = 0; i < requiredFields.length; i++) {
//       final String field = requiredFields[i];
//       final Class<?> expectedType = expectedTypes[i];

//       if (!job.containsKey(field)) {
//         System.err.println(ErrorHandler.formatMissingFieldError(jobLocation, field));
//         return false;
//       }

//       final Object value = job.get(field);
//       final String fieldPath = jobLocation.getPath() + "." + field;
//       final Location fieldLocation = ErrorHandler.createLocation(filename, locations.getOrDefault(fieldPath, null),
//           fieldPath);

//       if (value == null || !expectedType.isInstance(value)) {
//         System.err.println(ErrorHandler.formatTypeError(fieldLocation, field, value, expectedType));
//         return false;
//       }
//     }
//     return true;
//   }

//   /**
//    * Validates that a job references an existing stage in the pipeline
//    * configuration.
//    *
//    * @param job      The job map containing the "name" and "stage" fields.
//    * @param location The location information for error reporting.
//    * @return {@code true} if the referenced stage exists, {@code false} otherwise.
//    */
//   private boolean validateStage(Map<String, Object> job, Location location) {
//     final String jobName = (String) job.get("name");
//     final String stage = (String) job.get("stage");

//     if (!stages.contains(stage)) {
//       System.err.println(ErrorHandler.formatException(location,
//           String.format("Job '%s' references non-existent stage '%s'", jobName, stage)));
//       return false;
//     }
//     return true;
//   }

//   /**
//    * Validates that a job name is unique within its stage.
//    *
//    * @param job      The job map containing the "name" and "stage" fields.
//    * @param location The location information for error reporting.
//    * @return {@code true} if the job name is unique within its stage,
//    *         {@code false} otherwise.
//    */
//   private boolean validateJobNameUniqueness(Map<String, Object> job, Location location) {
//     final String jobName = (String) job.get("name");
//     final String stage = (String) job.get("stage");

//     if (stageJobs.get(stage).contains(jobName)) {
//       System.err.println(ErrorHandler.formatException(location,
//           String.format("Duplicate job name '%s' in stage '%s'", jobName, stage)));
//       return false;
//     }
//     return true;
//   }

//   /**
//    * Validates the script commands of a job.
//    *
//    * @param job         The job configuration containing the script to validate.
//    * @param jobLocation The location information for the job.
//    * @param locations   Map containing source locations for all elements.
//    * @param filename    The YAML filename for error reporting.
//    * @return {@code true} if the script is valid, {@code false} otherwise.
//    */
//   private boolean validateScript(Map<String, Object> job, Location jobLocation, Map<String, Mark> locations,
//       String filename) {
//     @SuppressWarnings("unchecked")
//     final List<Object> script = (List<Object>) job.get("script");

//     if (script == null || script.isEmpty()) {
//       System.err.println(ErrorHandler.formatException(jobLocation,
//           String.format("Job '%s' must have at least one script command", job.get("name"))));
//       return false;
//     }

//     for (Object command : script) {
//       if (!(command instanceof String)) {
//         System.err.println(ErrorHandler.formatTypeError(jobLocation, "script", command, String.class));
//         return false;
//       }
//     }
//     return true;
//   }

//   /** @return A map of job names to their corresponding stages. */
//   public Map<String, String> getJobStages() {
//     return Collections.unmodifiableMap(jobStages);
//   }
// }
