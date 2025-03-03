//package edu.neu.cs6510.sp25.t1.common.runtime;
//
//import org.junit.jupiter.api.Test;
//
//import java.util.List;
//
//import edu.neu.cs6510.sp25.t1.common.config.JobConfig;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertFalse;
//
//class JobRunStateTest {
//
//  @Test
//  void testJobExecutionInitialization() {
//    JobConfig jobDef = new JobConfig("test-job", "stage", "image", List.of(), List.of(), false);
//    JobRunState jobExec = new JobRunState(jobDef, "PENDING", false, List.of());
//
//    assertEquals("PENDING", jobExec.getStatus());
//    assertFalse(jobExec.isAllowFailure());
//  }
//
//  @Test
//  void testJobExecutionStart() {
//    JobRunState jobExec = new JobRunState("test-job", "PENDING");
//    jobExec.start();
//    assertEquals("RUNNING", jobExec.getStatus());
//  }
//
//  @Test
//  void testJobExecutionComplete() {
//    JobRunState jobExec = new JobRunState("test-job", "PENDING");
//    jobExec.complete("SUCCESS");
//    assertEquals("SUCCESS", jobExec.getStatus());
//  }
//}
