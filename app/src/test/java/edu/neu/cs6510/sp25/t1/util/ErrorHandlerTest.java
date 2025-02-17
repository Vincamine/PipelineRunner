package edu.neu.cs6510.sp25.t1.util;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ErrorHandlerTest {

    @Test
    void testFormatTypeError() {
        final ErrorHandler.Location location = new ErrorHandler.Location("test.yaml", 5, 10, "pipeline.jobs");
        final String error = ErrorHandler.formatTypeError(location, "stages", 123, String.class);
        assertTrue(error.contains("wrong type for value '123' in key 'stages', expected String but got Integer"));
    }

    @Test
    void testFormatCycleError() {
        final ErrorHandler.Location location = new ErrorHandler.Location("test.yaml", 2, 5, "jobs.dependencies");
        final String error = ErrorHandler.formatCycleError(location, List.of("build", "test", "deploy"));
        assertTrue(error.contains("cycle detected in: build -> test -> deploy -> build"));
    }

    @Test
    void testFormatMissingFieldError() {
        final ErrorHandler.Location location = new ErrorHandler.Location("test.yaml", 3, 4, "stages");
        final String error = ErrorHandler.formatMissingFieldError(location, "pipeline");
        assertTrue(error.contains("required field 'pipeline' not found"));
    }

    @Test
    void testFormatFileError() {
        final ErrorHandler.Location location = new ErrorHandler.Location("pipeline.yaml", 1, 1, "root");
        final String error = ErrorHandler.formatFileError(location, "File not found");
        assertTrue(error.contains("File not found"));
    }
}