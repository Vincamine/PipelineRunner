package edu.neu.cs6510.sp25.t1.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PipelineExecutionOrderGeneratorTest {
    private PipelineExecutionOrderGenerator generator;

    @BeforeEach
    void setUp() {
        generator = new PipelineExecutionOrderGenerator();
    }

    @Test
    void testGenerateExecutionOrder_InvalidFile_ThrowsException() {
        assertThrows(IOException.class, () -> generator.generateExecutionOrder("invalid-file.yaml"));
    }
}
