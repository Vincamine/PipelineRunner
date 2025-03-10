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
  private static final String BACKEND_URL = "http://localhost:8080/api/pipeline/run"; // Change as needed

  @CommandLine.ParentCommand
  private CliApp parent; // Inherits options from CliApp

  /**
   * Validates the pipeline configuration file and runs the pipeline.
   *
   * @return 0 if successful, 1 if an error occurred.
   */
  @Override
  public Integer call() {
    try {
      // Access options from CliApp
      String repo = parent.repo;
      String branch = parent.branch;
      String commit = parent.commit.isEmpty() ? GitUtils.getLatestCommitHash() : parent.commit;
      String filePath = parent.filePath;
      String pipeline = parent.pipeline;
      boolean localRun = parent.localRun;

      // Validate the pipeline configuration file
      System.out.println("Validating pipeline configuration: " + filePath);
      YamlPipelineValidator.validatePipeline(filePath);
      System.out.println("Pipeline configuration is valid!");

      // Run the pipeline (either locally or remotely)
      if (localRun) {
        return runPipelineLocally(repo, branch, commit, pipeline, filePath);
      } else {
        return runPipelineRemotely(repo, branch, commit, pipeline, filePath);
      }

    } catch (Exception e) {
      System.err.println("[Error] " + e.getMessage());
      return 1;
    }
  }

  /**
   * Triggers a remote pipeline execution via the backend API.
   *
   * @param repo     The repository path or URL.
   * @param branch   The Git branch.
   * @param commit   The commit hash.
   * @param pipeline The pipeline name (if specified).
   * @param filePath The pipeline configuration file path.
   * @return 0 if successful, 1 if an error occurred.
   */
  private Integer runPipelineRemotely(String repo, String branch, String commit, String pipeline, String filePath) {
    try {
      String jsonPayload = String.format(
              "{\"repo\": \"%s\", \"branch\": \"%s\", \"commit\": \"%s\", \"pipeline\": \"%s\"}",
              repo, branch, commit, pipeline != null ? pipeline : filePath
      );

      Request request = createPostRequest(BACKEND_URL, jsonPayload);
      Response response = HTTP_CLIENT.newCall(request).execute();

      if (!response.isSuccessful()) {
        System.err.println("[Error] Failed to trigger remote pipeline execution.");
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
   * Simulates a local pipeline execution
   *
   * @param repo     The repository path or URL.
   * @param branch   The Git branch.
   * @param commit   The commit hash.
   * @param pipeline The pipeline name (if specified).
   * @param filePath The pipeline configuration file path.
   * @return 0 if successful, 1 if an error occurred.
   */
  private Integer runPipelineLocally(String repo, String branch, String commit, String pipeline, String filePath) {
    System.out.println("[Local Execution] Running the pipeline locally...");
    System.out.println("[Local Execution] Repository: " + repo);
    System.out.println("[Local Execution] Branch: " + branch);
    System.out.println("[Local Execution] Commit: " + commit);
    System.out.println("[Local Execution] Pipeline: " + (pipeline != null ? pipeline : filePath));

    System.out.println("[Local Execution] Simulating pipeline execution...");
    System.out.println("[Local Execution] Pipeline execution completed successfully.");
    return 0;
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
