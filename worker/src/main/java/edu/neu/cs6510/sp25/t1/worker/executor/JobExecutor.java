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
 * JobExecutor is responsible for executing a job request.
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
   * Execute a job request.
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
        if (attempts++ >= 5) {  // Add a timeout limit (e.g., 5 retries)
          logger.error("Job {} failed: Dependencies did not complete in time", jobRequest.getJobName());
          return;
        }
        try {
          Thread.sleep(2000);  // Wait before retrying
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          logger.error("Job execution interrupted", e);
          return;
        }
      }
    }

    // Only now, after dependencies resolve, send RUNNING
    backendClient.sendJobStatus(jobRequest.getJobName(), ExecutionState.RUNNING);

    JobRunState jobRunState = new JobRunState(
            new JobConfig(jobRequest.getJobName(), "default-stage", "default-image",
                    List.of(), dependencies, false),
            ExecutionState.RUNNING.name(),
            false,
            List.of()
    );

    String containerId = dockerManager.runContainer(jobRunState);

    if (containerId != null) {
      boolean success = dockerManager.waitForContainer(containerId);
      ExecutionState finalState = success ? ExecutionState.SUCCESS : ExecutionState.FAILED;
      backendClient.sendJobStatus(jobRequest.getJobName(), finalState);
      dockerManager.cleanupContainer(containerId);
    } else {
      backendClient.sendJobStatus(jobRequest.getJobName(), ExecutionState.FAILED);
    }

    logExecution(jobRequest);
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
   * Log job execution to a file.
   *
   * @param jobRequest JobRequest
   */
  private void logExecution(JobRequest jobRequest) {
    try (FileWriter file = createFileWriter()) {  // ✅ Now using an extractable method
      String logEntry = String.format(
              "{ \"jobName\": \"%s\", \"status\": \"%s\" }\n",
              jobRequest.getJobName(),
              ExecutionState.UNKNOWN
      );
      file.write(logEntry);
      logger.info("Job execution logged: {}", logEntry);
    } catch (IOException e) {
      logger.error("Failed to write job execution log", e);
    }
  }

  /**
   * Extracted method to allow mocking
   *
   * @return FileWriter
   */
  protected FileWriter createFileWriter() throws IOException {
    return new FileWriter("job-executions.log", true);
  }
}
