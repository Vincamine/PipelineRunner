package edu.neu.cs6510.sp25.t1.common.validation.validator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.neu.cs6510.sp25.t1.common.model.Job;
import edu.neu.cs6510.sp25.t1.common.model.Stage;
import edu.neu.cs6510.sp25.t1.common.validation.error.ValidationException;
import edu.neu.cs6510.sp25.t1.common.validation.parser.YamlParser;

/**
 * JobValidator ensures that job definitions within a pipeline meet validation criteria.
 * <p>
 * It validates:
 *  - Required Fields**: Ensures `name`, `stage`, `image`, `script` exist.
 *  - Stage Reference**: Ensures jobs reference an existing stage.
 *  - Job Name Uniqueness**: Ensures jobs have unique names within each stage.
 *  - Script Validity**: Ensures `script` field contains valid commands.
 *  - Error Reporting**: Reports validation errors using `{filename}:{line}:{error-message}` format.
 * <p>
 * Usage:
 * - Call `JobValidator.validateJobs(stages, filename)` to validate all jobs.
 * - Throws `ValidationException` if any validation errors occur.
 */
public class JobValidator {

  /**
   * Validates all jobs in a pipeline configuration.
   *
   * @param stages   The list of stages in the pipeline.
   * @param filename The YAML filename for error reporting.
   * @throws ValidationException If validation fails.
   */
  public static void validateJobs(List<Stage> stages, String filename) throws ValidationException {
    List<String> errors = new ArrayList<>();
    Set<String> jobNames = new HashSet<>();
    Set<String> stageNames = new HashSet<>();

    for (Stage stage : stages) {
      stageNames.add(stage.getName());

      for (Job job : stage.getJobs()) {

        // Validate required fields
        if (job.getName() == null || job.getName().isEmpty()) {
          errors.add(formatError(filename, "name", "Job must have a name."));
        }
        if (job.getImage() == null || job.getImage().isEmpty()) {
          errors.add(formatError(filename, "image", "Job '" + job.getName() + "' must specify an image."));
        }
        if (job.getScript() == null || job.getScript().isEmpty()) {
          errors.add(formatError(filename, "script", "Job '" + job.getName() + "' must have at least one script command."));
        }

        // Validate unique job names
        if (jobNames.contains(job.getName())) {
          errors.add(formatError(filename, "name", "Duplicate job name found: " + job.getName()));
        }
        jobNames.add(job.getName());
      }
    }

    // Throw errors if validation fails
    if (!errors.isEmpty()) {
      throw new ValidationException(String.join("\n", errors));
    }
  }

  /**
   * Formats error messages.
   */
  private static String formatError(String filename, String fieldName, String message) {
    int line = YamlParser.getFieldLineNumber(filename, fieldName);
    return filename + ":" + line + ":" + message;
  }
}
