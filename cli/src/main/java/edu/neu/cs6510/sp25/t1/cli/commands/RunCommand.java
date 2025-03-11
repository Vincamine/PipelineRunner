package edu.neu.cs6510.sp25.t1.cli.commands;

import java.io.IOException;
import java.util.concurrent.Callable;

import edu.neu.cs6510.sp25.t1.cli.CliApp;
import edu.neu.cs6510.sp25.t1.cli.validation.utils.GitUtils;
import edu.neu.cs6510.sp25.t1.cli.validation.validator.YamlPipelineValidator;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import picocli.CommandLine;

/**
 * Handles the `run` command for executing a CI/CD pipeline.
 */
@CommandLine.Command(
        name = "run",
        description = "Runs a CI/CD pipeline after validating its configuration."
)
public class RunCommand implements Callable<Integer> {

  private static final OkHttpClient HTTP_CLIENT = new OkHttpClient();
  private static final String BACKEND_URL = "http://localhost:8080/api/pipeline/run"; // Unified URL

  @CommandLine.ParentCommand
  private CliApp parent; // Inherits global CLI options

  @CommandLine.Option(names = {"--repo", "-r"}, description = "Specify the repository URL.")
  private String repo;

  @CommandLine.Option(names = {"--branch", "-b"}, description = "Specify the Git branch.", defaultValue = "main")
  private String branch;

  @CommandLine.Option(names = {"--commit", "-c"}, description = "Specify the commit hash. Defaults to latest commit.")
  private String commit;

  @CommandLine.Option(names = {"-f", "--file"}, description = "Specify the pipeline config file.")
  private String filePath;

  @CommandLine.Option(names = {"--pipeline", "-p"}, description = "Specify the pipeline name to run.")
  private String pipeline;

  @CommandLine.Option(names = {"--local"}, description = "Run the pipeline locally.")
  private boolean localRun;

  /**
   * Validates the pipeline configuration file and runs the pipeline.
   *
   * @return 0 if successful, 1 if an error occurred.
   */
  @Override
  public Integer call() {
    try {
      // If no explicit commit is provided, fetch latest
      if (commit == null || commit.isEmpty()) {
        commit = GitUtils.getLatestCommitHash();
      }

      // Ensure a valid file path is provided when running locally
      if (localRun && (filePath == null || filePath.isEmpty())) {
        System.err.println("[Error] Pipeline configuration file must be specified when running locally (-f).");
        return 1;
      }

      // Validate the pipeline configuration file
      System.out.println("Validating pipeline configuration: " + filePath);
      YamlPipelineValidator.validatePipeline(filePath);
      System.out.println("Pipeline configuration is valid!");

      // Run the pipeline (either locally or remotely)
      return triggerPipelineExecution();

    } catch (Exception e) {
      System.err.println("[Error] " + e.getMessage());
      return 1;
    }
  }

  /**
   * Triggers a pipeline execution via the backend API.
   *
   * @return 0 if successful, 1 if an error occurred.
   */
  private Integer triggerPipelineExecution() {
    try {
      if (!localRun && (repo == null || repo.isEmpty())) {
        System.err.println("[Error] Repository (--repo) must be specified for remote execution.");
        return 1;
      }

      String jsonPayload = String.format(
              "{\"repo\": \"%s\", \"branch\": \"%s\", \"commit\": \"%s\", \"pipeline\": \"%s\", \"filePath\": \"%s\", \"local\": %s}",
              (repo != null ? repo : ""), branch, commit, (pipeline != null ? pipeline : ""), filePath, localRun
      );

      Request request = createPostRequest(BACKEND_URL, jsonPayload);
      Response response = HTTP_CLIENT.newCall(request).execute();

      if (!response.isSuccessful()) {
        String responseBody = response.body() != null ? response.body().string() : "Empty response";
        System.err.println("[Error] Failed pipeline execution.");
        System.err.println("HTTP Status: " + response.code());
        System.err.println("Response: " + responseBody);
        return 1;
      }

      System.out.println("[Success] Pipeline execution started.");
      System.out.println("Response: " + response.body().string());
      return 0;

    } catch (IOException e) {
      System.err.println("[Error] Failed to communicate with backend: " + e.getMessage());
      return 1;
    }
  }

  /**
   * Creates a POST request with the given payload.
   *
   * @param url     The request URL.
   * @param payload The request payload.
   * @return The constructed request object.
   */
  private Request createPostRequest(String url, String payload) {
    RequestBody body = RequestBody.create(payload, MediaType.get("application/json; charset=utf-8"));
    return new Request.Builder()
            .url(url)
            .post(body)
            .addHeader("Content-Type", "application/json")
            .addHeader("Accept", "application/json")
            .build();
  }
}
