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
  private static final String REMOTE_BACKEND_URL = "http://localhost:8080/api/pipeline/run";
  private static final String LOCAL_BACKEND_URL = "http://localhost:8080/api/pipeline/run-local"; // Local execution endpoint

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
      if (localRun) {
        return runPipelineLocally();
      } else {
        return runPipelineRemotely();
      }

    } catch (Exception e) {
      System.err.println("[Error] " + e.getMessage());
      return 1;
    }
  }

  /**
   * Triggers a remote pipeline execution via the backend API.
   *
   * @return 0 if successful, 1 if an error occurred.
   */
  private Integer runPipelineRemotely() {
    try {
      if (repo == null || repo.isEmpty()) {
        System.err.println("[Error] Repository (--repo) must be specified for remote execution.");
        return 1;
      }

      String jsonPayload = String.format(
              "{\"repo\": \"%s\", \"branch\": \"%s\", \"commit\": \"%s\", \"pipeline\": \"%s\"}",
              repo, branch, commit, (pipeline != null ? pipeline : "")
      );

      Request request = createPostRequest(REMOTE_BACKEND_URL, jsonPayload);
      Response response = HTTP_CLIENT.newCall(request).execute();

      if (!response.isSuccessful()) {
        System.err.println("[Error] Failed to trigger remote pipeline execution.");
        return 1;
      }

      System.out.println("[Success] Pipeline execution started remotely.");
      System.out.println("Response: " + response.body().string());
      return 0;

    } catch (IOException e) {
      System.err.println("[Error] Failed to communicate with backend: " + e.getMessage());
      return 1;
    }
  }

  /**
   * Triggers a **local** pipeline execution via the backend API.
   *
   * @return 0 if successful, 1 if an error occurred.
   */
  private Integer runPipelineLocally() {
    try {
      String jsonPayload = String.format(
              "{\"branch\": \"%s\", \"commit\": \"%s\", \"pipeline\": \"%s\", \"file\": \"%s\", \"local\": true}",
              branch, commit, (pipeline != null ? pipeline : ""), filePath
      );

      Request request = createPostRequest(LOCAL_BACKEND_URL, jsonPayload);
      Response response = HTTP_CLIENT.newCall(request).execute();

      if (!response.isSuccessful()) {
        System.err.println("[Error] Failed to trigger local pipeline execution.");
        return 1;
      }

      System.out.println("[Success] Pipeline execution started locally.");
      System.out.println("Response: " + response.body().string());
      return 0;

    } catch (IOException e) {
      System.err.println("[Error] Failed to communicate with local backend: " + e.getMessage());
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
