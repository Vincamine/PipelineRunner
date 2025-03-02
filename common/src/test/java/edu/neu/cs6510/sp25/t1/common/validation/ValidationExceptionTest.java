package edu.neu.cs6510.sp25.t1.common.validation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ValidationExceptionTest {

  @Test
  void shouldCreateExceptionWithMessage() {
    ValidationException exception = new ValidationException("Validation error occurred");
    assertEquals("Validation error occurred", exception.getMessage());
  }

  @Test
  void shouldCreateExceptionWithMessageAndCause() {
    Throwable cause = new RuntimeException("Root cause");
    ValidationException exception = new ValidationException("Validation error", cause);

    assertEquals("Validation error", exception.getMessage());
    assertEquals(cause, exception.getCause());
  }
}
