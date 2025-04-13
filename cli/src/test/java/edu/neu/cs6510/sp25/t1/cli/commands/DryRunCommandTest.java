//package edu.neu.cs6510.sp25.t1.cli.commands;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.*;
//
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.PrintStream;
//import java.lang.reflect.Field;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.time.LocalDateTime;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.List;
//import java.util.UUID;
//
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.junit.jupiter.api.io.TempDir;
//import org.mockito.MockedStatic;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import edu.neu.cs6510.sp25.t1.common.model.Job;
//import edu.neu.cs6510.sp25.t1.common.model.Pipeline;
//import edu.neu.cs6510.sp25.t1.common.model.Stage;
//import edu.neu.cs6510.sp25.t1.common.validation.parser.YamlParser;
//import edu.neu.cs6510.sp25.t1.common.validation.validator.YamlPipelineValidator;
//
//@ExtendWith(MockitoExtension.class)
//class DryRunCommandTest {
//
//    @TempDir
//    Path tempDir;
//
//    private DryRunCommand command;
//    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
//    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
//    private final PrintStream originalOut = System.out;
//    private final PrintStream originalErr = System.err;
//
//    // Method to set the private filePath field using reflection
//    private void setFilePath(String path) throws Exception {
//        Field filePathField = DryRunCommand.class.getDeclaredField("filePath");
//        filePathField.setAccessible(true);
//        filePathField.set(command, path);
//    }
//
//    @BeforeEach
//    void setUp() {
//        command = new DryRunCommand();
//        System.setOut(new PrintStream(outContent));
//        System.setErr(new PrintStream(errContent));
//    }
//
//    @AfterEach
//    void tearDown() {
//        System.setOut(originalOut);
//        System.setErr(originalErr);
//    }
//
//    @Test
//    void testFileDoesNotExist() throws Exception {
//        // Set non-existent file path
//        setFilePath("/non/existent/path.yml");
//
//        // Execute the command
//        Integer result = command.call();
//
//        // Verify
//        assertEquals(1, result);
//        assertTrue(errContent.toString().contains("Error: Specified pipeline file does not exist"));
//    }
//
//    @Test
//    void testValidPipelineFile() throws Exception {
//        // Create a valid pipeline file
//        Path validFile = tempDir.resolve("valid.yaml");
//        Files.writeString(validFile, "pipeline:\n  name: test\nstages:\n  - build");
//
//        // Set up the command
//        setFilePath(validFile.toString());
//
//        // Create mock Pipeline with Stages and Jobs for the test
//        LocalDateTime now = LocalDateTime.now();
//
//        // Create a job
//        List<String> script = Arrays.asList("mvn clean", "mvn compile");
//        List<String> dependencies = Collections.emptyList();
//        Job job1 = new Job(
//                UUID.randomUUID(), UUID.randomUUID(), "compile", "build",
//                "maven:3.8.4", null, null, null, false, false,
//                script, null, null, now, now
//        );
//
//        // Create a stage with the job
//        List<Job> jobs = Arrays.asList(job1);
//        Stage stage1 = new Stage(
//                UUID.randomUUID(), "build", UUID.randomUUID(), 1,
//                jobs, now, now
//        );
//
//        // Create the pipeline
//        List<Stage> stages = Arrays.asList(stage1);
//        Pipeline mockPipeline = new Pipeline(
//                UUID.randomUUID(), "test-pipeline", null, null, null,
//                stages, null, now, now
//        );
//
//        try (MockedStatic<YamlPipelineValidator> mockedValidator = mockStatic(YamlPipelineValidator.class);
//             MockedStatic<YamlParser> mockedParser = mockStatic(YamlParser.class)) {
//
//            // Set up the validator to do nothing (successful validation)
//            mockedValidator.when(() -> YamlPipelineValidator.validatePipeline(anyString()))
//                    .then(invocation -> null);
//
//            // Set up the parser to return our mock pipeline
//            mockedParser.when(() -> YamlParser.parseYaml(any(File.class)))
//                    .thenReturn(mockPipeline);
//
//            // Execute the command
//            Integer result = command.call();
//
//            // Verify
//            assertEquals(0, result);
//            assertTrue(outContent.toString().contains("Validating pipeline configuration"));
//            assertTrue(outContent.toString().contains("Execution Plan:"));
//            assertTrue(outContent.toString().contains("build:"));
//            assertTrue(outContent.toString().contains("image: maven:3.8.4"));
//        }
//    }
//
//    @Test
//    void testValidationFailure() throws Exception {
//        // Create an invalid pipeline file
//        Path invalidFile = tempDir.resolve("invalid.yaml");
//        Files.writeString(invalidFile, "invalid: yaml: content");
//
//        // Set up the command
//        setFilePath(invalidFile.toString());
//
//        try (MockedStatic<YamlPipelineValidator> mockedValidator = mockStatic(YamlPipelineValidator.class)) {
//
//            // Set up the validator to throw an exception
//            mockedValidator.when(() -> YamlPipelineValidator.validatePipeline(anyString()))
//                    .thenThrow(new RuntimeException("Invalid pipeline configuration"));
//
//            // Execute the command
//            Integer result = command.call();
//
//            // Verify
//            assertEquals(1, result);
//            assertTrue(errContent.toString().contains("Validation failed"));
//        }
//    }
//
//    @Test
//    void testComplexPipelineWithDependencies() throws Exception {
//        // Create a valid pipeline file
//        Path validFile = tempDir.resolve("complex.yaml");
//        Files.writeString(validFile, "pipeline:\n  name: test\nstages:\n  - build\n  - test");
//
//        // Set up the command
//        setFilePath(validFile.toString());
//
//        // Create mock Pipeline with Stages and Jobs with dependencies
//        LocalDateTime now = LocalDateTime.now();
//
//        // Create jobs
//        Job compileJob = new Job(
//                UUID.randomUUID(), UUID.randomUUID(), "compile", "build",
//                "maven:3.8.4", null, null, null, false, false,
//                Arrays.asList("mvn compile"), null, null, now, now
//        );
//
//        Job unitTestJob = new Job(
//                UUID.randomUUID(), UUID.randomUUID(), "unit-test", "test",
//                "maven:3.8.4", null, null, null, false, false,
//                Arrays.asList("mvn test"), null, null, now, now
//        );
//
//        // Set dependencies for unit test job
//        Field dependenciesField = Job.class.getDeclaredField("dependencies");
//        dependenciesField.setAccessible(true);
//        dependenciesField.set(unitTestJob, Arrays.asList("compile"));
//
//        // Create stages
//        Stage buildStage = new Stage(
//                UUID.randomUUID(), "build", UUID.randomUUID(), 1,
//                Arrays.asList(compileJob), now, now
//        );
//
//        Stage testStage = new Stage(
//                UUID.randomUUID(), "test", UUID.randomUUID(), 2,
//                Arrays.asList(unitTestJob), now, now
//        );
//
//        // Create pipeline
//        Pipeline mockPipeline = new Pipeline(
//                UUID.randomUUID(), "complex-pipeline", null, null, null,
//                Arrays.asList(buildStage, testStage), null, now, now
//        );
//
//        try (MockedStatic<YamlPipelineValidator> mockedValidator = mockStatic(YamlPipelineValidator.class);
//             MockedStatic<YamlParser> mockedParser = mockStatic(YamlParser.class)) {
//
//            // Set up the validator to do nothing (successful validation)
//            mockedValidator.when(() -> YamlPipelineValidator.validatePipeline(anyString()))
//                    .then(invocation -> null);
//
//            // Set up the parser to return our mock pipeline
//            mockedParser.when(() -> YamlParser.parseYaml(any(File.class)))
//                    .thenReturn(mockPipeline);
//
//            // Execute the command
//            Integer result = command.call();
//
//            // Verify
//            assertEquals(0, result);
//            assertTrue(outContent.toString().contains("build:"));
//            assertTrue(outContent.toString().contains("test:"));
//
//
//            // Verify correct ordering in output
//            String output = outContent.toString();
//            int buildPos = output.indexOf("build:");
//            int testPos = output.indexOf("test:");
//            assertTrue(buildPos < testPos, "Build stage should appear before test stage");
//        }
//    }
//}