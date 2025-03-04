package edu.neu.cs6510.sp25.t1.common.api.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JobRequestTest {

  private JobRequest jobRequest;

  @BeforeEach
  void setUp() {
    jobRequest = new JobRequest(
            "job-123",
            "pipeline-1",
            "build-job",
            "abcd1234",
            Map.of("RUN_LOCAL", "true", "SCRIPT", "echo Hello;echo World", "IMAGE", "ubuntu:latest"),
            List.of("artifact1", "artifact2")
    );
  }

  @Test
  void testConstructorAndGetters() {
    assertEquals("job-123", jobRequest.getJobId());
    assertEquals("pipeline-1", jobRequest.getPipelineName());
    assertEquals("build-job", jobRequest.getJobName());
    assertEquals("abcd1234", jobRequest.getCommitHash());
    assertEquals(Map.of("RUN_LOCAL", "true", "SCRIPT", "echo Hello;echo World", "IMAGE", "ubuntu:latest"), jobRequest.getEnvironmentVariables());
    assertEquals(List.of("artifact1", "artifact2"), jobRequest.getArtifactPaths());
  }

  @Test
  void testConstructorHandlesNullInputs() {
    JobRequest request = new JobRequest("job-456", "pipeline-2", "deploy-job", "xyz5678", null, null);
    assertNotNull(request.getEnvironmentVariables());
    assertTrue(request.getEnvironmentVariables().isEmpty());
    assertNotNull(request.getArtifactPaths());
    assertTrue(request.getArtifactPaths().isEmpty());
    assertNotNull(request.getNeeds());
    assertTrue(request.getNeeds().isEmpty());
  }

  @Test
  void testIsRunLocal() {
    assertTrue(jobRequest.isRunLocal());

    JobRequest request = new JobRequest("job-789", "pipeline-3", "test-job", "lmno123", Map.of(), List.of());
    assertFalse(request.isRunLocal());
  }

  @Test
  void testGetScript() {
    List<String> script = jobRequest.getScript();
    assertEquals(2, script.size());
    assertEquals("echo Hello", script.get(0));
    assertEquals("echo World", script.get(1));

    JobRequest requestWithoutScript = new JobRequest("job-999", "pipeline-4", "lint-job", "pqr789", Map.of(), List.of());
    assertTrue(requestWithoutScript.getScript().isEmpty());
  }

  @Test
  void testGetImage() {
    assertEquals("ubuntu:latest", jobRequest.getImage());

    JobRequest requestWithoutImage = new JobRequest("job-888", "pipeline-5", "test-job", "abcd999", Map.of(), List.of());
    assertNull(requestWithoutImage.getImage());
  }

  @Test
  void testAddNeeds() {
    jobRequest.addNeeds("test-job");
    assertEquals(1, jobRequest.getNeeds().size());
    assertTrue(jobRequest.getNeeds().contains("test-job"));

    jobRequest.addNeeds("deploy-job");
    assertEquals(2, jobRequest.getNeeds().size());
    assertTrue(jobRequest.getNeeds().contains("deploy-job"));
  }
}
