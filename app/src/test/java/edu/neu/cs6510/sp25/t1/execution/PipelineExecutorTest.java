package edu.neu.cs6510.sp25.t1.execution;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;
import java.util.List;

/**
 * Integration test for PipelineExecutor using real Docker Desktop.
 */
public class PipelineExecutorTest {

  private PipelineExecutor pipelineExecutor;
  private DockerRunner dockerRunner;
  private static final String TEST_IMAGE = "alpine:latest";

  @BeforeEach
  void setup() {
    dockerRunner = new DockerRunner(TEST_IMAGE);
    List<StageExecutor> stages = Arrays.asList(
        new StageExecutor("Build Stage", List.of()),
        new StageExecutor("Test Stage", List.of())
    );
    pipelineExecutor = new PipelineExecutor("CI/CD Pipeline", stages, dockerRunner);
  }

  @Test
  void testExecutePipeline_Success() {
    pipelineExecutor.execute();
    assertEquals(ExecutionStatus.SUCCESSFUL, pipelineExecutor.getStatus());
    System.out.println("Pipeline executed successfully in container.");
  }

  @Test
  void testExecutePipeline_Failure() {
    List<StageExecutor> failingStages = Arrays.asList(
        new StageExecutor("Build Stage", List.of()),
        new StageExecutor("Test Stage", List.of()) {
          @Override
          public void execute() {
            System.out.println("Simulating stage failure.");
            setStageStatus(ExecutionStatus.FAILED);
          }
        }
    );
    pipelineExecutor = new PipelineExecutor("CI/CD Pipeline", failingStages, dockerRunner);

    pipelineExecutor.execute();
    assertEquals(ExecutionStatus.FAILED, pipelineExecutor.getStatus());
    System.out.println("Pipeline execution failed as expected.");
  }
}
