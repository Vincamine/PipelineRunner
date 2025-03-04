package edu.neu.cs6510.sp25.t1.common.api.request;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RunPipelineRequestTest {

  @Test
  void testDefaultConstructor() {
    RunPipelineRequest request = new RunPipelineRequest();
    assertEquals("", request.getRepo());
    assertEquals("", request.getBranch());
    assertEquals("", request.getCommit());
    assertEquals("", request.getPipeline());
    assertFalse(request.isLocal());
    assertNotNull(request.getOverrides());
    assertTrue(request.getOverrides().isEmpty());
    assertEquals("", request.getConfigPath());
  }

  @Test
  void testConstructorWithPipeline() {
    RunPipelineRequest request = new RunPipelineRequest("my-pipeline");
    assertEquals("my-pipeline", request.getPipeline());
    assertEquals("", request.getRepo());
    assertEquals("", request.getBranch());
    assertEquals("", request.getCommit());
    assertFalse(request.isLocal());
    assertTrue(request.getOverrides().isEmpty());
    assertEquals("", request.getConfigPath());
  }

  @Test
  void testConstructorWithPipelineThrowsExceptionOnEmptyValue() {
    Exception exception1 = assertThrows(IllegalArgumentException.class, () -> new RunPipelineRequest(""));
    assertEquals("Pipeline name cannot be null or empty.", exception1.getMessage());

    Exception exception2 = assertThrows(IllegalArgumentException.class, () -> new RunPipelineRequest("   "));
    assertEquals("Pipeline name cannot be null or empty.", exception2.getMessage());

    Exception exception3 = assertThrows(IllegalArgumentException.class, () -> new RunPipelineRequest(null));
    assertEquals("Pipeline name cannot be null or empty.", exception3.getMessage());
  }

  @Test
  void testFullConstructor() {
    RunPipelineRequest request = new RunPipelineRequest(
            "https://github.com/example/repo",
            "main",
            "abcd1234",
            "deploy-pipeline",
            true,
            Map.of("KEY", "VALUE"),
            "/path/to/config"
    );

    assertEquals("https://github.com/example/repo", request.getRepo());
    assertEquals("main", request.getBranch());
    assertEquals("abcd1234", request.getCommit());
    assertEquals("deploy-pipeline", request.getPipeline());
    assertTrue(request.isLocal());
    assertEquals(Map.of("KEY", "VALUE"), request.getOverrides());
    assertEquals("/path/to/config", request.getConfigPath());
  }

  @Test
  void testFullConstructorHandlesNullValues() {
    RunPipelineRequest request = new RunPipelineRequest(
            null, null, null, "build-pipeline", false, null, null
    );

    assertEquals("", request.getRepo());
    assertEquals("", request.getBranch());
    assertEquals("", request.getCommit());
    assertEquals("build-pipeline", request.getPipeline());
    assertFalse(request.isLocal());
    assertNotNull(request.getOverrides());
    assertTrue(request.getOverrides().isEmpty());
    assertEquals("", request.getConfigPath());
  }

  @Test
  void testToStringMethod() {
    RunPipelineRequest request = new RunPipelineRequest(
            "repo-url",
            "dev-branch",
            "commit-hash",
            "test-pipeline",
            false,
            Map.of("configKey", "configValue"),
            "/pipeline.yml"
    );

    String expectedString = "RunPipelineRequest{repo='repo-url', branch='dev-branch', commit='commit-hash', " +
            "pipeline='test-pipeline', local=false, overrides={configKey=configValue}, configPath='/pipeline.yml'}";

    assertEquals(expectedString, request.toString());
  }
}
