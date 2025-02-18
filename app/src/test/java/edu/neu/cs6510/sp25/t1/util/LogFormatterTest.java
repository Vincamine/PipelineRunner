package edu.neu.cs6510.sp25.t1.util;

import edu.neu.cs6510.sp25.t1.model.LogEntry;
import edu.neu.cs6510.sp25.t1.model.LogLevel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class LogFormatterTest {

    @Test
    void testFormat() {
        final LogEntry log = new LogEntry("pipeline-1", LogLevel.INFO, "Pipeline started", System.currentTimeMillis());
        final String formattedLog = LogFormatter.format(log);

        assertTrue(formattedLog.contains("[INFO] Pipeline started"));
        assertTrue(formattedLog.contains("["));
        assertTrue(formattedLog.contains("]"));
    }
}
