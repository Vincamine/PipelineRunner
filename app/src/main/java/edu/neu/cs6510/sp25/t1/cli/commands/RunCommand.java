package edu.neu.cs6510.sp25.t1.cli.commands;

import java.io.IOException;
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
            final String pipelineConfig = readPipelineConfig(".pipelines/pipeline-config.json");
            if (pipelineConfig == null) {
                System.err.println("Error: Unable to read pipeline configuration file.");
                return;
            }

            final Integer response = sendRequestToApi(pipelineConfig);
            if (response == null || response == 0) {
                System.err.println("Error: Unable to connect to backend API.");
                return;
            }

            displayMessage(response);

        } catch (Exception e) {
            final String errorMessage = ErrorFormatter.format("RunCommand.java", 45, 15, e.getMessage());
            System.err.println(errorMessage);
        }
    }

    private String readPipelineConfig(String filePath) {
        try {
            return new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
            return null;
        }
    }

    private Integer sendRequestToApi(String pipelineConfig) {
        try {
            final URI uri = new URI("http://localhost:3000/pipelines");
            final URL url = uri.toURL();
            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");

            connection.getOutputStream().write(pipelineConfig.getBytes());

            return connection.getResponseCode() == HttpURLConnection.HTTP_OK ? 1 : 0;
        } catch (URISyntaxException | IOException e) {
            return 0;
        }
    }

    private void displayMessage(Integer response) {
        if (response != null && response == 1) {
            System.out.println("Cheers! Pipeline executed successfully!");
        } else {
            System.out.println("The pipeline has encountered a hiccup. Execution Failed!");
        }
    }
}
