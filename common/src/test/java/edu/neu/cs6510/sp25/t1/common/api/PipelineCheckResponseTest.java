package edu.neu.cs6510.sp25.t1.common.api;

import org.junit.jupiter.api.Test;

import edu.neu.cs6510.sp25.t1.common.api.PipelineCheckResponse;

import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class PipelineCheckResponseTest {

    @Test
    void testDefaultConstructor() {
        PipelineCheckResponse response = new PipelineCheckResponse();
        
        assertFalse(response.isValid(), "Default constructor should set valid to false.");
        assertNotNull(response.getErrors(), "Errors list should not be null.");
        assertTrue(response.getErrors().isEmpty(), "Default errors list should be empty.");
    }

    @Test
    void testParameterizedConstructorValidPipeline() {
        PipelineCheckResponse response = new PipelineCheckResponse(true, null);
        
        assertTrue(response.isValid(), "Valid pipeline should return true.");
        assertNotNull(response.getErrors(), "Errors list should not be null even when passed null.");
        assertTrue(response.getErrors().isEmpty(), "Errors list should be empty when passed null.");
    }

    @Test
    void testParameterizedConstructorInvalidPipeline() {
        List<String> errors = List.of("Syntax error", "Missing field");
        PipelineCheckResponse response = new PipelineCheckResponse(false, errors);
        
        assertFalse(response.isValid(), "Invalid pipeline should return false.");
        assertEquals(errors, response.getErrors(), "Errors list should match provided errors.");
    }

    @Test
    void testSetValid() {
        PipelineCheckResponse response = new PipelineCheckResponse();
        response.setValid(true);
        
        assertTrue(response.isValid(), "setValid(true) should update the valid flag.");
    }

    @Test
    void testSetErrors() {
        PipelineCheckResponse response = new PipelineCheckResponse();
        List<String> newErrors = List.of("New error 1", "New error 2");
        
        response.setErrors(newErrors);
        assertEquals(newErrors, response.getErrors(), "setErrors() should update the errors list.");
    }

    @Test
    void testToString() {
        PipelineCheckResponse response = new PipelineCheckResponse(false, List.of("Error 1"));
        
        String expectedString = "PipelineCheckResponse{valid=false, errors=[Error 1]}";
        assertEquals(expectedString, response.toString(), "toString() output should match expected format.");
    }
}
