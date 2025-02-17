package edu.neu.cs6510.sp25.t1.cli.commands;

import edu.neu.cs6510.sp25.t1.model.ApiResponse;
import edu.neu.cs6510.sp25.t1.util.ErrorHandler;
import edu.neu.cs6510.sp25.t1.util.GitValidator;
import edu.neu.cs6510.sp25.t1.validation.YamlPipelineValidator;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

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

            if (pipeline != null && pipelineFile != null) {
                System.err.println("❌ Error: --pipeline and --file cannot be used together.");
                return;
            }

            if (isLocalRun) {
                executeLocalRun();
            } else {
                executeRemoteRun();
            }
        } catch (Exception e) {
            ErrorHandler.reportError(e.getMessage());
        }
    }

    private void executeLocalRun() {
        System.out.println("🔍 Validating local repository...");
        GitValidator.validateGitRepo();

        String filePath = (pipelineFile != null) ? pipelineFile : ".pipelines/pipeline.yaml";
        executePipeline(filePath);
    }

    private void executeRemoteRun() {
        if (!repo.startsWith("https://")) {
            System.err.println("❌ Error: Remote repo URL must start with 'https://'.");
            return;
        }

        String filePath = (pipelineFile != null) ? pipelineFile : ".pipelines/pipeline.yaml";
        executePipeline(filePath);
    }

    private void executePipeline(String filePath) {
        try {
            String pipelineConfig = readPipelineConfig(filePath);
            if (pipelineConfig == null) {
                System.err.println("❌ Error: Unable to read pipeline configuration.");
                return;
            }

            YamlPipelineValidator validator = new YamlPipelineValidator();
            if (!validator.validatePipeline(filePath)) {
                System.err.println("❌ Pipeline validation failed.");
                return;
            }

            ApiResponse apiResponse = sendRequestToApi(pipelineConfig);
            displayMessage(apiResponse.getStatusCode(), apiResponse.getResponseBody());

        } catch (Exception e) {
            ErrorHandler.reportError(e.getMessage());
        }
    }

    String readPipelineConfig(String filePath) {
        try {
            return new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
            return null;
        }
    }

    ApiResponse sendRequestToApi(String pipelineConfig) {
        try {
            URI uri = new URI(getApiUrl());
            HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");

            connection.getOutputStream().write(pipelineConfig.getBytes());

            int statusCode = connection.getResponseCode();
            String responseBody = new String(connection.getInputStream().readAllBytes());

            return new ApiResponse(statusCode, responseBody);
        } catch (URISyntaxException | IOException e) {
            return new ApiResponse(0, e.getMessage());
        }
    }

    private String getApiUrl() {
        Properties properties = new Properties();
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

    private void displayMessage(int response, String responseBody) {
        if (response == HttpURLConnection.HTTP_OK) {
            System.out.println("✅ Pipeline executed successfully!");
        } else {
            System.err.println("❌ Pipeline execution failed! Response: " + responseBody);
        }
    }
}
