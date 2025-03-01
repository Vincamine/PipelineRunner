package edu.neu.cs6510.sp25.t1.common.model.execution;

import org.junit.jupiter.api.Test;

import java.util.List;

import edu.neu.cs6510.sp25.t1.common.model.definition.JobDefinition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class JobExecutionTest {

  @Test
  void testJobExecutionInitialization() {
    JobDefinition jobDef = new JobDefinition("test-job", "stage", "image", List.of(), List.of(), false);
    JobExecution jobExec = new JobExecution(jobDef, "PENDING", false, List.of());

    assertEquals("PENDING", jobExec.getStatus());
    assertFalse(jobExec.isAllowFailure());
  }

  @Test
  void testJobExecutionStart() {
    JobExecution jobExec = new JobExecution("test-job", "PENDING");
    jobExec.start();
    assertEquals("RUNNING", jobExec.getStatus());
  }

  @Test
  void testJobExecutionComplete() {
    JobExecution jobExec = new JobExecution("test-job", "PENDING");
    jobExec.complete("SUCCESS");
    assertEquals("SUCCESS", jobExec.getStatus());
  }
}
