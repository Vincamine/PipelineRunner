package edu.neu.cs6510.sp25.t1.worker.executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import edu.neu.cs6510.sp25.t1.common.api.JobRequest;
import edu.neu.cs6510.sp25.t1.common.execution.ExecutionState;
import edu.neu.cs6510.sp25.t1.common.config.JobConfig;
import edu.neu.cs6510.sp25.t1.common.runtime.JobRunState;
import edu.neu.cs6510.sp25.t1.worker.client.BackendClient;

/**
 * JobExecutor is responsible for executing a job request, either in a container or locally.
 */
@Service
public class JobExecutor {
  private static final Logger logger = LoggerFactory.getLogger(JobExecutor.class);
  private final DockerManager dockerManager;
  private final BackendClient backendClient;

  /**
   * Constructor
   *
   * @param dockerManager DockerManager
   * @param backendClient BackendClient
   */
  @Autowired
  public JobExecutor(DockerManager dockerManager, BackendClient backendClient) {
    this.dockerManager = dockerManager;
    this.backendClient = backendClient;
  }

  /**
   * Executes a job request, either in a container or locally.
   *
   * @param jobRequest JobRequest
   */
  public void executeJob(JobRequest jobRequest) {
    if (jobRequest.getJobName() == null || jobRequest.getJobName().isBlank()) {
      logger.error("Invalid job request: missing job name.");
      return;
    }

    backendClient.sendJobStatus(jobRequest.getJobName(), ExecutionState.QUEUED);
    List<String> dependencies = jobRequest.getNeeds();

    // Wait for dependencies before moving to RUNNING
    if (!dependencies.isEmpty()) {
      logger.info("Job {} is waiting for dependencies: {}", jobRequest.getJobName(), dependencies);
      int attempts = 0;
      while (!areDependenciesComplete(dependencies)) {
        if (attempts++ >= 5) {
          logger.error("Job {} failed: Dependencies did not complete in time", jobRequest.getJobName());
          backendClient.sendJobStatus(jobRequest.getJobName(), ExecutionState.FAILED);
          return;
        }
        try {
          Thread.sleep(2000);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          logger.error("Job execution interrupted", e);
          return;
        }
      }
    }

    // Move job to RUNNING state
    backendClient.sendJobStatus(jobRequest.getJobName(), ExecutionState.RUNNING);

    JobRunState jobRunState = new JobRunState(
            new JobConfig(jobRequest.getJobName(), "default-stage", "default-image",
                    List.of(), dependencies, false),
            ExecutionState.RUNNING.name(),
            false,
            dependencies,
            true
    );



    boolean success;

    // Determine execution method
    if (jobRequest.isRunLocal()) {
      success = runLocalScript(jobRequest);
    } else {
      String containerId = dockerManager.runContainer(jobRunState);
      if (containerId != null) {
        success = dockerManager.waitForContainer(containerId);
        dockerManager.cleanupContainer(containerId);
      } else {
        success = false;
      }
    }

    // Update job execution state based on success
    ExecutionState finalState = success ? ExecutionState.SUCCESS : ExecutionState.FAILED;
    backendClient.sendJobStatus(jobRequest.getJobName(), finalState);

    logExecution(jobRequest, finalState);
  }

  /**
   * Runs a job script locally instead of using Docker.
   *
   * @param jobRequest JobRequest containing the script details.
   * @return true if the script executed successfully, false otherwise.
   */
  private boolean runLocalScript(JobRequest jobRequest) {
    logger.info("Running job locally: {}", jobRequest.getJobName());

    List<String> script = jobRequest.getScript();
    if (script.isEmpty()) {
      logger.error("Job {} has no script to execute", jobRequest.getJobName());
      return false;
    }

    try {
      for (String command : script) {
        logger.info("Executing command: {}", command);
        ProcessBuilder processBuilder = new ProcessBuilder("/bin/sh", "-c", command);
        processBuilder.inheritIO();  // Capture stdout and stderr
        Process process = processBuilder.start();
        int exitCode = process.waitFor();

        if (exitCode != 0) {
          logger.error("Job {} failed with exit code {}", jobRequest.getJobName(), exitCode);
          return false;
        }
      }
      return true;
    } catch (IOException | InterruptedException e) {
      logger.error("Failed to execute job {} locally", jobRequest.getJobName(), e);
      return false;
    }
  }

  /**
   * Checks if all dependencies are completed successfully.
   *
   * @param dependencies List of dependencies
   * @return true if all dependencies are complete, false otherwise
   */
  boolean areDependenciesComplete(List<String> dependencies) {
    for (String dependency : dependencies) {
      ExecutionState state = backendClient.getJobStatus(dependency);
      if (state != ExecutionState.SUCCESS) {
        return false;
      }
    }
    return true;
  }

  /**
   * Logs job execution to a file.
   *
   * @param jobRequest JobRequest
   * @param executionState Execution result state
   */
  private void logExecution(JobRequest jobRequest, ExecutionState executionState) {
    try (FileWriter file = createFileWriter()) {
      String logEntry = String.format(
              "{ \"jobName\": \"%s\", \"status\": \"%s\" }\n",
              jobRequest.getJobName(),
              executionState.name()
      );
      file.write(logEntry);
      logger.info("Job execution logged: {}", logEntry);
    } catch (IOException e) {
      logger.error("Failed to write job execution log", e);
    }
  }

  /**
   * Extracted method to allow mocking in tests.
   *
   * @return FileWriter instance
   * @throws IOException If file access fails
   */
  protected FileWriter createFileWriter() throws IOException {
    return new FileWriter("job-executions.log", true);
  }
}
