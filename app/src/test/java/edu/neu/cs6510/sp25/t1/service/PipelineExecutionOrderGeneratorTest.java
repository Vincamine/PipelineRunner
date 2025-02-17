package edu.neu.cs6510.sp25.t1.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class PipelineExecutionOrderGeneratorTest {
    private PipelineExecutionOrderGenerator generator;

    @BeforeEach
    void setUp() {
        generator = new PipelineExecutionOrderGenerator();
    }

    @Test
    void testGenerateExecutionOrder_ValidFile() throws IOException {
        Map<String, Map<String, Object>> executionOrder = generator.generateExecutionOrder("src/test/resources/sample-pipeline.yaml");
        assertNotNull(executionOrder);
        assertFalse(executionOrder.isEmpty());
    }

    @Test
    void testGenerateExecutionOrder_InvalidFile_ThrowsException() {
        assertThrows(IOException.class, () -> generator.generateExecutionOrder("invalid-file.yaml"));
    }
}
