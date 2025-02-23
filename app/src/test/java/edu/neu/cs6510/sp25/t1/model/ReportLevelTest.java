package edu.neu.cs6510.sp25.t1.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ReportLevelTest {

    @Test
    void testFromString() {
        assertEquals(ReportLevel.SUCCESS, ReportLevel.fromString("SUCCESS"));
        assertEquals(ReportLevel.WARN, ReportLevel.fromString("warn"));
//        assertNull(ReportLevel.fromString(null));
    }

    @Test
    void testToValue() {
        assertEquals("DEBUG", ReportLevel.DEBUG.toValue());
        assertEquals("FAILED", ReportLevel.FAILED.toValue());
    }
}
