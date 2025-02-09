package edu.neu.cs6510.sp25.t1.cli.commands;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.net.URISyntaxException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URI;
import picocli.CommandLine.Command;

@Command(name = "run", description = "Trigger CI/CD pipeline execution")
public class RunCommand implements Runnable {

    @Override
    public void run(){
        try {
            System.out.println("CI/CD pipeline is being executed.");
            execute();
        } catch (Exception e) {
            System.err.println("An exception occurred in run(): " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Executes the run command, parses the pipeline configuration file, 
     * sends a request to the backend API, and displays the result.
     * 
     * <p>This method performs the following steps:</p>
     * <ol>
     *     <li>Reads the pipeline configuration file.</li>
     *     <li>If reading fails, prints an error message and returns.</li>
     *     <li>Sends a request to the backend API.</li>
     *     <li>If the request fails, prints an error message and returns.</li>
     *     <li>Displays a success or failure message based on the API response.</li>
     * </ol>
     */
    public void execute() {
        try {
            // Parse the pipeline configuration file
            final String pipelineConfig = readPipelineConfig(".pipelines/pipeline-config.json");
            if (pipelineConfig == null) {
                System.err.println("Error: Unable to read pipeline configuration file.");
                return;
            }

            // Send request to the backend API
            final Integer response = sendRequestToApi(pipelineConfig);
            if (response == null || response == 0) {
                System.err.println("Error: Unable to connect to backend API.");
                return;
            }
            
            // Display message
            displayMessage(response);

        } catch (Exception e) {
            System.err.println("An exception occurred in execute(): " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Reads the pipeline configuration file from the specified file path.
     *
     * <p>This method attempts to read the contents of the file and return it as a string.
     * If an error occurs during reading, it prints an error message and returns null.</p>
     *
     * @param filePath the path to the pipeline configuration file.
     * @return a String containing the contents of the file, or null if an error occurs.
     */
    private String readPipelineConfig(String filePath) {
        try {
            return new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
            System.err.println("Error occurred while reading the file: " + e.getMessage());
            return null;
        }
    }

    /**
     * Sends a request to the backend API with the provided pipeline configuration.
     *
     * <p>This method establishes a connection to the specified API endpoint, sends the
     * pipeline configuration as a JSON payload, and returns the response code.</p>
     *
     * <p>If the request is successful and the response code is HTTP_OK (200), it returns 1.
     * If the request fails or the response code indicates an error, it prints an error message
     * and returns 0.</p>
     *
     * @param pipelineConfig the JSON string representing the pipeline configuration to be sent to the API.
     * @return an Integer indicating the result of the API request: 
     *         1 for success (HTTP_OK), 0 for failure.
     */
    private Integer sendRequestToApi(String pipelineConfig) {
        try {
            final URI uri = new URI("http://localhost:3000/pipelines");
            final URL url = uri.toURL();
            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");

            connection.getOutputStream().write(pipelineConfig.getBytes());

            final int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                return 1;
            } else {
                System.err.println("Error: API returned response code " + responseCode);
                return 0;
            }
        } catch (URISyntaxException e) {
            System.err.println("Invalid URI syntax: " + e.getMessage());
            return 0;
        } catch (IOException e) {
            System.err.println("Error occurred while opening the connection: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Displays a message based on the response from the API.
     *
     * <p>This method checks the response code and prints a success message if the response
     * indicates a successful execution (response code 1). If the response is null or indicates
     * a failure, it prints an error message.</p>
     *
     * @param response the response code from the API, where a value of 1 indicates success.
     */
    private void displayMessage(Integer response) {
        if (response != null && response == 1) {
            System.out.println("Cheers! Pipeline executed successfully!");
        } else {
            System.out.println("The pipeline has encountered a hiccup. Execution Failed! ");
        }
    }
}
