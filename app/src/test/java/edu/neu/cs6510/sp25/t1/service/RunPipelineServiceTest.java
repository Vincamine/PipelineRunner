package edu.neu.cs6510.sp25.t1.service;

import edu.neu.cs6510.sp25.t1.model.PipelineState;
import edu.neu.cs6510.sp25.t1.model.PipelineStatus;
import edu.neu.cs6510.sp25.t1.validation.YamlPipelineValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RunPipelineServiceTest {
    private RunPipelineService service;
    private YamlPipelineValidator mockValidator;
    private Map<String, Object> mockPipelineConfig;

    @BeforeEach
    void setUp() {
        mockValidator = mock(YamlPipelineValidator.class);
        service = new RunPipelineService(mockValidator);

        mockPipelineConfig = new HashMap<>();
        mockPipelineConfig.put("pipeline", Map.of(
                "name", "test-pipeline",
                "stages", List.of("build", "test", "deploy")
        ));

        mockPipelineConfig.put("jobs", List.of(
                Map.of("name", "compile", "stage", "build"),
                Map.of("name", "unit-test", "stage", "test", "needs", List.of("compile")),
                Map.of("name", "deploy", "stage", "deploy", "needs", List.of("unit-test"))
        ));
    }

    @Test
    void testPipelineExecutesInOrder() {
        when(mockValidator.validatePipeline(mockPipelineConfig)).thenReturn(true);
        PipelineStatus status = service.executePipeline(mockPipelineConfig);

        assertEquals(PipelineState.SUCCEEDED, status.getState());
        assertEquals("All stages completed", status.getMessage());
    }
}
