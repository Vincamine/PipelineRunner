package edu.neu.cs6510.sp25.t1.common.model.definition;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PipelineDefinitionTest {

  @Test
  void testConstructorAndGetters() {
    List<StageDefinition> stages = List.of(new StageDefinition("build", List.of()));
    Map<String, String> globals = Map.of("ENV", "production");

    PipelineDefinition pipeline = new PipelineDefinition("test-pipeline", stages, globals);

    assertEquals("test-pipeline", pipeline.getName());
    assertEquals(stages, pipeline.getStages());
    assertEquals(globals, pipeline.getGlobals());
  }

  @Test
  void testConstructorWithEmptyStagesAndGlobals() {
    PipelineDefinition pipeline = new PipelineDefinition("test-pipeline", List.of(), Map.of());
    assertNotNull(pipeline.getStages());
    assertNotNull(pipeline.getGlobals());
    assertTrue(pipeline.getStages().isEmpty());
    assertTrue(pipeline.getGlobals().isEmpty());
  }
}
