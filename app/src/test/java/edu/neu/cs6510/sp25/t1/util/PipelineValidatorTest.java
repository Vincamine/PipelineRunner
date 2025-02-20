package edu.neu.cs6510.sp25.t1.util;

import edu.neu.cs6510.sp25.t1.validation.YamlPipelineValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
        final Path projectRoot = Paths.get(System.getProperty("user.dir")).getParent();
        final Path mockPath = projectRoot.resolve(".pipelines/pipeline.yaml").toAbsolutePath();

        when(yamlPipelineValidator.validatePipeline(mockPath.toString())).thenReturn(true);

        assertTrue(pipelineValidator.validatePipelineFile(mockPath.toString()));
    }

    @Test
    void testValidatePipelineFile_NotFound() {
        assertFalse(pipelineValidator.validatePipelineFile("nonexistent.yaml"));
    }

    @Test
    void testValidatePipelineFile_WrongDirectory() {
        final Path wrongPath = Paths.get("wrong-directory/pipeline.yaml").toAbsolutePath();
        assertFalse(pipelineValidator.validatePipelineFile(wrongPath.toString()));
    }
}
