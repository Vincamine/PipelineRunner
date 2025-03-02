package edu.neu.cs6510.sp25.t1.common.config;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PipelineConfigTest {

  @Test
  void testConstructorAndGetters() {
    List<StageConfig> stages = List.of(new StageConfig("build", List.of()));
    Map<String, String> globals = Map.of("ENV", "production");

    PipelineConfig pipeline = new PipelineConfig("test-pipeline", stages, globals);

    assertEquals("test-pipeline", pipeline.getName());
    assertEquals(stages, pipeline.getStages());
    assertEquals(globals, pipeline.getGlobals());
  }

  @Test
  void testConstructorWithEmptyStagesAndGlobals() {
    PipelineConfig pipeline = new PipelineConfig("test-pipeline", List.of(), Map.of());
    assertNotNull(pipeline.getStages());
    assertNotNull(pipeline.getGlobals());
    assertTrue(pipeline.getStages().isEmpty());
    assertTrue(pipeline.getGlobals().isEmpty());
  }

  @Test
  void testDefaultConstructor() {
    PipelineConfig pipeline = new PipelineConfig();
    assertNotNull(pipeline);
    assertEquals("", pipeline.getName());
    assertNotNull(pipeline.getStages());
    assertNotNull(pipeline.getGlobals());
    assertTrue(pipeline.getStages().isEmpty());
    assertTrue(pipeline.getGlobals().isEmpty());
  }

  @Test
  void testConstructorHandlesNullValues() {
    PipelineConfig pipeline = new PipelineConfig("test-pipeline", null, null);

    assertNotNull(pipeline.getStages(), "Stages should be initialized as empty list");
    assertNotNull(pipeline.getGlobals(), "Globals should be initialized as empty map");

    assertTrue(pipeline.getStages().isEmpty(), "Stages should be empty when passed null");
    assertTrue(pipeline.getGlobals().isEmpty(), "Globals should be empty when passed null");
  }
}
