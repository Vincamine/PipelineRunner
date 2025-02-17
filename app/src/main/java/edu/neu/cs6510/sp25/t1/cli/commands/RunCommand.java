package edu.neu.cs6510.sp25.t1.cli.commands;

import edu.neu.cs6510.sp25.t1.cli.util.ErrorHandler;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.net.URISyntaxException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URI;
import picocli.CommandLine.Command;
import edu.neu.cs6510.sp25.t1.cli.validation.YamlPipelineValidator;

/**
 * Command to trigger CI/CD pipeline execution.
 */
@Command(name = "run", description = "Trigger CI/CD pipeline execution")
public class RunCommand implements Runnable {

  @Override
  public void run() {
    try {
      System.out.println("CI/CD pipeline is being executed.");
      execute();
    } catch (Exception e) {
      ErrorHandler.reportError(e.getMessage());
    }
  }

  /**
   * Executes the run command, parses the pipeline configuration file,
   * sends a request to the backend API, and displays the result.
   */
  public void execute() {
    try {
      final String filePath = ".pipelines/pipeline-config.json";
      final String pipelineConfig = readPipelineConfig(filePath);
      if (pipelineConfig == null) {
        System.err.println("Error: Unable to read pipeline configuration file: " + filePath);
        return;
      }

      final YamlPipelineValidator validator = new YamlPipelineValidator();
      final boolean isValid = validator.validatePipeline(filePath);
      if (!isValid) {
        System.err.println("Pipeline validation failed.");
        return;
      }
      final ApiResponse apiResponse = sendRequestToApi(pipelineConfig);
      if (apiResponse.isNotFound()) {
        System.err.println("Error: Resource not found. Response: " + apiResponse.getResponseBody());
        return;
      }

      if (apiResponse.getStatusCode() != HttpURLConnection.HTTP_OK) {
        System.err.println("Error: Unable to connect to backend API. Response: " + apiResponse.getResponseBody());
        return;
      }

      displayMessage(apiResponse.getStatusCode(), apiResponse.getResponseBody());

    } catch (Exception e) {
      ErrorHandler.reportError(e.getMessage());
    }
  }

  private String readPipelineConfig(String filePath) {
    try {
      return new String(Files.readAllBytes(Paths.get(filePath)));
    } catch (IOException e) {
      System.err.println("Error reading file: " + filePath);
      return null;
    }
  }

  /*
   * Retrieves the API URL from the configuration properties file.
   * If the file is not found or an error occurs while reading it,
   * a default URL is returned.
   *
   * @return the API URL as a String
   */
  private String getApiUrl() {
    final Properties properties = new Properties();
    try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
      if (input == null) {
        System.err.println("Sorry, unable to find config.properties");
        return "http://localhost:3000/pipelines";
      }
      properties.load(input);
      return properties.getProperty("api.url");
    } catch (IOException ex) {
      ex.printStackTrace();
      return "http://localhost:3000/pipelines";
    }
  }

  private ApiResponse sendRequestToApi(String pipelineConfig) {
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

  private void displayMessage(int response, String responseBody) {
    if (response == HttpURLConnection.HTTP_OK) {
      System.out.println("Cheers! Pipeline executed successfully!");
    } else {
      System.out.println("The pipeline has encountered a hiccup. Execution Failed! Response: " + responseBody);
    }
  }
}



//package edu.neu.cs6510.sp25.t1.cli.commands;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import edu.neu.cs6510.sp25.t1.cli.model.PipelineState;
//import edu.neu.cs6510.sp25.t1.cli.model.PipelineStatus;
//import edu.neu.cs6510.sp25.t1.cli.service.StatusService;
//import edu.neu.cs6510.sp25.t1.cli.util.ExecutionErrorHandler;
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.Properties;
//import java.nio.file.Files;
//import java.nio.file.Paths;
////import java.net.URISyntaxException;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.net.URI;
//import org.yaml.snakeyaml.Yaml;
//import picocli.CommandLine.Command;
//import edu.neu.cs6510.sp25.t1.cli.validation.YamlPipelineValidator;
//import picocli.CommandLine.Option;
//
///**
// * Command to trigger CI/CD pipeline execution with error handling.
// */
//@Command(
//    name = "run",
//    description = "Trigger CI/CD pipeline execution",
//    mixinStandardHelpOptions = true
//)
//public class RunCommand implements Runnable {
//  private static final int STATUS_CHECK_INTERVAL = 5000; // 5 seconds
//
//  @Option(
//      names = {"-f", "--file"},
//      description = "Path to the pipeline YAML file",
//      defaultValue = ".pipelines/pipeline.yaml"
//  )
//  private String yamlFilePath;
//
//  private final StatusService statusService;
//  private final ExecutionErrorHandler errorHandler;
//  private final ObjectMapper objectMapper;
//
//  public RunCommand() {
//    this.statusService = new StatusService();
//    this.errorHandler = new ExecutionErrorHandler();
//    this.objectMapper = new ObjectMapper();
//  }
//
//  @Override
//  public void run() {
//    try {
//      System.out.println("Starting CI/CD pipeline execution...");
//      executePipeline();
//    } catch (Exception e) {
//      errorHandler.handleStartupError(e);
//    }
//  }
//
//  private void executePipeline() {
//    try {
//      final String config = readConfiguration();
//      if (config == null) {
//        return;
//      }
//
//      final String pipelineId = startPipeline(config);
//      if (pipelineId == null) {
//        return;
//      }
//
//      monitorExecution(pipelineId);
//
//    } catch (Exception e) {
//      errorHandler.handleStartupError(e);
//    }
//  }
//
//  private String readConfiguration() {
//    try {
//      final String yamlContent = new String(Files.readAllBytes(Paths.get(yamlFilePath)));
//
//      final YamlPipelineValidator validator = new YamlPipelineValidator();
//      if (!validator.validatePipeline(yamlFilePath)) {
//        errorHandler.handleConfigError(yamlFilePath, "Configuration validation failed");
//        return null;
//      }
//
//      final Yaml yaml = new Yaml();
//      final Object obj = yaml.load(yamlContent);
//      return objectMapper.writeValueAsString(obj);
//
//    } catch (IOException e) {
//      errorHandler.handleConfigError(yamlFilePath, "Failed to read configuration: " + e.getMessage());
//      return null;
//    }
//  }
//
//  private String startPipeline(String config) {
//    try {
//      final ApiResponse response = sendRequestToApi(config);
//
//      if (response.getStatusCode() == HttpURLConnection.HTTP_OK) {
//        final String pipelineId = extractPipelineId(response.getResponseBody());
//        System.out.println("Pipeline started successfully with ID: " + pipelineId);
//        return pipelineId;
//      }
//
//      errorHandler.handleApiError(response);
//      return null;
//
//    } catch (Exception e) {
//      errorHandler.handleStartupError(e);
//      return null;
//    }
//  }
//
//  private void monitorExecution(String pipelineId) {
//    PipelineStatus lastStatus = null;
//
//    while (true) {
//      try {
//        final PipelineStatus status = statusService.getPipelineStatus(pipelineId);
//
//        if (shouldDisplayStatus(lastStatus, status)) {
//          displayExecutionStatus(status);
//          lastStatus = status;
//        }
//
//        if (!errorHandler.handlePipelineStatus(status)) {
//          break;
//        }
//
//        if (isExecutionComplete(status)) {
//          break;
//        }
//
//        Thread.sleep(STATUS_CHECK_INTERVAL);
//
//      } catch (InterruptedException e) {
//        Thread.currentThread().interrupt();
//        errorHandler.handleMonitoringError(e);
//        break;
//      } catch (Exception e) {
//        errorHandler.handleMonitoringError(e);
//      }
//    }
//  }
//
//  private boolean shouldDisplayStatus(PipelineStatus lastStatus, PipelineStatus currentStatus) {
//    if (lastStatus == null) {
//      return true;
//    }
//
//    return !lastStatus.getState().equals(currentStatus.getState()) ||
//        !lastStatus.getCurrentStage().equals(currentStatus.getCurrentStage()) ||
//        lastStatus.getProgress() != currentStatus.getProgress();
//  }
//
//  private void displayExecutionStatus(PipelineStatus status) {
//    System.out.printf("Status: %s | Stage: %s | Progress: %d%%%n",
//        status.getState(),
//        status.getCurrentStage(),
//        status.getProgress());
//  }
//
//  private boolean isExecutionComplete(PipelineStatus status) {
//    return status.getState() == PipelineState.SUCCEEDED ||
//        status.getState() == PipelineState.FAILED ||
//        status.getState() == PipelineState.CANCELLED;
//  }
//
//  private ApiResponse sendRequestToApi(String pipelineConfig) throws Exception {
//    final URI uri = new URI(getApiUrl());
//    final URL url = uri.toURL();
//    final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//    connection.setRequestMethod("POST");
//    connection.setDoOutput(true);
//    connection.setRequestProperty("Content-Type", "application/json");
//
//    connection.getOutputStream().write(pipelineConfig.getBytes());
//
//    final int statusCode = connection.getResponseCode();
//    final String responseBody = new String(connection.getInputStream().readAllBytes());
//
//    return new ApiResponse(statusCode, responseBody);
//  }
//
//  private String getApiUrl() {
//    final Properties properties = new Properties();
//    try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
//      if (input == null) {
//        System.err.println("Unable to find config.properties");
//        return "http://localhost:3000/pipelines";
//      }
//      properties.load(input);
//      return properties.getProperty("api.url");
//    } catch (IOException ex) {
//      ex.printStackTrace();
//      return "http://localhost:3000/pipelines";
//    }
//  }
//
//  private String extractPipelineId(String responseBody) {
//    return responseBody.replaceAll(".*\"id\":\"([^\"]+)\".*", "$1");
//  }
//}