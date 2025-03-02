package edu.neu.cs6510.sp25.t1.common.validation;

/**
 * Exception thrown when validation fails.
 */
public class ValidationException extends Exception {
  /**
   * Constructs a new ValidationException with the specified detail message.
   * @param message The detail message.
   */
  public ValidationException(String message) {
    super(message);
  }

  /**
   * Constructs a new ValidationException with the specified detail message and cause.
   * @param message The detail message.
   * @param cause The cause of the exception.
   */
  public ValidationException(String message, Throwable cause) {
    super(message, cause);
  }
}
