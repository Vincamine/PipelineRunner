package edu.neu.cs6510.sp25.t1.common.api.request;

import org.junit.jupiter.api.Test;

import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UpdateExecutionStateRequestTest {

  @Test
  void testConstructorWithValidInputs() {
    UpdateExecutionStateRequest request = new UpdateExecutionStateRequest("pipeline-1", ExecutionStatus.RUNNING);
    assertEquals("pipeline-1", request.getName());
    assertEquals(ExecutionStatus.RUNNING, request.getState());
  }

  @Test
  void testConstructorThrowsExceptionForNullName() {
    Exception exception = assertThrows(IllegalArgumentException.class,
            () -> new UpdateExecutionStateRequest(null, ExecutionStatus.SUCCESS));

    assertEquals("Execution target name cannot be null or empty.", exception.getMessage());
  }

  @Test
  void testConstructorThrowsExceptionForEmptyName() {
    Exception exception1 = assertThrows(IllegalArgumentException.class,
            () -> new UpdateExecutionStateRequest("", ExecutionStatus.FAILED));

    assertEquals("Execution target name cannot be null or empty.", exception1.getMessage());

    Exception exception2 = assertThrows(IllegalArgumentException.class,
            () -> new UpdateExecutionStateRequest("   ", ExecutionStatus.FAILED));

    assertEquals("Execution target name cannot be null or empty.", exception2.getMessage());
  }

  @Test
  void testGetters() {
    UpdateExecutionStateRequest request = new UpdateExecutionStateRequest("stage-1", ExecutionStatus.PENDING);
    assertEquals("stage-1", request.getName());
    assertEquals(ExecutionStatus.PENDING, request.getState());
  }

  @Test
  void testToStringMethod() {
    UpdateExecutionStateRequest request = new UpdateExecutionStateRequest("job-1", ExecutionStatus.SUCCESS);

    String expectedString = "UpdateExecutionStateRequest{name='job-1', state=SUCCESS}";
    assertEquals(expectedString, request.toString());
  }
}
