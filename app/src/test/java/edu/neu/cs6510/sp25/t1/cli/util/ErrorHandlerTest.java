package edu.neu.cs6510.sp25.t1.cli.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.yaml.snakeyaml.error.Mark;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ErrorHandlerTest {
  private ErrorHandler.Location testLocation;

  @BeforeEach
  void setUp() {
    testLocation = new ErrorHandler.Location("pipeline.yaml", 10, 22, "jobs[0].name");
  }

  @Test
  void locationFormat_ShouldFormatCorrectly() {
    final String formatted = testLocation.format();
    assertEquals("pipeline.yaml:10:22", formatted);
  }

  @Test
  void formatTypeError_ShouldIncludeAllComponents() {
    final String error = ErrorHandler.formatTypeError(testLocation, "name", 123, String.class);
    final String expected = "pipeline.yaml:10:22: Wrong type for value '123' in key 'name', expected String but got Integer";
    assertEquals(expected, error);
  }

  @Test
  void formatTypeError_WithNullValue_ShouldHandleGracefully() {
    final String error = ErrorHandler.formatTypeError(testLocation, "name", null, String.class);
    final String expected = "pipeline.yaml:10:22: Wrong type for value 'null' in key 'name', expected String but got null";
    assertEquals(expected, error);
  }

  @Test
  void formatCycleError_ShouldFormatCycleCorrectly() {
    final List<String> cycle = Arrays.asList("job1", "job2", "job3");
    final String error = ErrorHandler.formatCycleError(testLocation, cycle);
    final String expected = "pipeline.yaml:10:22: Dependency cycle detected: job1 -> job2 -> job3 -> job1";
    assertEquals(expected, error);
  }

  @Test
  void formatMissingFieldError_ShouldFormatCorrectly() {
    final String error = ErrorHandler.formatMissingFieldError(testLocation, "script");
    final String expected = "pipeline.yaml:10:22: Missing required field 'script'";
    assertEquals(expected, error);
  }

  @Test
  void createLocation_WithNullMark_ShouldUseDefaultValues() {
    final ErrorHandler.Location location = ErrorHandler.createLocation(null, "jobs[0]");
    assertEquals("pipeline.yaml", location.getFilename());
    assertEquals(1, location.getLine());
    assertEquals(1, location.getColumn());
    assertEquals("jobs[0]", location.getPath());
  }

  @Test
  void createLocation_WithValidMark_ShouldUseMarkValues() {
    final Mark mark = new Mark("test", 0, 10, 20, new int[]{}, 0);
    final ErrorHandler.Location location = ErrorHandler.createLocation(mark, "jobs[0]");
    assertEquals("pipeline.yaml", location.getFilename());
    assertEquals(11, location.getLine()); // Mark is 0-based, Location is 1-based
    assertEquals(21, location.getColumn()); // Mark is 0-based, Location is 1-based
    assertEquals("jobs[0]", location.getPath());
  }
}