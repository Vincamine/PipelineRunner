package edu.neu.cs6510.sp25.t1.cli.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.yaml.snakeyaml.error.Mark;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ErrorHandlerTest {
  private ErrorHandler.Location testLocation;

  @BeforeEach
  void setUp() {
    testLocation = new ErrorHandler.Location("pipeline.yaml", 10, 22, "jobs[0].name");
  }

  @Test
  void locationFormatShouldFollowPattern() {
    final String formatted = testLocation.format();
    assertEquals("pipeline.yaml:10:22", formatted);
    assertTrue(formatted.matches("^[\\w.-]+:\\d+:\\d+$"));
  }

  @ParameterizedTest
  @MethodSource("provideTypeErrorTestCases")
  void typeErrorShouldFormatCorrectly(Object value, Class<?> expectedType) {
    final String error = ErrorHandler.formatTypeError(testLocation, "testField", value, expectedType);

    assertEquals(
        String.format("pipeline.yaml:10:22: Wrong type for value '%s' in key 'testField', expected %s but got %s",
            value, expectedType.getSimpleName(),
            value != null ? value.getClass().getSimpleName() : "null"),
        error
    );
  }

  private static Stream<Arguments> provideTypeErrorTestCases() {
    return Stream.of(
        Arguments.of(123, String.class),
        Arguments.of("test", Integer.class),
        Arguments.of(null, String.class),
        Arguments.of(true, String.class)
    );
  }

  @Test
  void cycleErrorShouldIncludeAllMembers() {
    final List<String> cycle = Arrays.asList("job1", "job2", "job3");
    final String error = ErrorHandler.formatCycleError(testLocation, cycle);
    assertEquals(
        "pipeline.yaml:10:22: Dependency cycle detected: job1 -> job2 -> job3 -> job1",
        error
    );
  }

  @Test
  void missingFieldErrorShouldIncludeFieldName() {
    final String error = ErrorHandler.formatMissingFieldError(testLocation, "requiredField");
    assertEquals(
        "pipeline.yaml:10:22: Missing required field 'requiredField'",
        error
    );
  }

  @Test
  void createLocationShouldHandleNullMark() {
    final ErrorHandler.Location location = ErrorHandler.createLocation(null, "test.path");
    assertEquals("pipeline.yaml", location.getFilename());
    assertEquals(1, location.getLine());
    assertEquals(1, location.getColumn());
    assertEquals("test.path", location.getPath());
  }

  @Test
  void createLocationShouldConvertMarkCoordinates() {
    final Mark mark = new Mark("test", 0, 5, 10, new int[]{}, 0);
    final ErrorHandler.Location location = ErrorHandler.createLocation(mark, "test.path");
    assertEquals("pipeline.yaml", location.getFilename());
    assertEquals(6, location.getLine());
    assertEquals(11, location.getColumn());
    assertEquals("test.path", location.getPath());
  }
}
