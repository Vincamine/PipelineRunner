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
import edu.neu.cs6510.sp25.t1.common.model.Stage;
import edu.neu.cs6510.sp25.t1.common.validation.error.ValidationException;
import edu.neu.cs6510.sp25.t1.common.validation.parser.YamlParser;

public class JobValidatorTest {

    @TempDir
    Path tempDir;

    private Path validJobsPipeline;
    private Path noNameJobPipeline;
    private Path emptyNameJobPipeline;
    private Path noImageJobPipeline;
    private Path emptyImageJobPipeline;
    private Path noScriptJobPipeline;
    private Path emptyScriptJobPipeline;
    private Path duplicateJobNamesPipeline;
    private Path multipleErrorsJobPipeline;

    @BeforeEach
    void setUp() throws IOException {
        // Create a pipeline with valid jobs
        validJobsPipeline = tempDir.resolve("valid-jobs-pipeline.yaml");
        Files.writeString(validJobsPipeline,
                "name: valid-jobs\n" +
                        "stages:\n" +
                        "  - build\n" +
                        "  - test\n" +
                        "jobs:\n" +
                        "  - name: compile\n" +
                        "    stage: build\n" +
                        "    image: gradle:8.2-jdk17\n" +
                        "    script:\n" +
                        "      - ./gradlew compile\n" +
                        "  - name: test\n" +
                        "    stage: test\n" +
                        "    image: gradle:8.2-jdk17\n" +
                        "    script:\n" +
                        "      - ./gradlew test\n"
        );

        // Create a pipeline with a job missing name
        noNameJobPipeline = tempDir.resolve("no-name-job-pipeline.yaml");
        Files.writeString(noNameJobPipeline,
                "name: no-name-job\n" +
                        "stages:\n" +
                        "  - build\n" +
                        "jobs:\n" +
                        "  - stage: build\n" +  // Missing name
                        "    image: gradle:8.2-jdk17\n" +
                        "    script:\n" +
                        "      - ./gradlew compile\n"
        );

        // Create a pipeline with a job having empty name
        emptyNameJobPipeline = tempDir.resolve("empty-name-job-pipeline.yaml");
        Files.writeString(emptyNameJobPipeline,
                "name: empty-name-job\n" +
                        "stages:\n" +
                        "  - build\n" +
                        "jobs:\n" +
                        "  - name: \"\"\n" +  // Empty name
                        "    stage: build\n" +
                        "    image: gradle:8.2-jdk17\n" +
                        "    script:\n" +
                        "      - ./gradlew compile\n"
        );

        // Create a pipeline with a job missing Docker image
        noImageJobPipeline = tempDir.resolve("no-image-job-pipeline.yaml");
        Files.writeString(noImageJobPipeline,
                "name: no-image-job\n" +
                        "stages:\n" +
                        "  - build\n" +
                        "jobs:\n" +
                        "  - name: compile\n" +
                        "    stage: build\n" +
                        "    script:\n" +  // Missing image
                        "      - ./gradlew compile\n"
        );

        // Create a pipeline with a job having empty Docker image
        emptyImageJobPipeline = tempDir.resolve("empty-image-job-pipeline.yaml");
        Files.writeString(emptyImageJobPipeline,
                "name: empty-image-job\n" +
                        "stages:\n" +
                        "  - build\n" +
                        "jobs:\n" +
                        "  - name: compile\n" +
                        "    stage: build\n" +
                        "    image: \"\"\n" +  // Empty image
                        "    script:\n" +
                        "      - ./gradlew compile\n"
        );

        // Create a pipeline with a job missing script
        noScriptJobPipeline = tempDir.resolve("no-script-job-pipeline.yaml");
        Files.writeString(noScriptJobPipeline,
                "name: no-script-job\n" +
                        "stages:\n" +
                        "  - build\n" +
                        "jobs:\n" +
                        "  - name: compile\n" +
                        "    stage: build\n" +
                        "    image: gradle:8.2-jdk17\n"  // Missing script
        );

        // Create a pipeline with a job having empty script
        emptyScriptJobPipeline = tempDir.resolve("empty-script-job-pipeline.yaml");
        Files.writeString(emptyScriptJobPipeline,
                "name: empty-script-job\n" +
                        "stages:\n" +
                        "  - build\n" +
                        "jobs:\n" +
                        "  - name: compile\n" +
                        "    stage: build\n" +
                        "    image: gradle:8.2-jdk17\n" +
                        "    script: []\n"  // Empty script array
        );

        // Create a pipeline with duplicate job names
        duplicateJobNamesPipeline = tempDir.resolve("duplicate-job-names-pipeline.yaml");
        Files.writeString(duplicateJobNamesPipeline,
                "name: duplicate-job-names\n" +
                        "stages:\n" +
                        "  - build\n" +
                        "  - test\n" +
                        "jobs:\n" +
                        "  - name: duplicate-job\n" +
                        "    stage: build\n" +
                        "    image: gradle:8.2-jdk17\n" +
                        "    script:\n" +
                        "      - ./gradlew compile\n" +
                        "  - name: duplicate-job\n" +  // Duplicate job name
                        "    stage: test\n" +
                        "    image: gradle:8.2-jdk17\n" +
                        "    script:\n" +
                        "      - ./gradlew test\n"
        );

        // Create a pipeline with multiple job errors
        multipleErrorsJobPipeline = tempDir.resolve("multiple-errors-job-pipeline.yaml");
        Files.writeString(multipleErrorsJobPipeline,
                "name: multiple-errors-job\n" +
                        "stages:\n" +
                        "  - build\n" +
                        "jobs:\n" +
                        "  - name: \"\"\n" +  // Empty name
                        "    stage: build\n" +
                        "    image: \"\"\n" +  // Empty image
                        "    script: []\n"  // Empty script
        );
    }

    @Test
    void testValidJobs() throws Exception {
        // Parse the YAML file to get a Pipeline object
        Pipeline pipeline = YamlParser.parseYaml(validJobsPipeline.toFile());
        List<Stage> stages = pipeline.getStages();

        // This should not throw an exception
        assertDoesNotThrow(() -> {
            JobValidator.validateJobs(stages, validJobsPipeline.toString());
        });
    }

}