package edu.neu.cs6510.sp25.t1.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LogEntryTest {

    @Test
    void testConstructorAndGetters() {
        final LogEntry log = new LogEntry("pipeline-123", LogLevel.INFO, "Pipeline started", System.currentTimeMillis());

        assertEquals("pipeline-123", log.getPipelineId());
        assertEquals(LogLevel.INFO, log.getLevel());
        assertEquals("Pipeline started", log.getMessage());
        assertTrue(log.getTimestamp() > 0);
    }

    @Test
    void testJsonSerialization() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final LogEntry log = new LogEntry("pipeline-123", LogLevel.ERROR, "An error occurred", 1678945600000L);
        final String json = mapper.writeValueAsString(log);

        final LogEntry deserializedLog = mapper.readValue(json, LogEntry.class);
        assertEquals("pipeline-123", deserializedLog.getPipelineId());
        assertEquals(LogLevel.ERROR, deserializedLog.getLevel());
        assertEquals("An error occurred", deserializedLog.getMessage());
        assertEquals(1678945600000L, deserializedLog.getTimestamp());
    }
}
