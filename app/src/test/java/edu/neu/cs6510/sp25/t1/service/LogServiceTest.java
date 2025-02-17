package edu.neu.cs6510.sp25.t1.service;

import edu.neu.cs6510.sp25.t1.model.LogEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.net.http.HttpClient;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class LogServiceTest {
    private LogService logService;

    @BeforeEach
    void setUp() {
        HttpClient mockHttpClient = Mockito.mock(HttpClient.class);
        logService = new LogService(mockHttpClient);
    }

    @Test
    void testGetLogsByPipelineId_ReturnsMockData() {
        List<LogEntry> logs = logService.getLogsByPipelineId("pipeline-123");
        assertNotNull(logs);
        assertFalse(logs.isEmpty());
        assertEquals(4, logs.size());  // Mock data contains 4 logs
    }

    @Test
    void testGetLogsByPipelineId_ThrowsErrorOnNullId() {
        assertThrows(IllegalArgumentException.class, () -> logService.getLogsByPipelineId(null));
    }
}
