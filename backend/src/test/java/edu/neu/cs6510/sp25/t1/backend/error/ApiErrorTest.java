package edu.neu.cs6510.sp25.t1.backend.error;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;


class ApiErrorTest {

  @Test
  void testApiErrorInitialization() {
    HttpStatus status = HttpStatus.BAD_REQUEST;
    String message = "Validation failed";
    String detail = "Field 'name' must not be blank";

    ApiError error = new ApiError(status, message, detail);

    assertEquals(status, error.getStatus());
    assertEquals(message, error.getMessage());
    assertEquals(detail, error.getDetail());

    assertNotNull(error.getTimestamp());
    assertTrue(error.getTimestamp().isBefore(LocalDateTime.now().plusSeconds(1)),
        "Timestamp should be set to current time");
  }
}
