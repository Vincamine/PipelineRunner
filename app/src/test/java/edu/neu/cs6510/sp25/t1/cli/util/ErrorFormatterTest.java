package edu.neu.cs6510.sp25.t1.cli.util;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link ErrorFormatter}.
 */
class ErrorFormatterTest {

    @Test
    void testFormatErrorMessage() {
        final String result = ErrorFormatter.format("testfile.java", 15, 3, "Syntax error");
        assertEquals("testfile.java:15:3: Syntax error", result);
    }
}
