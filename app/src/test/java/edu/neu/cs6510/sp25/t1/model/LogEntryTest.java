package edu.neu.cs6510.sp25.t1.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LogEntryTest {

    @Test
    void testConstructorAndGetters() {
        final ReportEntry log = new ReportEntry("pipeline-123", ReportLevel.SUCCESS, "Pipeline started",
                System.currentTimeMillis());

        assertEquals("pipeline-123", log.getPipelineId());
        assertEquals(ReportLevel.SUCCESS, log.getLevel());
        assertEquals("Pipeline started", log.getMessage());
        assertTrue(log.getTimestamp() > 0);
    }

    @Test
    void testJsonSerialization() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final ReportEntry log = new ReportEntry("pipeline-123", ReportLevel.FAILED, "An error occurred", 1678945600000L);
        final String json = mapper.writeValueAsString(log);

        final ReportEntry deserializedLog = mapper.readValue(json, ReportEntry.class);
        assertEquals("pipeline-123", deserializedLog.getPipelineId());
        assertEquals(ReportLevel.FAILED, deserializedLog.getLevel());
        assertEquals("An error occurred", deserializedLog.getMessage());
        assertEquals(1678945600000L, deserializedLog.getTimestamp());
    }
}
