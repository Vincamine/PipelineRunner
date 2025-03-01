package edu.neu.cs6510.sp25.t1.common.api;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
  void testRunPipelineRequestDefaults() {
    RunPipelineRequest request = new RunPipelineRequest();

    assertNotNull(request.getOverrides());
    assertTrue(request.getOverrides().isEmpty());
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
