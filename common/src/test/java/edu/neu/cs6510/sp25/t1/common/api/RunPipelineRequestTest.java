package edu.neu.cs6510.sp25.t1.common.api;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


class RunPipelineRequestTest {

  @Test
  void testRunPipelineRequestConstructorAndGetters() {
    RunPipelineRequest request = new RunPipelineRequest(
            "repoURL", "main", "commit123", "build", true, Map.of(), ""
    );

    assertEquals("repoURL", request.getRepo());
    assertEquals("main", request.getBranch());
    assertEquals("commit123", request.getCommit());
    assertEquals("build", request.getPipeline());
    assertTrue(request.isLocal());
    assertEquals(Map.of(), request.getOverrides()); // Ensure empty map is set correctly
    assertEquals("", request.getConfigPath()); // Ensure default configPath is ""
  }

  @Test
  void testRunPipelineRequest_DefaultConstructor() {
    RunPipelineRequest request = new RunPipelineRequest();

    assertNotNull(request.getRepo());
    assertNotNull(request.getBranch());
    assertNotNull(request.getCommit());
    assertNotNull(request.getPipeline());
    assertNotNull(request.getOverrides());
    assertNotNull(request.getConfigPath());

    assertEquals("", request.getRepo());
    assertEquals("", request.getBranch());
    assertEquals("", request.getCommit());
    assertEquals("", request.getPipeline());
    assertEquals("", request.getConfigPath());
    assertTrue(request.getOverrides().isEmpty());
  }

  @Test
  void testRunPipelineRequest_SinglePipelineConstructor() {
    RunPipelineRequest request = new RunPipelineRequest("testPipeline");

    assertEquals("testPipeline", request.getPipeline());
    assertEquals("", request.getRepo());
    assertEquals("", request.getBranch());
    assertEquals("", request.getCommit());
    assertEquals("", request.getConfigPath());
    assertFalse(request.isLocal());
    assertTrue(request.getOverrides().isEmpty());
  }

  @Test
  void testRunPipelineRequest_NullValues() {
    RunPipelineRequest request = new RunPipelineRequest(null, null, null, "pipeline", false, null, null);

    assertEquals("", request.getRepo());
    assertEquals("", request.getBranch());
    assertEquals("", request.getCommit());
    assertEquals("pipeline", request.getPipeline());
    assertEquals("", request.getConfigPath());
    assertFalse(request.isLocal());
    assertTrue(request.getOverrides().isEmpty());
  }

  @Test
  void testRunPipelineRequest_ThrowsExceptionForEmptyPipeline() {
    Exception exception = assertThrows(IllegalArgumentException.class, () -> new RunPipelineRequest(""));
    assertEquals("Pipeline name cannot be null or empty.", exception.getMessage());

    Exception nullPipelineException = assertThrows(IllegalArgumentException.class, () -> new RunPipelineRequest(null));
    assertEquals("Pipeline name cannot be null or empty.", nullPipelineException.getMessage());
  }

  @Test
  void testToString() {
    RunPipelineRequest request = new RunPipelineRequest(
            "repoURL", "main", "commit123", "build", true, Map.of(), ""
    );

    String result = request.toString();
    assertTrue(result.contains("repoURL"));
    assertTrue(result.contains("main"));
    assertTrue(result.contains("commit123"));
    assertTrue(result.contains("build"));
  }
}
