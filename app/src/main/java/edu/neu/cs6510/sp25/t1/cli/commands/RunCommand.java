package edu.neu.cs6510.sp25.t1.cli.commands;

import edu.neu.cs6510.sp25.t1.model.ApiResponse;
import edu.neu.cs6510.sp25.t1.util.ErrorHandler;
import edu.neu.cs6510.sp25.t1.util.GitValidator;
import edu.neu.cs6510.sp25.t1.validation.YamlPipelineValidator;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * CLI command to trigger a CI/CD pipeline execution.
 * Supports both local and remote repositories.
 */
@Command(name = "run", description = "Trigger CI/CD pipeline execution")
public class RunCommand implements Runnable {

    @Option(names = "--local", description = "Run the pipeline on the local machine")
    private boolean isLocalRun;

    @Option(names = "--repo", description = "Repository location (local path or remote HTTPS URL)", required = true)
    private String repo;

    @Option(names = "--branch", description = "Branch name to run the pipeline on", defaultValue = "main")
    private String branch;

    @Option(names = "--commit", description = "Commit hash to run the pipeline on")
    private String commit;

    @Option(names = "--pipeline", description = "Pipeline name to execute")
    private String pipeline;

    @Option(names = "--file", description = "Path to the pipeline configuration file")
    private String pipelineFile;

    @Override
    public void run() {
        try {
            System.out.println("🚀 CI/CD pipeline execution started.");

            // Validate mutually exclusive options
            if (pipeline != null && pipelineFile != null) {
                System.err.println("❌ Error: --pipeline and --file cannot be used together.");
                System.exit(1);
                return;
            }

            if (isLocalRun) {
                executeLocalRun();
            } else {
                executeRemoteRun();
            }
        } catch (Exception e) {
            ErrorHandler.reportError(e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Executes the pipeline locally.
     */
    private void executeLocalRun() {
        System.out.println("🔍 Validating local repository...");
        if (!GitValidator.isGitRepository()) {
            System.err.println("❌ Error: Not a valid Git repository.");
            System.exit(1);
            return;
        }

        GitValidator.validateGitRepo();

        final String filePath = (pipelineFile != null) ? pipelineFile : ".pipelines/pipeline.yaml";
        executePipeline(filePath);
    }

    /**
     * Executes the pipeline for a remote repository.
     */
    private void executeRemoteRun() {
        if (!repo.startsWith("https://")) {
            System.err.println("❌ Error: Remote repo URL must start with 'https://'.");
            System.exit(1);
            return;
        }

        final String filePath = (pipelineFile != null) ? pipelineFile : ".pipelines/pipeline.yaml";
        executePipeline(filePath);
    }

    /**
     * Executes the pipeline, validating the YAML configuration and sending an API request.
     * @param filePath The path to the pipeline YAML file.
     */
    private void executePipeline(String filePath) {
        try {
            final String pipelineConfig = readPipelineConfig(filePath);
            if (pipelineConfig == null) {
                System.err.println("❌ Error: Unable to read pipeline configuration.");
                System.exit(1);
                return;
            }

            final YamlPipelineValidator validator = new YamlPipelineValidator();
            if (!validator.validatePipeline(filePath)) {
                System.err.println("❌ Pipeline validation failed.");
                System.exit(1);
                return;
            }

            final ApiResponse apiResponse = sendRequestToApi(pipelineConfig);
            displayMessage(apiResponse.getStatusCode(), apiResponse.getResponseBody());

            if (apiResponse.getStatusCode() != HttpURLConnection.HTTP_OK) {
                System.exit(1);
            }

        } catch (Exception e) {
            ErrorHandler.reportError(e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Reads the pipeline YAML configuration file.
     * @param filePath Path to the pipeline file.
     * @return The file contents as a string, or null if an error occurs.
     */
    String readPipelineConfig(String filePath) {
        try {
            final Path path = Paths.get(filePath);
            if (!Files.exists(path) || !Files.isRegularFile(path) || !Files.isReadable(path)) {
                System.err.println("❌ Error: Invalid or unreadable pipeline file: " + filePath);
                return null;
            }
            return Files.readString(path);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Sends a request to the API to trigger the pipeline.
     * @param pipelineConfig The pipeline configuration as a JSON string.
     * @return The API response.
     */
    ApiResponse sendRequestToApi(String pipelineConfig) {
        try {
            final URI uri = new URI(getApiUrl());
            final HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");

            connection.getOutputStream().write(pipelineConfig.getBytes());

            final int statusCode = connection.getResponseCode();
            final String responseBody = new String(connection.getInputStream().readAllBytes());

            return new ApiResponse(statusCode, responseBody);
        } catch (URISyntaxException | IOException e) {
            return new ApiResponse(0, e.getMessage());
        }
    }

    /**
     * Retrieves the API URL from the configuration properties file.
     * @return The API URL as a string.
     */
    private String getApiUrl() {
        final Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                return "http://localhost:3000/pipelines";
            }
            properties.load(input);
            return properties.getProperty("api.url", "http://localhost:3000/pipelines");
        } catch (IOException ex) {
            return "http://localhost:3000/pipelines";
        }
    }

    /**
     * Displays the response message after pipeline execution.
     * @param response The HTTP response status code.
     * @param responseBody The response body.
     */
    private void displayMessage(int response, String responseBody) {
        if (response == HttpURLConnection.HTTP_OK) {
            System.out.println("✅ Pipeline executed successfully!");
        } else {
            System.err.println("❌ Pipeline execution failed! Response: " + responseBody);
        }
    }
}
