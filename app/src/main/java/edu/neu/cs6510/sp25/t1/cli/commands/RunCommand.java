package edu.neu.cs6510.sp25.t1.cli.commands;

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
import edu.neu.cs6510.sp25.t1.cli.util.ErrorFormatter;

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
            final String errorMessage = ErrorFormatter.format("RunCommand.java", 30, 10, e.getMessage());
            System.err.println(errorMessage);
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
            final String errorMessage = ErrorFormatter.format("RunCommand.java", 45, 15, e.getMessage());
            System.err.println(errorMessage);
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
