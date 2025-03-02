package edu.neu.cs6510.sp25.t1.cli;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import edu.neu.cs6510.sp25.t1.cli.api.CliBackendClient;
import edu.neu.cs6510.sp25.t1.cli.commands.RunCommand;
import edu.neu.cs6510.sp25.t1.common.api.RunPipelineRequest;
import picocli.CommandLine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CliBackendIntegrationTest {
  private CliBackendClient backendClient;
  private CommandLine cmd;

  @TempDir
  private Path tempDir; // Temporary directory for test files

  @BeforeEach
  void setUp() {
    backendClient = mock(CliBackendClient.class);
    cmd = new CommandLine(new RunCommand(backendClient));
  }

  @Test
  void testCliBackendRunPipeline() throws Exception {
    // ✅ Create a valid temporary YAML file
    File tempFile = tempDir.resolve("pipeline.yaml").toFile();
    String validYamlContent = """
            name: test-pipeline
            stages:
              - name: build
                jobs:
                  - name: compile
                    image: openjdk:17
                    script:
                      - javac Main.java
            """;
    Files.write(tempFile.toPath(), validYamlContent.getBytes());

    when(backendClient.runPipeline(any(RunPipelineRequest.class)))
            .thenReturn("Pipeline execution started");

    // ✅ Execute CLI command
    int exitCode = cmd.execute("--file", tempFile.getAbsolutePath());

    assertEquals(0, exitCode);

    verify(backendClient).runPipeline(argThat(request ->
            request.getPipeline().equals("test-pipeline") &&
                    request.getRepo().isEmpty() &&
                    request.getBranch().isEmpty() &&
                    request.getCommit().isEmpty() &&
                    !request.isLocal() &&
                    request.getOverrides().isEmpty() &&
                    request.getConfigPath().isEmpty()
    ));
  }
}
