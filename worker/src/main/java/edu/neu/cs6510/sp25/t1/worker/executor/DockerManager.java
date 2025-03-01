package edu.neu.cs6510.sp25.t1.worker.executor;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.StartContainerCmd;
import com.github.dockerjava.api.command.WaitContainerResultCallback;
import com.github.dockerjava.api.model.HostConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import edu.neu.cs6510.sp25.t1.common.model.execution.JobExecution;

/**
 * DockerManager class to manage Docker containers.
 */
@Service
public class DockerManager {
  private final DockerClient dockerClient;
  private static final Logger logger = LoggerFactory.getLogger(DockerManager.class);

  /**
   * Default constructor.
   */
  public DockerManager() {
    this.dockerClient = null;
  }

  /**
   * Constructor with DockerClient.
   *
   * @param dockerClient DockerClient
   */
  public DockerManager(DockerClient dockerClient) {
    this.dockerClient = dockerClient;
  }

  /**
   * Run a Docker container for a job.
   *
   * @param jobExecution JobExecution
   * @return Container ID
   */
  public String runContainer(JobExecution jobExecution) {
    try {
      CreateContainerResponse container = dockerClient.createContainerCmd(jobExecution.getJobName())
              .withImage(jobExecution.getJobName())
              .withHostConfig(HostConfig.newHostConfig().withAutoRemove(true))
              .withCmd(jobExecution.getJobName())
              .exec();

      StartContainerCmd startCmd = dockerClient.startContainerCmd(container.getId());
      startCmd.exec();

      return container.getId();
    } catch (Exception e) {
      logger.error("Failed to start Docker container for job: " + jobExecution.getJobName(), e);
      return null;
    }
  }

  /**
   * Start a container with the given image.
   *
   * @param image Image
   * @return Container ID
   */
  public String startContainer(String image) {
    CreateContainerResponse container = dockerClient.createContainerCmd(image)
            .withHostConfig(HostConfig.newHostConfig())
            .exec();
    dockerClient.startContainerCmd(container.getId()).exec();
    return container.getId();
  }

  /**
   * Wait for a container to complete.
   *
   * @param containerId Container ID
   * @return True if the container completed successfully
   */
  public boolean waitForContainer(String containerId) {
    try {
      dockerClient.waitContainerCmd(containerId)
              .exec(new WaitContainerResultCallback())
              .awaitCompletion();
      return true;
    } catch (Exception e) {
      logger.error("Error while waiting for container: " + containerId, e);
      return false;
    }
  }

  /**
   * Cleanup a container.
   *
   * @param containerId Container ID
   */
  public void cleanupContainer(String containerId) {
    try {
      dockerClient.removeContainerCmd(containerId).exec();
    } catch (Exception e) {
      logger.error("Failed to remove container: " + containerId, e);
    }
  }
}
