package edu.neu.cs6510.sp25.t1.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import java.util.Collections;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for ReportEntry class.
 */
class ReportEntryTest {

    @Test
    void testConstructorAndGetters() {
        final ReportEntry log = new ReportEntry(
                "pipeline-123",
                "SUCCESS",
                "Pipeline started",
                System.currentTimeMillis(),
                "SUCCESS",
                Collections.emptyList(),
                Arrays.asList("Initialization complete", "Resources allocated")
        );

        assertEquals("pipeline-123", log.getPipelineId(), "Pipeline ID should match");
        assertEquals(ReportLevel.SUCCESS, log.getLevel(), "Level should be SUCCESS");
        assertEquals("Pipeline started", log.getMessage(), "Message should match");
        assertTrue(log.getTimestamp() > 0, "Timestamp should be positive");
        assertEquals("SUCCESS", log.getStatus(), "Status should match");
        assertTrue(log.getStages().isEmpty(), "Stages list should be empty");
        assertEquals(2, log.getDetails().size(), "Details should contain 2 items");
    }

    @Test
    void testJsonSerialization() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final ReportEntry log = new ReportEntry(
                "pipeline-123",
                "FAILED",
                "An error occurred",
                1678945600000L,
                "FAILED",
                Arrays.asList("build", "test"),
                Arrays.asList("Build failed", "Tests skipped")
        );

        final String json = mapper.writeValueAsString(log);
        assertTrue(json.contains("\"level\":\"FAILED\""), "JSON should contain level as string");
        assertTrue(json.contains("\"details\":["), "JSON should contain details array");

        final ReportEntry deserializedLog = mapper.readValue(json, ReportEntry.class);
        assertEquals("pipeline-123", deserializedLog.getPipelineId(), "Pipeline ID should persist");
        assertEquals(ReportLevel.FAILED, deserializedLog.getLevel(), "Level should be FAILED");
        assertEquals("An error occurred", deserializedLog.getMessage(), "Message should persist");
        assertEquals(1678945600000L, deserializedLog.getTimestamp(), "Timestamp should persist");
        assertEquals("FAILED", deserializedLog.getStatus(), "Status should persist");
        assertEquals(2, deserializedLog.getStages().size(), "Should have 2 stages");
        assertEquals(2, deserializedLog.getDetails().size(), "Should have 2 details");
    }

    @Test
    void testNullLevel() {
        final ReportEntry log = new ReportEntry(
                "pipeline-123",
                null,
                "Pipeline started",
                System.currentTimeMillis(),
                "SUCCESS",
                Collections.emptyList(),
                Collections.emptyList()
        );

        assertEquals(ReportLevel.SUCCESS, log.getLevel(), "Null level should default to SUCCESS");
    }

    @Test
    void testInvalidLevelString() {
        final ReportEntry log = new ReportEntry(
                "pipeline-123",
                "INVALID_LEVEL",
                "Pipeline started",
                System.currentTimeMillis(),
                "SUCCESS",
                Collections.emptyList(),
                Collections.emptyList()
        );

        assertEquals(ReportLevel.SUCCESS, log.getLevel(), "Invalid level should default to SUCCESS");
    }

//    @Test
//    void testNullCollections() {
//        final ReportEntry log = new ReportEntry(
//                "pipeline-123",
//                "SUCCESS",
//                "Pipeline started",
//                System.currentTimeMillis(),
//                "SUCCESS",
//                null,
//                null
//        );
//
////        assertNotNull(log.getStages(), "Stages should not be null");
//        assertTrue(log.getStages().isEmpty(), "Stages should be empty");
//        assertNotNull(log.getDetails(), "Details should not be null");
//        assertTrue(log.getDetails().isEmpty(), "Details should be empty");
//    }

    @Test
    void testJsonDeserialization() throws Exception {
        final String json = "{\"pipelineId\":\"test-pipeline\"," +
                "\"level\":\"SUCCESS\"," +
                "\"message\":\"Test message\"," +
                "\"timestamp\":1678945600000," +
                "\"status\":\"SUCCESS\"," +
                "\"stages\":[\"build\",\"test\"]," +
                "\"details\":[\"Detail 1\",\"Detail 2\"]}";

        final ObjectMapper mapper = new ObjectMapper();
        final ReportEntry log = mapper.readValue(json, ReportEntry.class);

        assertEquals("test-pipeline", log.getPipelineId());
        assertEquals(ReportLevel.SUCCESS, log.getLevel());
        assertEquals("Test message", log.getMessage());
        assertEquals(1678945600000L, log.getTimestamp());
        assertEquals("SUCCESS", log.getStatus());
        assertEquals(2, log.getStages().size());
        assertEquals(2, log.getDetails().size());
        assertEquals("Detail 1", log.getDetails().get(0));
    }
}