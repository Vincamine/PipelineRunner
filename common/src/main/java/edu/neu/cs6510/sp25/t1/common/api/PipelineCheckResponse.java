package edu.neu.cs6510.sp25.t1.common.api;

import java.util.List;

/**
 * Represents the response from a pipeline check.
 * This class is used for checking pipeline correctness before execution.
 */
public class PipelineCheckResponse {
  private boolean valid; // Indicates if the pipeline is valid.
  private List<String> errors; // List of errors if the pipeline is not valid.

  /**
   * Default constructor. Ensures errors list is initialized.
   */
  public PipelineCheckResponse() {
    this.valid = false;
    this.errors = List.of(); // Prevents NullPointerException
  }

  /**
   * Constructor with parameters.
   *
   * @param valid  Whether the pipeline is valid.
   * @param errors List of errors (if any).
   */
  public PipelineCheckResponse(boolean valid, List<String> errors) {
    this.valid = valid;
    this.errors = (errors != null) ? errors : List.of();
  }

  /**
   * Check if the pipeline is valid.
   *
   * @return True if the pipeline is valid, false otherwise.
   */
  public boolean isValid() {
    return valid;
  }

  /**
   * Set the pipeline validity.
   *
   * @param valid Whether the pipeline is valid.
   */
  public void setValid(boolean valid) {
    this.valid = valid;
  }

  /**
   * Get the list of errors.
   *
   * @return List of errors. Never null.
   */
  public List<String> getErrors() {
    return errors;
  }

  /**
   * Set the errors.
   *
   * @param errors List of errors.
   */
  public void setErrors(List<String> errors) {
    this.errors = errors;
  }

  /**
   * String representation of the PipelineCheckResponse.
   *
   * @return String representation.
   */
  @Override
  public String toString() {
    return "PipelineCheckResponse{" +
            "valid=" + valid +
            ", errors=" + errors +
            '}';
  }
}
