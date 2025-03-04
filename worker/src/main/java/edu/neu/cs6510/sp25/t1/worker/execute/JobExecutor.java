package edu.neu.cs6510.sp25.t1.worker.execute;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import edu.neu.cs6510.sp25.t1.common.api.BackendClientInterface;
import edu.neu.cs6510.sp25.t1.common.api.request.JobRequest;
import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;
import edu.neu.cs6510.sp25.t1.common.execution.JobExecution;

/**
 * JobExecutor is responsible for executing a job request inside a Docker container.
 */
@Service
public class JobExecutor {
  private static final Logger logger = LoggerFactory.getLogger(JobExecutor.class);
  private final DockerManagerInterface dockerManager;
  private final BackendClientInterface backendClient;

  /**
   * Constructor for JobExecutor.
   *
   * @param dockerManager The DockerManager for executing jobs inside containers.
   * @param backendClient The BackendClient for sending job execution updates.
   */
  @Autowired
  public JobExecutor(DockerManagerInterface dockerManager, BackendClientInterface backendClient) {
    this.dockerManager = dockerManager;
    this.backendClient = backendClient;
  }

  /**
   * Executes a job request inside a Docker container.
   *
   * @param jobRequest The job request containing execution details.
   * @return ExecutionStatus of the job execution.
   */
  public ExecutionStatus executeJob(JobRequest jobRequest) {
    if (jobRequest.getJobName() == null || jobRequest.getJobName().isBlank()) {
      logger.error("Invalid job request: missing job name.");
      return ExecutionStatus.FAILED;
    }

    // Step 1: Send Initial Job Status to Backend
    backendClient.sendJobStatus(jobRequest.getJobName(), ExecutionStatus.QUEUED);

    // Step 2: Check and Wait for Dependencies
    if (!jobRequest.getNeeds().isEmpty()) {
      logger.info("Job {} is waiting for dependencies: {}", jobRequest.getJobName(), jobRequest.getNeeds());
      if (!waitForDependencies(jobRequest.getNeeds())) {
        logger.error("Job {} failed: Dependencies did not complete in time", jobRequest.getJobName());
        backendClient.sendJobStatus(jobRequest.getJobName(), ExecutionStatus.FAILED);
        return ExecutionStatus.FAILED;
      }
    }

    // Step 3: Mark Job as RUNNING
    backendClient.sendJobStatus(jobRequest.getJobName(), ExecutionStatus.RUNNING);

    // Step 4: Always execute in Docker
    boolean success = executeInDocker(jobRequest);

    // Step 5: Determine final job status
    ExecutionStatus finalState = success ? ExecutionStatus.SUCCESS : ExecutionStatus.FAILED;
    backendClient.sendJobStatus(jobRequest.getJobName(), finalState);

    // Step 6: Log Execution Result
    logExecution(jobRequest, finalState);

    return finalState;
  }

  /**
   * Runs a job inside a Docker container.
   *
   * @param jobRequest The job request containing execution details.
   * @return true if execution was successful, false otherwise.
   */
  private boolean executeInDocker(JobRequest jobRequest) {
    logger.info("Running job in Docker: {}", jobRequest.getJobName());

    JobExecution jobExecution = new JobExecution(
            jobRequest.getJobName(),      // Job name
            jobRequest.getImage(),        // Docker image
            jobRequest.getScript(),       // Script commands
            jobRequest.getNeeds(),        // Dependencies
            false                         // Allow failure (default: false)
    );

    // Start Docker container
    String containerId = dockerManager.runContainer(jobExecution);
    if (containerId == null) {
      logger.error("Failed to start Docker container for job {}", jobRequest.getJobName());
      return false;
    }

    // Wait for container execution to complete
    boolean success = dockerManager.waitForContainer(containerId);

    // Cleanup container after execution
    dockerManager.cleanupContainer(containerId);

    return success;
  }

  /**
   * Waits for all dependencies to complete before executing the job.
   *
   * @param dependencies List of job dependencies.
   * @return true if all dependencies are complete, false otherwise.
   */
  boolean waitForDependencies(List<String> dependencies) {
    int attempts = 0;
    while (!areDependenciesComplete(dependencies)) {
      if (attempts++ >= 5) {
        return false;
      }
      try {
        Thread.sleep(2000);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        logger.error("Job execution interrupted", e);
        return false;
      }
    }
    return true;
  }

  /**
   * Checks if all dependencies have completed successfully.
   *
   * @param dependencies List of dependencies.
   * @return true if all dependencies are complete, false otherwise.
   */
  boolean areDependenciesComplete(List<String> dependencies) {
    for (String dependency : dependencies) {
      ExecutionStatus state = backendClient.getJobStatus(dependency);
      if (state != ExecutionStatus.SUCCESS) {
        return false;
      }
    }
    return true;
  }

  /**
   * Logs job execution details to a file.
   *
   * @param jobRequest      The job request details.
   * @param executionStatus The final execution status of the job.
   */
  void logExecution(JobRequest jobRequest, ExecutionStatus executionStatus) {
    try (FileWriter file = createFileWriter()) {
      String logEntry = String.format(
              "{ \"jobName\": \"%s\", \"status\": \"%s\" }\n",
              jobRequest.getJobName(),
              executionStatus.name()
      );
      file.write(logEntry);
      logger.info("Job execution logged: {}", logEntry);
    } catch (IOException e) {
      logger.error("Failed to write job execution log", e);
    }
  }

  /**
   * Provides a FileWriter for logging job execution.
   *
   * @return FileWriter instance.
   * @throws IOException If file access fails.
   */
  protected FileWriter createFileWriter() throws IOException {
    return new FileWriter("job-executions.log", true);
  }
}
