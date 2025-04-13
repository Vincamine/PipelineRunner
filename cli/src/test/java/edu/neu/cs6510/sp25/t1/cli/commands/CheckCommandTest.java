package edu.neu.cs6510.sp25.t1.cli.commands;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.neu.cs6510.sp25.t1.common.logging.PipelineLogger;
import edu.neu.cs6510.sp25.t1.common.validation.error.ValidationException;
import edu.neu.cs6510.sp25.t1.common.validation.validator.YamlPipelineValidator;

@ExtendWith(MockitoExtension.class)
class CheckCommandTest {

    @TempDir
    Path tempDir;

    private CheckCommand command;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    // Method to set the private filePath field using reflection
    private void setFilePath(String path) throws Exception {
        Field filePathField = CheckCommand.class.getDeclaredField("filePath");
        filePathField.setAccessible(true);
        filePathField.set(command, path);
    }

    @BeforeEach
    void setUp() {
        command = new CheckCommand();
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    void testValidPipelineFile() throws Exception {
        // Create a valid pipeline file
        Path validFile = tempDir.resolve("valid.yaml");
        Files.writeString(validFile, "pipeline:\n  name: test\nstages:\n  - build");

        // Set up the command
        setFilePath(validFile.toString());

        try (MockedStatic<YamlPipelineValidator> mockedValidator = mockStatic(YamlPipelineValidator.class)) {
            // Set up the validator to do nothing (simulating successful validation)
            mockedValidator.when(() -> YamlPipelineValidator.validatePipeline(anyString()))
                    .then(invocation -> null); // Use doNothing() style

            // Execute the command
            Integer result = command.call();

            // Verify results
            assertEquals(0, result);
            assertTrue(outContent.toString().contains("Pipeline configuration is valid"));
            mockedValidator.verify(() -> YamlPipelineValidator.validatePipeline(validFile.toString()));
        }
    }

    @Test
    void testNonExistentFile() throws Exception {
        // Set a non-existent file path
        setFilePath("/path/to/nonexistent/file.yaml");

        // Execute the command
        Integer result = command.call();

        // Verify results
        assertEquals(1, result);
        assertTrue(errContent.toString().contains("does not exist"));
    }

    @Test
    void testFilePathIsNull() throws Exception {
        // Set null file path
        setFilePath(null);

        // Execute the command
        Integer result = command.call();

        // Verify results
        assertEquals(1, result);
        assertTrue(errContent.toString().contains("File path cannot be null"));
    }

    @Test
    void testValidationError() throws Exception {
        // Create a file with invalid content
        Path invalidFile = tempDir.resolve("invalid.yaml");
        Files.writeString(invalidFile, "invalid: yaml: content");

        // Set up the command
        setFilePath(invalidFile.toString());

        try (MockedStatic<YamlPipelineValidator> mockedValidator = mockStatic(YamlPipelineValidator.class)) {
            // Set up the validator to throw a ValidationException
            mockedValidator.when(() -> YamlPipelineValidator.validatePipeline(anyString()))
                    .thenThrow(new ValidationException("Invalid pipeline format"));

            // Execute the command
            Integer result = command.call();

            // Verify results
            assertEquals(1, result);
            assertTrue(errContent.toString().contains("Invalid pipeline"));
            mockedValidator.verify(() -> YamlPipelineValidator.validatePipeline(invalidFile.toString()));
        }
    }

    @Test
    void testUnexpectedError() throws Exception {
        // Create a valid file
        Path validFile = tempDir.resolve("error.yaml");
        Files.writeString(validFile, "pipeline:\n  name: test");

        // Set up the command
        setFilePath(validFile.toString());

        try (MockedStatic<YamlPipelineValidator> mockedValidator = mockStatic(YamlPipelineValidator.class)) {
            // Set up the validator to throw a RuntimeException
            mockedValidator.when(() -> YamlPipelineValidator.validatePipeline(anyString()))
                    .thenThrow(new RuntimeException("Unexpected error occurred"));

            // Execute the command
            Integer result = command.call();

            // Verify results
            assertEquals(1, result);
            assertTrue(errContent.toString().contains("Unexpected error"));
            mockedValidator.verify(() -> YamlPipelineValidator.validatePipeline(validFile.toString()));
        }
    }

    @Test
    void testUnreadableFile() throws Exception {
        // Since we can't easily control the File creation in the CheckCommand,
        // we'll test this indirectly by creating a real file that's not readable

        // Create a file
        Path unreadableFile = tempDir.resolve("unreadable.yaml");
        Files.writeString(unreadableFile, "pipeline:\n  name: test");
        File file = unreadableFile.toFile();

        // Try to make the file unreadable - this may not work on all systems
        // but will help test our functionality
        boolean madeUnreadable = file.setReadable(false);

        // Only proceed with the test if we were able to make the file unreadable
        if (madeUnreadable) {
            // Set up the command
            setFilePath(unreadableFile.toString());

            // Execute the command
            Integer result = command.call();

            // Verify results
            assertEquals(1, result);
            assertTrue(errContent.toString().contains("Cannot read pipeline file"));

            // Reset file permissions
            file.setReadable(true);
        } else {
            // Skip this test if we couldn't make the file unreadable
            System.out.println("Skipping unreadable file test - unable to set file permissions");
        }
    }
}