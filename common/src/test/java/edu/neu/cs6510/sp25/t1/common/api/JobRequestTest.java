//package edu.neu.cs6510.sp25.t1.common.api;
//
//import org.junit.jupiter.api.Test;
//
//import java.util.List;
//import java.util.Map;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//class JobRequestTest {
//
//  @Test
//  void testJobRequestConstructorAndGetters() {
//    JobRequest request = new JobRequest("job123", "pipeline1", "jobA", "commit456",
//            Map.of("VAR1", "value1"), List.of("artifact1"));
//
//    assertEquals("job123", request.getJobId());
//    assertEquals("pipeline1", request.getPipelineName());
//    assertEquals("jobA", request.getJobName());
//    assertEquals("commit456", request.getCommitHash());
//    assertEquals(Map.of("VAR1", "value1"), request.getEnvironmentVariables());
//    assertEquals(List.of("artifact1"), request.getArtifactPaths());
//  }
//
//  @Test
//  void testJobRequestNullInputs() {
//    JobRequest request = new JobRequest("job123", "pipeline1", "jobA", "commit456",
//            null, null);
//
//    assertNotNull(request.getEnvironmentVariables());
//    assertNotNull(request.getArtifactPaths());
//    assertTrue(request.getEnvironmentVariables().isEmpty());
//    assertTrue(request.getArtifactPaths().isEmpty());
//  }
//
//  @Test
//  void testToString() {
//    JobRequest request = new JobRequest("job123", "pipeline1", "jobA", "commit456",
//            Map.of("VAR1", "value1"), List.of("artifact1"));
//
//    String result = request.toString();
//    assertTrue(result.contains("job123"));
//    assertTrue(result.contains("pipeline1"));
//    assertTrue(result.contains("jobA"));
//  }
//}
