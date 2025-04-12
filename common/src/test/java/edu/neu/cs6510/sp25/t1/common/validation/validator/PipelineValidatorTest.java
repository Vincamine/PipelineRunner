package edu.neu.cs6510.sp25.t1.common.validation.validator;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import edu.neu.cs6510.sp25.t1.common.model.Pipeline;
import edu.neu.cs6510.sp25.t1.common.validation.error.ValidationException;
import edu.neu.cs6510.sp25.t1.common.validation.parser.YamlParser;

public class PipelineValidatorTest {

    @TempDir
    Path tempDir;

    private Path validPipeline;
    private Path emptyNamePipeline;
    private Path emptyStagesPipeline;
    private Path emptyJobsPipeline;
    private Path invalidDependencyPipeline;
    private Path cyclicDependencyPipeline;

    @BeforeEach
    void setUp() throws IOException {
        // Create a valid pipeline YAML file
        validPipeline = tempDir.resolve("valid-pipeline.yaml");
        Files.writeString(validPipeline,
                "name: test-pipeline\n" +
                        "\n" +
                        "stages:\n" +
                        "  - build\n" +
                        "  - test\n" +
                        "\n" +
                        "jobs:\n" +
                        "  - name: job1\n" +
                        "    dockerImage: gradle:8.2-jdk17\n" +
                        "    script:\n" +
                        "      - ./gradlew compile\n" +
                        "    allowFailure: false\n" +
                        "    stage: build\n" +
                        "  - name: job2\n" +
                        "    dockerImage: gradle:8.2-jdk17\n" +
                        "    script:\n" +
                        "      - ./gradlew test\n" +
                        "    allowFailure: false\n" +
                        "    stage: test\n" +
                        "    dependencies:\n" +
                        "      - job1\n"
        );

        // Create a pipeline with empty name
        emptyNamePipeline = tempDir.resolve("empty-name-pipeline.yaml");
        Files.writeString(emptyNamePipeline,
                "name: \n" +  // Empty name
                        "\n" +
                        "stages:\n" +
                        "  - build\n" +
                        "\n" +
                        "jobs:\n" +
                        "  - name: job1\n" +
                        "    dockerImage: gradle:8.2-jdk17\n" +
                        "    script:\n" +
                        "      - ./gradlew compile\n" +
                        "    allowFailure: false\n" +
                        "    stage: build\n"
        );

        // Create a pipeline with empty stages
        emptyStagesPipeline = tempDir.resolve("empty-stages-pipeline.yaml");
        Files.writeString(emptyStagesPipeline,
                "name: empty-stages\n" +
                        "\n" +
                        "stages: []\n" +  // Empty stages
                        "\n" +
                        "jobs:\n" +
                        "  - name: job1\n" +
                        "    dockerImage: gradle:8.2-jdk17\n" +
                        "    script:\n" +
                        "      - ./gradlew compile\n" +
                        "    allowFailure: false\n" +
                        "    stage: build\n"
        );

        // Create a pipeline with empty jobs
        emptyJobsPipeline = tempDir.resolve("empty-jobs-pipeline.yaml");
        Files.writeString(emptyJobsPipeline,
                "name: empty-jobs\n" +
                        "\n" +
                        "stages:\n" +
                        "  - build\n" +
                        "\n" +
                        "jobs: []\n"  // Empty jobs
        );

        // Create a pipeline with invalid dependency
        invalidDependencyPipeline = tempDir.resolve("invalid-dependency-pipeline.yaml");
        Files.writeString(invalidDependencyPipeline,
                "name: invalid-dependency\n" +
                        "\n" +
                        "stages:\n" +
                        "  - build\n" +
                        "\n" +
                        "jobs:\n" +
                        "  - name: job1\n" +
                        "    dockerImage: gradle:8.2-jdk17\n" +
                        "    script:\n" +
                        "      - ./gradlew compile\n" +
                        "    allowFailure: false\n" +
                        "    stage: build\n" +
                        "    dependencies:\n" +
                        "      - non-existent-job\n"  // Non-existent dependency
        );

        // Create a pipeline with cyclic dependency
        cyclicDependencyPipeline = tempDir.resolve("cyclic-dependency-pipeline.yaml");
        Files.writeString(cyclicDependencyPipeline,
                "name: cyclic-dependency\n" +
                        "\n" +
                        "stages:\n" +
                        "  - build\n" +
                        "\n" +
                        "jobs:\n" +
                        "  - name: job1\n" +
                        "    dockerImage: gradle:8.2-jdk17\n" +
                        "    script:\n" +
                        "      - ./gradlew compile\n" +
                        "    allowFailure: false\n" +
                        "    stage: build\n" +
                        "    dependencies:\n" +
                        "      - job2\n" +  // Depends on job2
                        "  - name: job2\n" +
                        "    dockerImage: gradle:8.2-jdk17\n" +
                        "    script:\n" +
                        "      - ./gradlew test\n" +
                        "    allowFailure: false\n" +
                        "    stage: build\n" +
                        "    dependencies:\n" +
                        "      - job1\n"  // Depends on job1, creating a cycle
        );
    }

    @Test
    void testValidPipeline() throws Exception {
        // Parse the YAML file to get a Pipeline object
        Pipeline pipeline = YamlParser.parseYaml(validPipeline.toFile());

        // This should not throw an exception
        assertDoesNotThrow(() -> {
            PipelineValidator.validate(pipeline, validPipeline.toString());
        }, "Valid pipeline should not throw an exception");
    }

    @Test
    void testEmptyNamePipeline() throws Exception {
        // Parse the YAML file to get a Pipeline object
        Pipeline pipeline = YamlParser.parseYaml(emptyNamePipeline.toFile());

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            PipelineValidator.validate(pipeline, emptyNamePipeline.toString());
        });

        String message = exception.getMessage();
        assertTrue(message.contains("Pipeline name is required") || message.contains("null"),
                "Exception should mention empty pipeline name");
    }

    @Test
    void testEmptyStagesPipeline() throws Exception {
        // Parse the YAML file to get a Pipeline object
        Pipeline pipeline = YamlParser.parseYaml(emptyStagesPipeline.toFile());

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            PipelineValidator.validate(pipeline, emptyStagesPipeline.toString());
        });

        assertTrue(exception.getMessage().contains("Pipeline must contain at least one stage") ||
                        exception.getMessage().contains("empty"),
                "Exception should mention empty stages");
    }

    @Test
    void testEmptyJobsPipeline() throws Exception {
        // Parse the YAML file to get a Pipeline object
        Pipeline pipeline = YamlParser.parseYaml(emptyJobsPipeline.toFile());

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            PipelineValidator.validate(pipeline, emptyJobsPipeline.toString());
        });

        // Check error contains reference to jobs being required
        assertTrue(exception.getMessage().contains("must contain at least one job") ||
                        exception.getMessage().contains("empty jobs"),
                "Exception should mention empty jobs");
    }

    @Test
    void testInvalidDependencyPipeline() throws Exception {
        // Parse the YAML file to get a Pipeline object
        Pipeline pipeline = YamlParser.parseYaml(invalidDependencyPipeline.toFile());

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            PipelineValidator.validate(pipeline, invalidDependencyPipeline.toString());
        });

        // Check error contains reference to non-existent dependency
        assertTrue(exception.getMessage().contains("non-existent-job") ||
                        exception.getMessage().contains("depend") ||
                        exception.getMessage().contains("does not exist"),
                "Exception should mention invalid dependency");
    }

    @Test
    void testCyclicDependencyPipeline() throws Exception {
        // Parse the YAML file to get a Pipeline object
        Pipeline pipeline = YamlParser.parseYaml(cyclicDependencyPipeline.toFile());

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            PipelineValidator.validate(pipeline, cyclicDependencyPipeline.toString());
        });

        // Check error contains reference to cycles or dependencies
        assertTrue(exception.getMessage().contains("Cyclic") ||
                        exception.getMessage().contains("cycle") ||
                        exception.getMessage().contains("circular"),
                "Exception should mention cyclic dependencies");

        // Alternatively, directly test the cycle detection logic
        List<List<String>> cycles = PipelineValidator.detectCycles(pipeline);
        assertFalse(cycles.isEmpty(), "Should detect at least one cycle");
    }
}