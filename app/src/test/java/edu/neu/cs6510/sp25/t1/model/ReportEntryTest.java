package edu.neu.cs6510.sp25.t1.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import java.util.Collections;
import java.util.List;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;


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
                Instant.now().toEpochMilli(),
                "SUCCESS",
                List.of(new StageInfo("Build", "SUCCESS", Instant.now().toEpochMilli(), Instant.now().toEpochMilli())),
                List.of("Initialization complete", "Resources allocated"),
                2,
                "def456",
                Instant.now().minusSeconds(900).toEpochMilli(),
                Instant.now().toEpochMilli());

        assertEquals("pipeline-123", log.getPipelineId());
        assertEquals(ReportLevel.SUCCESS, log.getLevel());
        assertEquals("Pipeline started", log.getMessage());
        assertTrue(log.getTimestamp() > 0);
        assertEquals("SUCCESS", log.getStatus());
        assertEquals(1, log.getStages().size());
        assertEquals(2, log.getDetails().size());
        assertEquals(2, log.getRunNumber());
        assertEquals("def456", log.getGitCommitHash());
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
                List.of(new StageInfo("Test", "FAILED", 1678945600000L, 1678945800000L)),
                List.of("Build failed", "Tests skipped"),
                3,
                "xyz789",
                1678945000000L,
                1678946000000L);

        final String json = mapper.writeValueAsString(log);
        assertTrue(json.contains("\"level\":\"FAILED\""));
        assertTrue(json.contains("\"details\":"));

        final ReportEntry deserializedLog = mapper.readValue(json, ReportEntry.class);
        assertEquals("pipeline-123", deserializedLog.getPipelineId());
        assertEquals(ReportLevel.FAILED, deserializedLog.getLevel());
        assertEquals("An error occurred", deserializedLog.getMessage());
        assertEquals(1678945600000L, deserializedLog.getTimestamp());
        assertEquals("FAILED", deserializedLog.getStatus());
        assertEquals(1, deserializedLog.getStages().size());
        assertEquals(2, deserializedLog.getDetails().size());
        assertEquals(3, deserializedLog.getRunNumber());
        assertEquals("xyz789", deserializedLog.getGitCommitHash());
        assertEquals(1678945000000L, deserializedLog.getStartTime());
        assertEquals(1678946000000L, deserializedLog.getCompletionTime());
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
                Collections.emptyList(),
                3,
                "commitDEF",
                System.currentTimeMillis() - 7000,
                System.currentTimeMillis());

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
                Collections.emptyList(),
                4,
                "commitGHI",
                System.currentTimeMillis() - 9000,
                System.currentTimeMillis());

        assertEquals(ReportLevel.SUCCESS, log.getLevel(), "Invalid level should default to SUCCESS");
    }

    @Test
    void testFormat_ReportWithNullFields() {
        final ReportEntry report = new ReportEntry(
                "pipeline-123",
                null, // level
                "Pipeline status", // message
                System.currentTimeMillis(), // timestamp
                "UNKNOWN", // status
                null, // stages
                null, // details
                5,
                "commitJKL",
                System.currentTimeMillis() - 11000,
                System.currentTimeMillis());

        assertEquals(ReportLevel.SUCCESS, report.getLevel(), "Null level should default to SUCCESS");
        assertEquals("Pipeline status", report.getMessage(), "Message should match");
        assertNull(report.getStages(), "Stages should be null");
        assertNull(report.getDetails(), "Details should be null");
    }


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