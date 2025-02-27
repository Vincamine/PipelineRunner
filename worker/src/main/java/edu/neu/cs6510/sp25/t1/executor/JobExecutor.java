package edu.neu.cs6510.sp25.t1.executor;
import edu.neu.cs6510.sp25.t1.model.ExecutionStatus;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.PullImageResultCallback;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.core.command.WaitContainerResultCallback;


/**
 * Handles the execution of individual jobs in a Docker container.
 * Runs a job inside a Docker container.
 */
public class JobExecutor {
  private final String jobName;
  private final String dockerImage;
  private final String[] script;
  private ExecutionStatus status;
  private final DockerClient dockerClient;

  /**
   * Constructor to initialize JobExecutor with the specified parameters.
   * @param jobName
   * @param dockerImage
   * @param script
   * @param dockerClient
   */
  public JobExecutor(String jobName, String dockerImage, String[] script, DockerClient dockerClient) {
    this.jobName = jobName;
    this.dockerImage = dockerImage;
    this.script = script;
    this.status = ExecutionStatus.PENDING;
    this.dockerClient = dockerClient;
  }

  /**
   * Executes the job inside a Docker container.
   * @throws InterruptedException
   * @throws IOException
   */
  public void execute() {
    System.out.println("Starting job: " + jobName);
    status = ExecutionStatus.RUNNING;

    try {
      // Pull Docker image if not available
      dockerClient.pullImageCmd(dockerImage).exec(new PullImageResultCallback()).awaitCompletion();

      // Create and start container
      CreateContainerResponse container = dockerClient.createContainerCmd(dockerImage)
          .withHostConfig(HostConfig.newHostConfig())
          .withCmd("sh", "-c", String.join(" ", script))
          .exec();

      dockerClient.startContainerCmd(container.getId()).exec();
      System.out.println("Job is running in container: " + container.getId());

      // Wait for completion
      dockerClient.waitContainerCmd(container.getId()).exec(new WaitContainerResultCallback()).awaitCompletion();
      status = ExecutionStatus.SUCCESSFUL;
      System.out.println("Job completed successfully: " + jobName);

      // Cleanup
      dockerClient.removeContainerCmd(container.getId()).exec();
    } catch (Exception e) {
      status = ExecutionStatus.FAILED;
      System.err.println("Job execution error: " + e.getMessage());
    }
  }

  /**
   * Getter: Returns the status of the job execution
   * @return The status of the job execution.
   */
  public ExecutionStatus getStatus() {
    return status;
  }
}
