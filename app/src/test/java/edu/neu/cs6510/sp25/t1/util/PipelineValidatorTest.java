package edu.neu.cs6510.sp25.t1.util;

import edu.neu.cs6510.sp25.t1.validation.YamlPipelineValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PipelineValidatorTest {
    private PipelineValidator pipelineValidator;
    private YamlPipelineValidator yamlPipelineValidator;

    @BeforeEach
    void setUp() {
        yamlPipelineValidator = mock(YamlPipelineValidator.class);
        pipelineValidator = new PipelineValidator(yamlPipelineValidator);
    }

    @Test
    void testValidatePipelineFile_Success() {
        Path mockPath = Paths.get(".pipelines/pipeline.yaml").toAbsolutePath();
        when(yamlPipelineValidator.validatePipeline(mockPath.toString())).thenReturn(true);

        assertTrue(pipelineValidator.validatePipelineFile(mockPath.toString()));
    }

    @Test
    void testValidatePipelineFile_NotFound() {
        assertFalse(pipelineValidator.validatePipelineFile("nonexistent.yaml"));
    }

    @Test
    void testValidatePipelineFile_WrongDirectory() {
        Path wrongPath = Paths.get("wrong-directory/pipeline.yaml").toAbsolutePath();
        assertFalse(pipelineValidator.validatePipelineFile(wrongPath.toString()));
    }
}
