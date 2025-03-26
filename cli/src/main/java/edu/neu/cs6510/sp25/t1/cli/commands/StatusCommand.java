package edu.neu.cs6510.sp25.t1.cli.commands;

import java.io.IOException;
import java.util.concurrent.Callable;

import edu.neu.cs6510.sp25.t1.cli.CliApp;
import edu.neu.cs6510.sp25.t1.common.logging.PipelineLogger;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import picocli.CommandLine;

/**
 * Handles the `status` command to fetch pipeline execution status.
 */
@CommandLine.Command(
    name = "status",
    description = "Fetches the status of a CI/CD pipeline."
)
public class StatusCommand implements Callable<Integer> {

  private static final OkHttpClient HTTP_CLIENT = new OkHttpClient();
  private static final String BASE_URL = "http://localhost:8080/api/pipeline/";

  @CommandLine.ParentCommand
  private CliApp parent;

  @CommandLine.Option(names = {"--pipeline", "-p"}, required = true, description = "Specify the pipeline name.")
  private String pipelineName;

  @Override
  public Integer call() {
    if (pipelineName == null || pipelineName.trim().isEmpty()) {
      PipelineLogger.error("Pipeline name must be provided with --pipeline or -p.");
      return 1;
    }

    String url = BASE_URL + pipelineName;
    PipelineLogger.info("Fetching pipeline status for: " + pipelineName);
    PipelineLogger.debug("GET " + url);

    Request request = new Request.Builder()
        .url(url)
        .get()
        .addHeader("Accept", "application/json")
        .build();

    try (Response response = HTTP_CLIENT.newCall(request).execute()) {
      if (!response.isSuccessful()) {
        String errorBody = response.body() != null ? response.body().string() : "Empty response";
        PipelineLogger.error("Failed to fetch pipeline status.");
        PipelineLogger.error("HTTP Status: " + response.code());
        PipelineLogger.error("Response: " + errorBody);
        return 1;
      }

      String responseBody = response.body() != null ? response.body().string() : "No content";
      PipelineLogger.info("Pipeline Status:");
      PipelineLogger.info(responseBody);
      return 0;

    } catch (IOException e) {
      PipelineLogger.error("Error while contacting backend: " + e.getMessage());
      return 1;
    }
  }
}
