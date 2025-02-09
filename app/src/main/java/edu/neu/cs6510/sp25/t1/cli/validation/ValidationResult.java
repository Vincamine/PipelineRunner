package edu.neu.cs6510.sp25.t1.cli.validation;

import java.util.ArrayList;
import java.util.List;

public class ValidationResult {
  private final boolean valid;
  private final List<String> errors;

  public ValidationResult() {
    this.valid = true;
    this.errors = new ArrayList<>();
  }

  public ValidationResult(List<String> errors) {
    this.valid = errors.isEmpty();
    this.errors = errors;
  }

  public boolean isValid() {
    return valid;
  }

  public List<String> getErrors() {
    return errors;
  }

  public static ValidationResult success() {
    return new ValidationResult();
  }

  public static ValidationResult failure(List<String> errors) {
    return new ValidationResult(errors);
  }
}