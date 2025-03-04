package edu.neu.cs6510.sp25.t1.common.api.response;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WorkerRegistrationResponseTest {

  @Test
  void testDefaultConstructor() {
    WorkerRegistrationResponse response = new WorkerRegistrationResponse();
    assertFalse(response.isSuccess(), "Default constructor should initialize success to false");
    assertNull(response.getMessage(), "Default constructor should initialize message to null");
  }

  @Test
  void testParameterizedConstructor() {
    WorkerRegistrationResponse response = new WorkerRegistrationResponse(true, "Registration successful");

    assertTrue(response.isSuccess(), "Expected success to be true");
    assertEquals("Registration successful", response.getMessage());
  }

  @Test
  void testSetSuccess() {
    WorkerRegistrationResponse response = new WorkerRegistrationResponse();
    response.setSuccess(true);
    assertTrue(response.isSuccess(), "Expected success to be true after setting to true");

    response.setSuccess(false);
    assertFalse(response.isSuccess(), "Expected success to be false after setting to false");
  }

  @Test
  void testSetMessage() {
    WorkerRegistrationResponse response = new WorkerRegistrationResponse();
    response.setMessage("Worker registered");

    assertEquals("Worker registered", response.getMessage());

    response.setMessage(null);
    assertNull(response.getMessage(), "Expected message to be null after setting to null");
  }
}
