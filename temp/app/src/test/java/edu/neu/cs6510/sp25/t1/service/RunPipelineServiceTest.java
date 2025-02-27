package edu.neu.cs6510.sp25.t1.service;

import edu.neu.cs6510.sp25.t1.model.PipelineState;
import edu.neu.cs6510.sp25.t1.model.PipelineStatus;
import edu.neu.cs6510.sp25.t1.validation.YamlPipelineValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RunPipelineServiceTest {
    private RunPipelineService pipelineService;
    private YamlPipelineValidator mockValidator;

    @BeforeEach
    void setUp() {
        mockValidator = mock(YamlPipelineValidator.class);
        pipelineService = new RunPipelineService(mockValidator);
    }

    /** Ensures execution fails if validation fails */
    @Test
    void testExecutePipeline_validationFails() {
        Map<String, Object> invalidPipelineConfig = Map.of();
        when(mockValidator.validatePipeline(invalidPipelineConfig)).thenReturn(false);

        PipelineStatus status = pipelineService.executePipeline(invalidPipelineConfig);

        assertEquals(PipelineState.FAILED, status.getState());
        assertEquals("Validation error.", status.getMessage());
    }

    /** Ensures extraction of stages */
    @Test
    void testExtractStages() {
        Map<String, Object> config = Map.of("stages", List.of("build", "test", "deploy"));
        List<String> stages = pipelineService.extractStages(config);

        assertEquals(List.of("build", "test", "deploy"), stages);
    }

    /** Ensures extraction of jobs */
    @Test
    void testExtractJobs() {
        Map<String, Object> config = Map.of(
                "jobs", Map.of(
                        "build", List.of("compile"),
                        "test", List.of("unit-test")
                )
        );

        Map<String, List<String>> jobs = pipelineService.extractJobs(config);

        assertEquals(List.of("compile"), jobs.get("build"));
        assertEquals(List.of("unit-test"), jobs.get("test"));
    }

    /** Ensures extraction of dependencies */
    @Test
    void testExtractDependencies() {
        Map<String, Object> config = Map.of(
                "dependencies", Map.of(
                        "unit-test", List.of("compile"),
                        "deploy-app", List.of("unit-test")
                )
        );

        Map<String, List<String>> dependencies = pipelineService.extractDependencies(config);

        assertEquals(List.of("compile"), dependencies.get("unit-test"));
        assertEquals(List.of("unit-test"), dependencies.get("deploy-app"));
    }
}
