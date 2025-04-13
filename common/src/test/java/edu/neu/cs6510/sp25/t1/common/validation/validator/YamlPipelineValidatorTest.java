package edu.neu.cs6510.sp25.t1.common.validation.validator;

import edu.neu.cs6510.sp25.t1.common.model.Pipeline;
import edu.neu.cs6510.sp25.t1.common.validation.error.ValidationException;
import edu.neu.cs6510.sp25.t1.common.validation.manager.PipelineNameManager;
import edu.neu.cs6510.sp25.t1.common.validation.parser.YamlParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class YamlPipelineValidatorTest {

    private static final String VALID_PIPELINE_NAME = "test-pipeline";

    @TempDir
    Path tempDir;

    File validYamlFile;

    @BeforeEach
    void setup() throws IOException {
        validYamlFile = tempDir.resolve("pipeline.yaml").toFile();
        try (FileWriter writer = new FileWriter(validYamlFile)) {
            writer.write("name: " + VALID_PIPELINE_NAME); // minimal valid content
        }
    }

    @Test
    void testFileNotFoundThrowsException() {
        String invalidPath = "nonexistent.yaml";
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            YamlPipelineValidator.validatePipeline(invalidPath);
        });
        assertTrue(exception.getMessage().contains("File not found"));
    }

    @Test
    void testYamlParsingFailure() {
        try (MockedStatic<YamlParser> parserMock = mockStatic(YamlParser.class)) {
            parserMock.when(() -> YamlParser.parseYaml(any(File.class)))
                    .thenThrow(new ValidationException("Parsing error"));

            assertThrows(ValidationException.class, () ->
                    YamlPipelineValidator.validatePipeline(validYamlFile.getAbsolutePath())
            );
        }
    }

    @Test
    void testSuccessfulValidation() throws Exception {
        Pipeline mockPipeline = mock(Pipeline.class);
        when(mockPipeline.getName()).thenReturn(VALID_PIPELINE_NAME);
        when(mockPipeline.getStages()).thenReturn(null); // You can mock actual stages if needed

        try (
                MockedStatic<YamlParser> parserMock = mockStatic(YamlParser.class);
                MockedStatic<PipelineValidator> pipelineValidatorMock = mockStatic(PipelineValidator.class);
                MockedStatic<JobValidator> jobValidatorMock = mockStatic(JobValidator.class);
                MockedStatic<PipelineNameManager> managerMock = mockStatic(PipelineNameManager.class)
        ) {
            parserMock.when(() -> YamlParser.parseYaml(any(File.class))).thenReturn(mockPipeline);

            PipelineNameManager nameManager = mock(PipelineNameManager.class);
            when(nameManager.isPipelineNameUnique(VALID_PIPELINE_NAME)).thenReturn(true);
//            managerMock.when(PipelineNameManager::new).thenReturn(nameManager);

            assertDoesNotThrow(() ->
                    YamlPipelineValidator.validatePipeline(validYamlFile.getAbsolutePath())
            );
        }
    }
}
