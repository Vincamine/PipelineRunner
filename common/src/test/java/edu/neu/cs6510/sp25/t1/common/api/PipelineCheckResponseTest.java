package edu.neu.cs6510.sp25.t1.common.api;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PipelineCheckResponseTest {

    @Test
    void testPipelineCheckResponseConstructorAndGetters() {
        PipelineCheckResponse response = new PipelineCheckResponse(true, List.of("Error1"));

        assertTrue(response.isValid());
        assertEquals(List.of("Error1"), response.getErrors());
    }

    @Test
    void testPipelineCheckResponseWithDefaultConstructor() {
        PipelineCheckResponse response = new PipelineCheckResponse();

        assertFalse(response.isValid());
        assertNotNull(response.getErrors());
        assertTrue(response.getErrors().isEmpty());
    }

    @Test
    void testSetters() {
        PipelineCheckResponse response = new PipelineCheckResponse();
        response.setValid(true);
        response.setErrors(List.of("New Error"));

        assertTrue(response.isValid());
        assertEquals(List.of("New Error"), response.getErrors());
    }

    @Test
    void testToString() {
        PipelineCheckResponse response = new PipelineCheckResponse(true, List.of("Error1"));

        String result = response.toString();
        assertTrue(result.contains("Error1"));
        assertTrue(result.contains("true"));
    }
}
