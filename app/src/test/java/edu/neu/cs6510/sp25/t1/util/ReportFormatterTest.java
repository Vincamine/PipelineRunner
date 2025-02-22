package edu.neu.cs6510.sp25.t1.util;

import edu.neu.cs6510.sp25.t1.model.ReportEntry;
import edu.neu.cs6510.sp25.t1.model.ReportLevel;
import edu.neu.cs6510.sp25.t1.model.StageInfo;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReportFormatterTest {

    @Test
    void testFormat_ReportWithoutStages() {
        ReportEntry report = new ReportEntry("pipeline-123", ReportLevel.SUCCESS, "Pipeline completed",
            System.currentTimeMillis(), "SUCCESS", Collections.emptyList());

        String formatted = ReportFormatter.format(report);
        assertTrue(formatted.contains("SUCCESS"));
        assertTrue(formatted.contains("Pipeline completed"));
        assertFalse(formatted.contains("Stages:"));
    }

    @Test
    void testFormat_ReportWithStages() {
        StageInfo stage1 = new StageInfo("Build", "SUCCESS", 1678900000000L, 1678900500000L);
        StageInfo stage2 = new StageInfo("Test", "FAILED", 1678900600000L, 1678901100000L);

        ReportEntry report = new ReportEntry("pipeline-123", ReportLevel.FAILED, "Pipeline failed",
            1678901200000L, "FAILED", List.of(stage1, stage2));

        String formatted = ReportFormatter.format(report);
        assertTrue(formatted.contains("FAILED"));
        assertTrue(formatted.contains("Pipeline failed"));
        assertTrue(formatted.contains("Stages:"));
        assertTrue(formatted.contains("Build"));
        assertTrue(formatted.contains("Test"));
        assertTrue(formatted.contains("SUCCESS"));
        assertTrue(formatted.contains("FAILED"));
    }
}