//package edu.neu.cs6510.sp25.t1.common.config;
//
//import org.junit.jupiter.api.Test;
//
//import java.util.List;
//import java.util.Map;
//
//import edu.neu.cs6510.sp25.t1.common.model.Pipeline;
//import edu.neu.cs6510.sp25.t1.common.model.Stage;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//class PipelineTest {
//
//  @Test
//  void testConstructorAndGetters() {
//    List<Stage> stages = List.of(new Stage("build", List.of()));
//    Map<String, String> globals = Map.of("ENV", "production");
//
//    Pipeline pipeline = new Pipeline("test-pipeline", stages, globals);
//
//    assertEquals("test-pipeline", pipeline.getName());
//    assertEquals(stages, pipeline.getStages());
//    assertEquals(globals, pipeline.getGlobals());
//  }
//
//  @Test
//  void testConstructorWithEmptyStagesAndGlobals() {
//    Pipeline pipeline = new Pipeline("test-pipeline", List.of(), Map.of());
//    assertNotNull(pipeline.getStages());
//    assertNotNull(pipeline.getGlobals());
//    assertTrue(pipeline.getStages().isEmpty());
//    assertTrue(pipeline.getGlobals().isEmpty());
//  }
//
//  @Test
//  void testDefaultConstructor() {
//    Pipeline pipeline = new Pipeline();
//    assertNotNull(pipeline);
//    assertEquals("", pipeline.getName());
//    assertNotNull(pipeline.getStages());
//    assertNotNull(pipeline.getGlobals());
//    assertTrue(pipeline.getStages().isEmpty());
//    assertTrue(pipeline.getGlobals().isEmpty());
//  }
//
//  @Test
//  void testConstructorHandlesNullValues() {
//    Pipeline pipeline = new Pipeline("test-pipeline", null, null);
//
//    assertNotNull(pipeline.getStages(), "Stages should be initialized as empty list");
//    assertNotNull(pipeline.getGlobals(), "Globals should be initialized as empty map");
//
//    assertTrue(pipeline.getStages().isEmpty(), "Stages should be empty when passed null");
//    assertTrue(pipeline.getGlobals().isEmpty(), "Globals should be empty when passed null");
//  }
//}
