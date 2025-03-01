package edu.neu.cs6510.sp25.t1.worker.executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import edu.neu.cs6510.sp25.t1.common.api.JobRequest;
import edu.neu.cs6510.sp25.t1.common.model.ExecutionState;
import edu.neu.cs6510.sp25.t1.common.model.definition.JobDefinition;
import edu.neu.cs6510.sp25.t1.common.model.execution.JobExecution;
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

    backendClient.sendJobStatus(jobRequest.getJobName(), ExecutionState.RUNNING);

    JobExecution jobExecution = new JobExecution(
            new JobDefinition(jobRequest.getJobName(), "default-stage", "default-image",
                    List.of(), List.of(), false), // Default values
            ExecutionState.RUNNING.name(),
            false,
            List.of()
    );

    String containerId = dockerManager.runContainer(jobExecution);

    if (containerId != null) {
      boolean success = dockerManager.waitForContainer(containerId);

      if (success) {
        backendClient.sendJobStatus(jobRequest.getJobName(), ExecutionState.SUCCESS);
      } else {
        backendClient.sendJobStatus(jobRequest.getJobName(), ExecutionState.FAILED);
      }

      dockerManager.cleanupContainer(containerId);
    } else {
      backendClient.sendJobStatus(jobRequest.getJobName(), ExecutionState.FAILED);
    }

    logExecution(jobRequest);
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
   * @return FileWriter
   */
  protected FileWriter createFileWriter() throws IOException {
    return new FileWriter("job-executions.log", true);
  }
}
