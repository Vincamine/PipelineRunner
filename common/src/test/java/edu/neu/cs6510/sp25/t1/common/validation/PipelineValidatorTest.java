package edu.neu.cs6510.sp25.t1.common.validation;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import edu.neu.cs6510.sp25.t1.common.config.JobConfig;
import edu.neu.cs6510.sp25.t1.common.config.PipelineConfig;
import edu.neu.cs6510.sp25.t1.common.config.StageConfig;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PipelineValidatorTest {

  @Test
  void shouldPassValidationForValidPipeline() throws ValidationException {
    PipelineConfig validPipeline = new PipelineConfig(
            "test-pipeline",
            List.of(new StageConfig("build", List.of(new JobConfig("job1", "build", "image", List.of("script"), List.of(), false)))),
            Map.of()
    );

    assertDoesNotThrow(() -> PipelineValidator.validate(validPipeline));
  }

  @Test
  void shouldThrowExceptionForMissingPipelineName() {
    PipelineConfig invalidPipeline = new PipelineConfig(
            null, List.of(), Map.of()
    );

    ValidationException exception = assertThrows(ValidationException.class, () -> {
      PipelineValidator.validate(invalidPipeline);
    });

    assertTrue(exception.getMessage().contains("Pipeline name is required"));
  }

  @Test
  void shouldThrowExceptionForMissingStages() {
    PipelineConfig invalidPipeline = new PipelineConfig(
            "test-pipeline", List.of(), Map.of()
    );

    ValidationException exception = assertThrows(ValidationException.class, () -> {
      PipelineValidator.validate(invalidPipeline);
    });

    assertTrue(exception.getMessage().contains("At least one stage is required"));
  }

  @Test
  void shouldThrowExceptionForDuplicateJobNames() {
    PipelineConfig invalidPipeline = new PipelineConfig(
            "test-pipeline",
            List.of(new StageConfig("build", List.of(
                    new JobConfig("job1", "build", "image", List.of("script"), List.of(), false),
                    new JobConfig("job1", "build", "image", List.of("script"), List.of(), false)
            ))),
            Map.of()
    );

    ValidationException exception = assertThrows(ValidationException.class, () -> {
      PipelineValidator.validate(invalidPipeline);
    });

    assertTrue(exception.getMessage().contains("Duplicate job name found: job1"));
  }

  @Test
  void shouldThrowExceptionForCyclicDependencies() {
    PipelineConfig invalidPipeline = new PipelineConfig(
            "test-pipeline",
            List.of(new StageConfig("build", List.of(
                    new JobConfig("job1", "build", "image", List.of("script"), List.of("job2"), false),
                    new JobConfig("job2", "build", "image", List.of("script"), List.of("job1"), false)
            ))),
            Map.of()
    );

    ValidationException exception = assertThrows(ValidationException.class, () -> {
      PipelineValidator.validate(invalidPipeline);
    });

    assertTrue(exception.getMessage().contains("Cyclic dependencies detected"));
  }
}
