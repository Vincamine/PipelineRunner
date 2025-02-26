package edu.neu.cs6510.sp25.t1.execution;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for ExecutionStatus enum.
 */
public class ExecutionStatusTest {

  @Test
  public void testExecutionStatusValues() {
    assertEquals(ExecutionStatus.PENDING, ExecutionStatus.valueOf("PENDING"));
    assertEquals(ExecutionStatus.RUNNING, ExecutionStatus.valueOf("RUNNING"));
    assertEquals(ExecutionStatus.SUCCESSFUL, ExecutionStatus.valueOf("SUCCESSFUL"));
    assertEquals(ExecutionStatus.FAILED, ExecutionStatus.valueOf("FAILED"));
  }
}