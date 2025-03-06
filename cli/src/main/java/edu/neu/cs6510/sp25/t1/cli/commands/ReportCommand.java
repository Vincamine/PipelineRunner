package edu.neu.cs6510.sp25.t1.cli.commands;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.Callable;

import edu.neu.cs6510.sp25.t1.common.logging.PipelineLogger;
import picocli.CommandLine;

/**
 * Fetches and prints reports of pipeline executions.
 */
@CommandLine.Command(name = "report", description = "Fetches pipeline execution reports.")
public class ReportCommand implements Callable<Integer> {

  @CommandLine.Option(names = {"--repo", "-r"}, description = "Repository URL or path", required = true)
  private String repo;

  @CommandLine.Option(names = "--pipeline", description = "Pipeline name")
  private String pipeline;

  @CommandLine.Option(names = "--run", description = "Specific pipeline execution run ID")
  private String runId;

  @CommandLine.Option(names = "--stage", description = "Stage name to filter report")
  private String stage;

  @CommandLine.Option(names = "--job", description = "Job name to filter report")
  private String job;

  @Override
  public Integer call() {
    PipelineLogger.info("Fetching report for repository: " + repo);

    String apiUrl = buildReportApiUrl();
    HttpClient client = HttpClient.newHttpClient();
    HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(apiUrl))
            .header("Accept", "application/json")
            .GET()
            .build();

    try {
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

      if (response.statusCode() == 200) {
        System.out.println(response.body());
        return 0;
      } else {
        System.err.println("Error: Failed to fetch report. Status: " + response.statusCode());
        return 1;
      }
    } catch (Exception e) {
      System.err.println("Error fetching report: " + e.getMessage());
      return 1;
    }
  }

  /**
   * Builds the API URL based on provided CLI arguments.
   */
  private String buildReportApiUrl() {
    StringBuilder url = new StringBuilder("http://localhost:8080/api/report?repo=" + repo);

    if (pipeline != null) url.append("&pipeline=").append(pipeline);
    if (runId != null) url.append("&run=").append(runId);
    if (stage != null) url.append("&stage=").append(stage);
    if (job != null) url.append("&job=").append(job);

    return url.toString();
  }
}