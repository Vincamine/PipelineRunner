package edu.neu.cs6510.sp25.t1.util;

import edu.neu.cs6510.sp25.t1.model.ReportEntry;
import edu.neu.cs6510.sp25.t1.model.StageInfo;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for ReportFormatter class.
 */
class ReportFormatterTest {

    @Test
    void testFormat_ReportWithoutStages() {
        ReportEntry report = new ReportEntry(
                "pipeline-123",
                "SUCCESS",
                "Pipeline completed",
                Instant.now().toEpochMilli(),
                "SUCCESS",
                Collections.emptyList(),
                Collections.emptyList(),
                4,
                "commit123",
                Instant.now().minusSeconds(1200).toEpochMilli(),
                Instant.now().toEpochMilli());

        String formatted = ReportFormatter.format(report);
        assertTrue(formatted.contains("SUCCESS"));
        assertTrue(formatted.contains("Pipeline completed"));
    }

    @Test
    void testFormat_ReportWithStagesAndDetails() {
        ReportEntry report = new ReportEntry(
                "pipeline-123",
                "FAILED",
                "Pipeline failed",
                1678901200000L,
                "FAILED",
                List.of(new StageInfo("Build", "FAILED", 1678901200000L, 1678901400000L)),
                List.of("Error in compilation", "Tests not executed"),
                5,
                "commit456",
                1678901000000L,
                1678901500000L);

        String formatted = ReportFormatter.format(report);
        assertTrue(formatted.contains("FAILED"));
        assertTrue(formatted.contains("Pipeline failed"));
        assertTrue(formatted.contains("Stages:"));
        assertTrue(formatted.contains("Build"));
        assertTrue(formatted.contains("Error in compilation"));
    }

    @Test
    void testFormat_NullReport() {
        String formatted = ReportFormatter.format(null);
        assertEquals("No report available", formatted, "Should handle null report");
    }

    @Test
    void testFormat_ReportWithNullFields() {
        ReportEntry report = new ReportEntry(
                "pipeline-123", // pipelineId
                null, // level
                "Pipeline status", // message
                System.currentTimeMillis(), // timestamp
                "UNKNOWN", // status
                null, // stages
                null, // details
                3,
                "commit789",
                System.currentTimeMillis() - 7000,
                System.currentTimeMillis());

        String formatted = ReportFormatter.format(report);
        assertTrue(formatted.contains("SUCCESS"), "Should use default level for null");
        assertTrue(formatted.contains("Pipeline status"), "Should contain message");
        assertFalse(formatted.contains("Stages:"), "Should not contain null stages");
        assertFalse(formatted.contains("Details:"), "Should not contain null details");
    }
}