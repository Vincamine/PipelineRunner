package edu.neu.cs6510.sp25.t1.cli.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RunCommandTest {

    private RunCommand runCommand;
    private ByteArrayOutputStream outputStream;
    private ByteArrayOutputStream errorStream;
    private final String validConfig = "{\"pipeline\": \"test\"}";

    @BeforeEach
    void setUp() {
        runCommand = new RunCommand();
        outputStream = new ByteArrayOutputStream();
        errorStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        System.setErr(new PrintStream(errorStream));
    }

    @Test
    void testSuccessfulPipelineExecution(@TempDir Path tempDir) throws Exception {
        // Create temporary pipeline config file
        final Path configPath = tempDir.resolve(".pipelines/pipeline-config.json");
        Files.createDirectories(configPath.getParent());
        Files.write(configPath, validConfig.getBytes());

        // Mock HTTP connection
        final HttpURLConnection mockConnection = mock(HttpURLConnection.class);
        final URL mockUrl = new URI("http://localhost:3000/pipelines").toURL();

        try (MockedStatic<Paths> mockedPaths = Mockito.mockStatic(Paths.class)) {
            mockedPaths.when(() -> Paths.get(any(String.class)))
                      .thenReturn(configPath);

            // Mock URL connection
            try (MockedStatic<URL> mockedUrl = Mockito.mockStatic(URL.class)) {
                when(mockConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);
                when(mockConnection.getInputStream())
                    .thenReturn(new java.io.ByteArrayInputStream("Success".getBytes()));
                when(mockConnection.getOutputStream())
                    .thenReturn(new java.io.ByteArrayOutputStream());

                mockedUrl.when(() -> URL.class.cast(any()))
                         .thenReturn(mockUrl);

                runCommand.execute();

                assertTrue(outputStream.toString().contains("Pipeline executed successfully"));
                assertEquals("", errorStream.toString());
            }
        }
    }

    @Test
    void testFailedPipelineExecution(@TempDir Path tempDir) throws Exception {
        // Create temporary pipeline config file
        final Path configPath = tempDir.resolve(".pipelines/pipeline-config.json");
        Files.createDirectories(configPath.getParent());
        Files.write(configPath, validConfig.getBytes());

        // Mock HTTP connection
        final HttpURLConnection mockConnection = mock(HttpURLConnection.class);
        final URL mockUrl = new URI("http://localhost:3000/pipelines").toURL();

        try (MockedStatic<Paths> mockedPaths = Mockito.mockStatic(Paths.class)) {
            mockedPaths.when(() -> Paths.get(any(String.class)))
                      .thenReturn(configPath);

            // Mock URL connection with error response
            try (MockedStatic<URL> mockedUrl = Mockito.mockStatic(URL.class)) {
                when(mockConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_BAD_REQUEST);
                when(mockConnection.getInputStream())
                    .thenReturn(new java.io.ByteArrayInputStream("Error occurred".getBytes()));
                when(mockConnection.getOutputStream())
                    .thenReturn(new java.io.ByteArrayOutputStream());

                mockedUrl.when(() -> URL.class.cast(any()))
                         .thenReturn(mockUrl);

                runCommand.execute();

                assertTrue(outputStream.toString().contains("Pipeline has encountered a hiccup"));
            }
        }
    }

    @Test
    void testMissingConfigFile() {
        try (MockedStatic<Paths> mockedPaths = Mockito.mockStatic(Paths.class)) {
            mockedPaths.when(() -> Paths.get(any(String.class)))
                      .thenReturn(Paths.get("non-existent-file.json"));

            runCommand.execute();

            assertTrue(errorStream.toString().contains("Unable to read pipeline configuration file"));
        }
    }

    @Test
    void testApiConnectionError(@TempDir Path tempDir) throws Exception {
        // Create temporary pipeline config file
        final Path configPath = tempDir.resolve(".pipelines/pipeline-config.json");
        Files.createDirectories(configPath.getParent());
        Files.write(configPath, validConfig.getBytes());

        try (MockedStatic<Paths> mockedPaths = Mockito.mockStatic(Paths.class)) {
            mockedPaths.when(() -> Paths.get(any(String.class)))
                      .thenReturn(configPath);

            // Mock URL to throw IOException
            try (MockedStatic<URL> mockedUrl = Mockito.mockStatic(URL.class)) {
                mockedUrl.when(() -> URL.class.cast(any()))
                         .thenThrow(new IOException("Connection failed"));

                runCommand.execute();

                assertTrue(errorStream.toString().contains("Connection failed"));
            }
        }
    }

    @Test
    void testMainRunMethod() {
        runCommand.run();
        assertTrue(outputStream.toString().contains("CI/CD pipeline is being executed"));
    }

    @Test
    void testGetApiUrlWithMissingConfig() throws Exception {
        // Test when config.properties is missing
        try (MockedStatic<ClassLoader> mockedLoader = Mockito.mockStatic(ClassLoader.class)) {
            final ClassLoader mockLoader = mock(ClassLoader.class);
            when(mockLoader.getResourceAsStream("config.properties"))
                .thenReturn(null);

            mockedLoader.when(() -> ClassLoader.getSystemClassLoader())
                       .thenReturn(mockLoader);

            runCommand.execute();

            assertTrue(errorStream.toString().contains("unable to find config.properties"));
        }
    }
}