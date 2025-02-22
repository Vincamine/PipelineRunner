package edu.neu.cs6510.sp25.t1.util;

import edu.neu.cs6510.sp25.t1.model.ReportEntry;
import edu.neu.cs6510.sp25.t1.model.ReportLevel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class LogFormatterTest {

    @Test
    void testFormat() {
        final ReportEntry log = new ReportEntry("pipeline-1", ReportLevel.SUCCESS, "Pipeline started", System.currentTimeMillis());
        final String formattedLog = ReportFormatter.format(log);

        assertTrue(formattedLog.contains("[SUCCESSSUCCESS] Pipeline started"));
        assertTrue(formattedLog.contains("["));
        assertTrue(formattedLog.contains("]"));
    }
}
