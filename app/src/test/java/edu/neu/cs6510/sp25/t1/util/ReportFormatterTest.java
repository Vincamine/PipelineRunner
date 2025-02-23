package edu.neu.cs6510.sp25.t1.util;

import edu.neu.cs6510.sp25.t1.model.ReportEntry;
import org.junit.jupiter.api.Test;

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
                "pipeline-123",      // pipelineId
                "SUCCESS",           // level as string
                "Pipeline completed", // message
                System.currentTimeMillis(), // timestamp
                "SUCCESS",           // status
                Collections.emptyList(), // stages
                Collections.emptyList()  // details
        );

        String formatted = ReportFormatter.format(report);
        assertTrue(formatted.contains("SUCCESS"), "Should contain status");
        assertTrue(formatted.contains("Pipeline completed"), "Should contain message");
        assertFalse(formatted.contains("Stages:"), "Should not contain stages section");
        assertFalse(formatted.contains("Details:"), "Should not contain details section");
    }

    @Test
    void testFormat_ReportWithStagesAndDetails() {
        ReportEntry report = new ReportEntry(
                "pipeline-123",      // pipelineId
                "FAILED",            // level as string
                "Pipeline failed",   // message
                1678901200000L,      // timestamp
                "FAILED",            // status
                List.of("Build stage failed", "Test stage skipped"), // stages
                List.of("Error in compilation", "Tests not executed") // details
        );

        String formatted = ReportFormatter.format(report);
        assertTrue(formatted.contains("FAILED"), "Should contain failure status");
        assertTrue(formatted.contains("Pipeline failed"), "Should contain failure message");
        assertTrue(formatted.contains("Stages:"), "Should contain stages section");
        assertTrue(formatted.contains("Build stage failed"), "Should contain build stage info");
        assertTrue(formatted.contains("Test stage skipped"), "Should contain test stage info");
        assertTrue(formatted.contains("Details:"), "Should contain details section");
        assertTrue(formatted.contains("Error in compilation"), "Should contain error details");
        assertTrue(formatted.contains("Tests not executed"), "Should contain test skip details");
    }

    @Test
    void testFormat_NullReport() {
        String formatted = ReportFormatter.format(null);
        assertEquals("No report available", formatted, "Should handle null report");
    }

    @Test
    void testFormat_ReportWithNullFields() {
        ReportEntry report = new ReportEntry(
                "pipeline-123",      // pipelineId
                null,               // level
                "Pipeline status",   // message
                System.currentTimeMillis(), // timestamp
                "UNKNOWN",          // status
                null,              // stages
                null               // details
        );

        String formatted = ReportFormatter.format(report);
        assertTrue(formatted.contains("SUCCESS"), "Should use default level for null");
        assertTrue(formatted.contains("Pipeline status"), "Should contain message");
        assertFalse(formatted.contains("Stages:"), "Should not contain null stages");
        assertFalse(formatted.contains("Details:"), "Should not contain null details");
    }

//    @Test
//    void testFormat_ReportWithTimestamp() {
//        long timestamp = 1678901200000L; // 2023-03-15 12:00:00
//        ReportEntry report = new ReportEntry(
//                "pipeline-123",
//                "SUCCESS",
//                "Pipeline completed",
//                timestamp,
//                "SUCCESS",
//                Collections.emptyList(),
//                Collections.emptyList()
//        );
//
//        String formatted = ReportFormatter.format(report);
//        assertTrue(formatted.contains("2023-03-15"), "Should contain formatted date");
//        assertTrue(formatted.contains("12:00:00"), "Should contain formatted time");
//    }
}