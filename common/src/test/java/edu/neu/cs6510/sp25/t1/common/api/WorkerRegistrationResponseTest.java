package edu.neu.cs6510.sp25.t1.common.api;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class WorkerRegistrationResponseTest {

  @Test
  void testWorkerRegistrationResponseConstructorAndGetters() {
    WorkerRegistrationResponse response = new WorkerRegistrationResponse(true, "Worker registered successfully");

    assertTrue(response.isSuccess());
    assertEquals("Worker registered successfully", response.getMessage());
  }

  @Test
  void testWorkerRegistrationResponseDefaultConstructor() {
    WorkerRegistrationResponse response = new WorkerRegistrationResponse();

    assertFalse(response.isSuccess()); // Default should be false
    assertNull(response.getMessage()); // No message should be set by default
  }

  @Test
  void testWorkerRegistrationResponseSetters() {
    WorkerRegistrationResponse response = new WorkerRegistrationResponse();
    response.setSuccess(true);
    response.setMessage("Worker registered successfully");

    assertTrue(response.isSuccess());
    assertEquals("Worker registered successfully", response.getMessage());
  }
}
