package edu.neu.cs6510.sp25.t1.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.List;

class StageInfoTest {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  void testStageInfoSerialization() throws IOException {
    StageInfo stage = new StageInfo("Build", "SUCCESS", 1708550400000L, 1708554000000L, List.of("compile", "package"));

    // Serialize to JSON
    String json = objectMapper.writeValueAsString(stage);

    // Deserialize from JSON
    StageInfo deserializedStage = objectMapper.readValue(json, StageInfo.class);

    // Validate fields
    assertEquals(stage.getStageName(), deserializedStage.getStageName());
    assertEquals(stage.getStageStatus(), deserializedStage.getStageStatus());
    assertEquals(stage.getStartTime(), deserializedStage.getStartTime());
    assertEquals(stage.getCompletionTime(), deserializedStage.getCompletionTime());
    assertEquals(stage.getJobs(), deserializedStage.getJobs()); // ✅ Validate jobs field
  }

  @Test
  void testStageInfoGetters() {
    List<String> jobs = List.of("unit-test", "integration-test");
    StageInfo stage = new StageInfo("Test", "FAILED", 1708550400000L, 1708554000000L, jobs);

    assertEquals("Test", stage.getStageName());
    assertEquals("FAILED", stage.getStageStatus());
    assertEquals(1708550400000L, stage.getStartTime());
    assertEquals(1708554000000L, stage.getCompletionTime());
    assertEquals(jobs, stage.getJobs()); // ✅ Validate jobs field
  }

  @Test
  void testEmptyJobsList() {
    StageInfo stage = new StageInfo("Deploy", "SUCCESS", 1708550400000L, 1708554000000L, List.of());

    assertNotNull(stage.getJobs()); // Ensure jobs list is not null
    assertTrue(stage.getJobs().isEmpty()); // Ensure jobs list is empty
  }
}
