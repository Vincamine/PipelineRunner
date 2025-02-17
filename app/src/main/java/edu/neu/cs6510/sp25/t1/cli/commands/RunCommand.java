package edu.neu.cs6510.sp25.t1.cli.commands;

import edu.neu.cs6510.sp25.t1.model.ApiResponse;
import edu.neu.cs6510.sp25.t1.util.ErrorHandler;
import edu.neu.cs6510.sp25.t1.validation.YamlPipelineValidator;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URI;
import java.net.URISyntaxException;
import picocli.CommandLine.Command;

/**
 * Command to trigger a CI/CD pipeline execution.
 * <p>
 * This command:
 * <ul>
 *     <li>Reads the pipeline configuration file from the `.pipelines/` directory.</li>
 *     <li>Validates the configuration using {@link YamlPipelineValidator}.</li>
 *     <li>Sends a request to the backend API to execute the pipeline.</li>
 *     <li>Displays the result of the execution.</li>
 * </ul>
 * </p>
 */
@Command(name = "run", description = "Trigger CI/CD pipeline execution")
public class RunCommand implements Runnable {

  @Override
  public void run() {
    try {
      System.out.println("🚀 CI/CD pipeline execution started.");
      execute();
    } catch (Exception e) {
      ErrorHandler.reportError(e.getMessage());
    }
  }

  /**
   * Executes the CI/CD pipeline:
   * <ul>
   *   <li>Reads the pipeline configuration file.</li>
   *   <li>Validates the pipeline structure.</li>
   *   <li>Sends a request to trigger pipeline execution.</li>
   * </ul>
   */
  public void execute() {
    try {
      final String filePath = ".pipelines/pipeline.yaml"; 
      final String pipelineConfig = readPipelineConfig(filePath);
      
      if (pipelineConfig == null) {
        System.err.println("❌ Error: Unable to read pipeline configuration file: " + filePath);
        return;
      }

      final YamlPipelineValidator validator = new YamlPipelineValidator();
      final boolean isValid = validator.validatePipeline(filePath);

      if (!isValid) {
        System.err.println("❌ Pipeline validation failed.");
        return;
      }

      final ApiResponse apiResponse = sendRequestToApi(pipelineConfig);

      if (apiResponse.isNotFound()) {
        System.err.println("❌ Error: Resource not found. Response: " + apiResponse.getResponseBody());
        return;
      }

      if (apiResponse.getStatusCode() != HttpURLConnection.HTTP_OK) {
        System.err.println("❌ Error: Unable to connect to backend API. Response: " + apiResponse.getResponseBody());
        return;
      }

      displayMessage(apiResponse.getStatusCode(), apiResponse.getResponseBody());

    } catch (Exception e) {
      ErrorHandler.reportError(e.getMessage());
    }
  }

  /**
   * Reads the pipeline configuration file.
   *
   * @param filePath The path to the pipeline configuration file.
   * @return The file contents as a string or {@code null} if an error occurs.
   */
  String readPipelineConfig(String filePath) {
    try {
      return new String(Files.readAllBytes(Paths.get(filePath)));
    } catch (IOException e) {
      System.err.println("❌ Error reading file: " + filePath);
      return null;
    }
  }

  /**
   * Retrieves the API URL from the configuration properties file.
   * If unavailable, a default API URL is used.
   *
   * @return The API URL as a string.
   */
  private String getApiUrl() {
    final Properties properties = new Properties();
    try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
      if (input == null) {
        System.err.println("⚠️ Warning: config.properties not found, using default API URL.");
        return "http://localhost:3000/pipelines";
      }
      properties.load(input);
      return properties.getProperty("api.url", "http://localhost:3000/pipelines");
    } catch (IOException ex) {
      return "http://localhost:3000/pipelines";
    }
  }

  /**
   * Sends a request to the backend API to trigger pipeline execution.
   *
   * @param pipelineConfig The pipeline configuration to send.
   * @return The API response.
   */
  ApiResponse sendRequestToApi(String pipelineConfig) {
    try {
      final URI uri = new URI(getApiUrl());
      final URL url = uri.toURL();
      final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
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
   * Displays a message based on the pipeline execution result.
   *
   * @param response The HTTP status code.
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
