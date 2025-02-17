package edu.neu.cs6510.sp25.t1.util;

import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.error.Mark;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ErrorHandlerTest {

    @Test
    void testFormatTypeError() {
        ErrorHandler.Location location = new ErrorHandler.Location("test.yaml", 5, 10, "pipeline.jobs");
        String error = ErrorHandler.formatTypeError(location, "stages", 123, String.class);
        assertTrue(error.contains("wrong type for value '123' in key 'stages', expected String but got Integer"));
    }

    @Test
    void testFormatCycleError() {
        ErrorHandler.Location location = new ErrorHandler.Location("test.yaml", 2, 5, "jobs.dependencies");
        String error = ErrorHandler.formatCycleError(location, List.of("build", "test", "deploy"));
        assertTrue(error.contains("cycle detected in: build -> test -> deploy -> build"));
    }

    @Test
    void testFormatMissingFieldError() {
        ErrorHandler.Location location = new ErrorHandler.Location("test.yaml", 3, 4, "stages");
        String error = ErrorHandler.formatMissingFieldError(location, "pipeline");
        assertTrue(error.contains("required field 'pipeline' not found"));
    }

    @Test
    void testFormatFileError() {
        ErrorHandler.Location location = new ErrorHandler.Location("pipeline.yaml", 1, 1, "root");
        String error = ErrorHandler.formatFileError(location, "File not found");
        assertTrue(error.contains("File not found"));
    }
}