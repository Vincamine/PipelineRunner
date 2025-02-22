package edu.neu.cs6510.sp25.t1.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

class StageInfoTest {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  void testStageInfoSerialization() throws IOException {
    StageInfo stage = new StageInfo("Build", "SUCCESS", 1708550400000L, 1708554000000L);

    String json = objectMapper.writeValueAsString(stage);
    StageInfo deserializedStage = objectMapper.readValue(json, StageInfo.class);

    assertEquals(stage.getStageName(), deserializedStage.getStageName());
    assertEquals(stage.getStageStatus(), deserializedStage.getStageStatus());
    assertEquals(stage.getStartTime(), deserializedStage.getStartTime());
    assertEquals(stage.getCompletionTime(), deserializedStage.getCompletionTime());
  }

  @Test
  void testStageInfoGetters() {
    StageInfo stage = new StageInfo("Test", "FAILED", 1708550400000L, 1708554000000L);

    assertEquals("Test", stage.getStageName());
    assertEquals("FAILED", stage.getStageStatus());
    assertEquals(1708550400000L, stage.getStartTime());
    assertEquals(1708554000000L, stage.getCompletionTime());
  }
}
