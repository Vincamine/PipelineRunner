package edu.neu.cs6510.sp25.t1.util;

import com.github.dockerjava.api.DockerClient;
import edu.neu.cs6510.sp25.t1.execution.StageExecutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class PipelineParserTest {
  private static final String TEST_YAML_FILE = "test_pipeline.yaml";
  private PipelineParser parser;
  private DockerClient dockerClient;

  @BeforeEach
  void setup() throws IOException {
    createTestYaml();
    dockerClient = Mockito.mock(DockerClient.class);
    parser = new PipelineParser(TEST_YAML_FILE, dockerClient);
  }

  @Test
  void testPipelineNameExtraction() {
    assertEquals("Sample Pipeline", parser.getPipelineName());
  }

  @Test
  void testStageExtraction() {
    List<StageExecutor> stages = parser.getStages();
    assertEquals(2, stages.size());
    assertEquals("build", stages.get(0).getStageName());
    assertEquals("test", stages.get(1).getStageName());
  }

  private void createTestYaml() throws IOException {
    String yamlContent = """
            pipeline:
              name: Sample Pipeline
              stages:
                - build
                - test
            jobs:
              - name: Build Job
                stage: build
                image: alpine:latest
                script:
                  - echo "Building..."
              - name: Test Job
                stage: test
                image: alpine:latest
                script:
                  - echo "Testing..."
            """;

    try (FileWriter writer = new FileWriter(TEST_YAML_FILE)) {
      writer.write(yamlContent);
    }
  }
}
