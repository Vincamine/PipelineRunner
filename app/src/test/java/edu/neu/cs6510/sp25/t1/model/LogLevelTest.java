package edu.neu.cs6510.sp25.t1.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LogLevelTest {

    @Test
    void testFromString() {
        assertEquals(LogLevel.INFO, LogLevel.fromString("INFO"));
        assertEquals(LogLevel.WARN, LogLevel.fromString("warn"));
        assertNull(LogLevel.fromString(null));
    }

    @Test
    void testToValue() {
        assertEquals("DEBUG", LogLevel.DEBUG.toValue());
        assertEquals("ERROR", LogLevel.ERROR.toValue());
    }
}
