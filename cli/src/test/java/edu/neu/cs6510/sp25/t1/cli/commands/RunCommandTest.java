//package edu.neu.cs6510.sp25.t1.cli.commands;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.io.TempDir;
//import org.mockito.MockedStatic;
//
//import java.io.File;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//
//import edu.neu.cs6510.sp25.t1.cli.api.CliBackendClient;
//import edu.neu.cs6510.sp25.t1.common.api.RunPipelineRequest;
//import edu.neu.cs6510.sp25.t1.common.config.PipelineConfig;
//import edu.neu.cs6510.sp25.t1.common.validation.parser.YamlParser;
//import edu.neu.cs6510.sp25.t1.common.validation.validator.PipelineValidator;
//import edu.neu.cs6510.sp25.t1.common.validation.error.ValidationException;
//import picocli.CommandLine;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.Mockito.any;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.mockStatic;
//import static org.mockito.Mockito.when;
//
//class RunCommandTest {
//  private RunCommand runCommand;
//  private CommandLine cmd;
//  private CliBackendClient backendClient;
//
//  @TempDir
//  private Path tempDir;
//
//  @BeforeEach
//  void setUp() {
//    backendClient = mock(CliBackendClient.class);
//    runCommand = new RunCommand(backendClient);
//    cmd = new CommandLine(runCommand);
//  }
//
//  @Test
//  void shouldReturnErrorWhenNoFileProvided() {
//    runCommand.configFile = null;
//    assertEquals(2, runCommand.call());
//  }
//
//  @Test
//  void shouldReturnErrorWhenFileDoesNotExist() {
//    runCommand.configFile = "nonexistent.yaml";
//    assertEquals(2, runCommand.call());
//  }
//
//  @Test
//  void shouldReturnValidationErrorWhenYamlParsingFails() throws Exception {
//    File tempFile = tempDir.resolve("invalid.yaml").toFile();
//    Files.write(tempFile.toPath(), "invalid-content".getBytes());
//    runCommand.configFile = tempFile.getAbsolutePath();
//
//    try (MockedStatic<YamlParser> parserMock = mockStatic(YamlParser.class)) {
//      parserMock.when(() -> YamlParser.parseYaml(tempFile)).thenThrow(new ValidationException("Invalid YAML"));
//
//      assertEquals(3, runCommand.call());
//    }
//  }
//
//  @Test
//  void shouldReturnErrorWhenBackendFails() throws Exception {
//    File tempFile = tempDir.resolve("valid.yaml").toFile();
//    Files.write(tempFile.toPath(), "pipeline: test-pipeline".getBytes());
//    runCommand.configFile = tempFile.getAbsolutePath();
//
//    PipelineConfig mockPipelineConfig = mock(PipelineConfig.class);
//    when(mockPipelineConfig.getName()).thenReturn("test-pipeline");
//
//    try (MockedStatic<YamlParser> parserMock = mockStatic(YamlParser.class);
//         MockedStatic<PipelineValidator> validatorMock = mockStatic(PipelineValidator.class)) {
//
//      parserMock.when(() -> YamlParser.parseYaml(tempFile)).thenReturn(mockPipelineConfig);
//      when(backendClient.runPipeline(any(RunPipelineRequest.class))).thenThrow(new IOException("Backend error"));
//
//      assertEquals(1, runCommand.call());
//    }
//  }
//
//  @Test
//  void shouldReturnSuccessWhenPipelineRunsSuccessfully() throws IOException {
//    File tempFile = tempDir.resolve("valid.yaml").toFile();
//    Files.write(tempFile.toPath(), "pipeline: test-pipeline".getBytes());
//    runCommand.configFile = tempFile.getAbsolutePath();
//
//    PipelineConfig mockPipelineConfig = mock(PipelineConfig.class);
//    when(mockPipelineConfig.getName()).thenReturn("test-pipeline");
//
//    try (MockedStatic<YamlParser> parserMock = mockStatic(YamlParser.class);
//         MockedStatic<PipelineValidator> validatorMock = mockStatic(PipelineValidator.class)) {
//
//      parserMock.when(() -> YamlParser.parseYaml(tempFile)).thenReturn(mockPipelineConfig);
//      when(backendClient.runPipeline(any(RunPipelineRequest.class))).thenReturn("Pipeline Execution Started");
//
//      assertEquals(0, runCommand.call());
//    }
//  }
//}
