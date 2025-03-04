package edu.neu.cs6510.sp25.t1.common.api.response;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PipelineCheckResponseTest {

  @Test
  void testDefaultConstructor() {
    PipelineCheckResponse response = new PipelineCheckResponse();
    assertFalse(response.isValid(), "Default constructor should set valid to false");
    assertNotNull(response.getErrors(), "Errors list should not be null");
    assertTrue(response.getErrors().isEmpty(), "Default errors list should be empty");
  }

  @Test
  void testParameterizedConstructorWithValidValues() {
    PipelineCheckResponse response = new PipelineCheckResponse(true, List.of("Error1", "Error2"));

    assertTrue(response.isValid(), "Expected valid to be true");
    assertEquals(2, response.getErrors().size());
    assertEquals("Error1", response.getErrors().get(0));
    assertEquals("Error2", response.getErrors().get(1));
  }

  @Test
  void testParameterizedConstructorHandlesNullErrors() {
    PipelineCheckResponse response = new PipelineCheckResponse(false, null);

    assertFalse(response.isValid(), "Expected valid to be false");
    assertNotNull(response.getErrors(), "Errors list should not be null");
    assertTrue(response.getErrors().isEmpty(), "Errors list should be empty when null is passed");
  }

  @Test
  void testSetValid() {
    PipelineCheckResponse response = new PipelineCheckResponse();
    response.setValid(true);
    assertTrue(response.isValid());

    response.setValid(false);
    assertFalse(response.isValid());
  }

  @Test
  void testSetErrorsWithValidList() {
    PipelineCheckResponse response = new PipelineCheckResponse();
    response.setErrors(List.of("Issue1"));

    assertEquals(1, response.getErrors().size());
    assertEquals("Issue1", response.getErrors().get(0));
  }

  @Test
  void testSetErrorsHandlesNull() {
    PipelineCheckResponse response = new PipelineCheckResponse();
    response.setErrors(null);

    assertNotNull(response.getErrors(), "Errors list should not be null after setting to null");
    assertTrue(response.getErrors().isEmpty(), "Errors list should be empty after setting to null");
  }

  @Test
  void testToStringMethod() {
    PipelineCheckResponse response = new PipelineCheckResponse(true, List.of("Error A"));

    String expectedString = "PipelineCheckResponse{valid=true, errors=[Error A]}";
    assertEquals(expectedString, response.toString());
  }
}
