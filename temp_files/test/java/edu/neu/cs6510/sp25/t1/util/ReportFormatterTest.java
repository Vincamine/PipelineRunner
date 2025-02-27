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
        assertNotNull(formatted, "Formatted report should not be null.");
        assertTrue(formatted.contains("SUCCESS"), "Formatted report should contain 'SUCCESS'.");
        assertTrue(formatted.contains("Pipeline completed"), "Formatted report should contain the message.");
    }

    @Test
    void testFormat_ReportWithStagesAndDetails() {
        ReportEntry report = new ReportEntry(
                "pipeline-123",
                "FAILED",
                "Pipeline failed",
                1678901200000L,
                "FAILED",
                List.of(
                        new StageInfo("Build", "FAILED", 1678901200000L, 1678901400000L, List.of("Compile", "Package")),
                        new StageInfo("Test", "SUCCESS", 1678901500000L, 1678901700000L, List.of("Unit Test", "Integration Test"))
                ),
                List.of("Error in compilation", "Tests not executed"),
                5,
                "commit456",
                1678901000000L,
                1678901500000L);

        String formatted = ReportFormatter.format(report);
        assertNotNull(formatted, "Formatted report should not be null.");
        assertTrue(formatted.contains("FAILED"), "Formatted report should contain 'FAILED'.");
        assertTrue(formatted.contains("Pipeline failed"), "Formatted report should contain the failure message.");
        assertTrue(formatted.contains("Stages:"), "Formatted report should include stage information.");
        assertTrue(formatted.contains("Build"), "Formatted report should mention the stage name.");
        assertTrue(formatted.contains("Error in compilation"), "Formatted report should include error details.");
        assertTrue(formatted.contains("Jobs: Compile, Package"), "Formatted report should list jobs in stages.");
        assertTrue(formatted.contains("Jobs: Unit Test, Integration Test"), "Formatted report should list jobs for the Test stage.");
    }

    @Test
    void testFormat_NullReport() {
        String formatted = ReportFormatter.format(null);
        assertEquals("No report available", formatted, "Should handle null report gracefully.");
    }

    @Test
    void testFormat_ReportWithNullFields() {
        ReportEntry report = new ReportEntry(
                "pipeline-123", // pipelineId
                null, // level (should default to SUCCESS)
                "Pipeline status", // message
                System.currentTimeMillis(), // timestamp
                "UNKNOWN", // status
                null, // stages (should be handled gracefully)
                null, // details (should be handled gracefully)
                3,
                "commit789",
                System.currentTimeMillis() - 7000,
                System.currentTimeMillis());

        String formatted = ReportFormatter.format(report);
        assertNotNull(formatted, "Formatted report should not be null.");
        assertTrue(formatted.contains("SUCCESS") || formatted.contains("UNKNOWN"), "Should use a default level for null.");
        assertTrue(formatted.contains("Pipeline status"), "Formatted report should contain the message.");
        assertFalse(formatted.contains("Stages:"), "Formatted report should not contain null stages.");
        assertFalse(formatted.contains("Details:"), "Formatted report should not contain null details.");
    }
}
