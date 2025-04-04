package edu.neu.cs6510.sp25.t1.cli.commands;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;

import edu.neu.cs6510.sp25.t1.cli.CliApp;
import edu.neu.cs6510.sp25.t1.cli.utils.DockerVolumeUtil;
import edu.neu.cs6510.sp25.t1.cli.utils.GitCloneUtil;
import edu.neu.cs6510.sp25.t1.common.logging.PipelineLogger;
import edu.neu.cs6510.sp25.t1.common.validation.utils.GitUtils;
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
//      GitUtils.isGitRootDirectory();
//      PipelineLogger.info("Starting pipeline execution...");

      // Ensure a valid file path is provided when running locally
      if (localRun) {
        if ((repo == null )&& (filePath == null || filePath.isEmpty())) {
          PipelineLogger.error("Pipeline configuration file must be specified when running locally (-f).");
          return 1;
        } else if (repo != null && !repo.isEmpty()) {
          try {
            String repoName = repo.substring(repo.lastIndexOf('/') + 1).replace(".git", "");
            File currentDir = new File(System.getProperty("user.dir"));
            File parentDir = currentDir.getParentFile();
            File cloneDir = new File(parentDir, "cloned-repos/" + repoName);

            PipelineLogger.info("Cloning repo " + repo + " to " + cloneDir.getAbsolutePath());
//          File cloned = GitCloneUtil.cloneRepository(repo, cloneDir);

            File cloned;
            if (cloneDir.exists() && new File(cloneDir, ".git").exists()) {
              PipelineLogger.info("Repository already cloned at: " + cloneDir.getAbsolutePath());
              cloned = cloneDir;

              try {
                PipelineLogger.info("Pulling latest changes from branch: " + branch);
                GitCloneUtil.pullLatest(cloned, branch);
              } catch (Exception e) {
                PipelineLogger.error("Failed to pull latest changes: " + e.getMessage());
                return 1;
              }

            } else {
              // for branch selection
              if ( branch!= null && !branch.isEmpty()) {
                PipelineLogger.info("Cloning repo " + repo + " to " + cloneDir.getAbsolutePath());
                cloned = GitCloneUtil.cloneRepository(repo, cloneDir, branch);
              } else {
                PipelineLogger.info("Cloning repo " + repo + " to " + cloneDir.getAbsolutePath());
                cloned = GitCloneUtil.cloneRepository(repo, cloneDir);
              }
            }

            // If no explicit commit is provided, fetch latest
            if (commit == null || commit.isEmpty()) {
              commit = GitUtils.getLatestCommitHash();
              PipelineLogger.debug("Using latest commit: " + commit);
            } else {
              PipelineLogger.debug("Using select commit: " + commit);
              GitCloneUtil.checkoutCommit(cloned, commit);
            }

            File pipelineDir = new File(cloned, ".pipelines");
            if (!pipelineDir.exists() || !pipelineDir.isDirectory()) {
              PipelineLogger.error("'.pipelines' directory not found in cloned repo: " + pipelineDir.getAbsolutePath());
              return 1;
            }

            // Look for any .yaml or .yml file
            File[] yamlFiles = pipelineDir.listFiles((dir, name) ->
                name.toLowerCase().endsWith(".yaml") || name.toLowerCase().endsWith(".yml")
            );

            if (yamlFiles == null || yamlFiles.length == 0) {
              PipelineLogger.error("No YAML pipeline file found in: " + pipelineDir.getAbsolutePath());
              return 1;
            }

            // Use the first YAML file found (or implement selection logic if needed)
            File pipelineFile = yamlFiles[0];
            PipelineLogger.info("Using pipeline file: " + pipelineFile.getAbsolutePath());

            this.filePath = pipelineFile.getAbsolutePath();
            // this log double checked file Path
            PipelineLogger.info("Using pipeline file at: " + this.filePath);
            // now lets passed on docker volume address to the backend
            // Replace filePath with Docker volume mount path

//            this.filePath = this.filePath.replace("\\", "\\\\");
//            PipelineLogger.info("Using pipeline file at: " + this.filePath);
          } catch (Exception e) {
            PipelineLogger.error("Failed to clone Git repo: " + e.getMessage());
            return 1;
          }
        }
      }


      // Validate the pipeline configuration file
//      PipelineLogger.info("Validating pipeline configuration: " + filePath);
//      try {
//        YamlPipelineValidator.validatePipeline(filePath);
//        PipelineLogger.info("Pipeline configuration is valid!");
//      } catch (Exception e) {
//        PipelineLogger.error("Validation failed: " + e.getMessage());
//        e.printStackTrace();
//        return 1;
//      }

      // Run the pipeline (either locally or remotely)
      return triggerPipelineExecution();

    } catch (Exception e) {
      PipelineLogger.error("Execution Error:" + e.getMessage());
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
      String dockerPath = DockerVolumeUtil.createVolumeFromHostDir(this.filePath);
      if (dockerPath == null) {
        PipelineLogger.error("Failed to prepare Docker volume for file path.");
        return 1;
      }
      this.filePath = dockerPath;

      if (!localRun && (repo == null || repo.isEmpty())) {
        PipelineLogger.error("Repository (--repo) must be specified for remote execution.");
        return 1;
      }

      String jsonPayload = String.format(
              "{\"repo\": \"%s\", \"branch\": \"%s\", \"commit\": \"%s\", \"pipeline\": \"%s\", \"filePath\": \"%s\", \"local\": %s}",
              (repo != null ? repo : ""), branch, commit, (pipeline != null ? pipeline : ""), filePath, localRun
      );

      PipelineLogger.debug("Sending request to backend: " + BACKEND_URL);
      PipelineLogger.debug("Payload: " + jsonPayload);

      Request request = createPostRequest(BACKEND_URL, jsonPayload);
      Response response = HTTP_CLIENT.newCall(request).execute();

      if (!response.isSuccessful()) {
        String responseBody = response.body() != null ? response.body().string() : "Empty response";
        PipelineLogger.error("Failed pipeline execution.");
        PipelineLogger.error("HTTP Status: " + response.code());
        PipelineLogger.error("Response: " + responseBody);
        return 1;
      }

      PipelineLogger.info("Pipeline execution started.");
      PipelineLogger.info("Response: " + response.body().string());
      return 0;

    } catch (IOException e) {
      PipelineLogger.error("Failed to communicate with backend: " + e.getMessage());
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
