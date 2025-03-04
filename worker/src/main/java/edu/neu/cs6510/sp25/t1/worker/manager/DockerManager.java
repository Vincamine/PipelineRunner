package edu.neu.cs6510.sp25.t1.worker.manager;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.HostConfig;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import edu.neu.cs6510.sp25.t1.worker.api.request.JobRequest;

import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;



/**
 * Manages Docker containers for job execution.
 * Handles the creation, execution, and cleanup of containers.
 */
@Service
public class DockerManager {
  private static final Logger logger = LoggerFactory.getLogger(DockerManager.class);
  private final DockerClient dockerClient;

  /**
   * Constructor with DockerClient.
   *
   *
   */
  public DockerManager() {
    DefaultDockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder().build();
    this.dockerClient = DockerClientImpl.getInstance(config)
            .withDockerCmdExecFactory(new HttpClient5DockerCmdExecFactory());
  }

  /**
   * Runs a job inside a Docker container.
   *
   * @param jobRequest The job request containing execution details.
   * @return The container ID if started successfully, otherwise null.
   */
  public String runContainer(JobRequest jobRequest) {
    try {
      // Create the Docker container
      CreateContainerResponse container = dockerClient.createContainerCmd(jobRequest.getImage())
              .withHostConfig(HostConfig.newHostConfig().withAutoRemove(true))
              .withCmd(jobRequest.getScript().toArray(new String[0])) // Pass script as command args
              .exec();

      String containerId = container.getId();

      // Start the container
      dockerClient.startContainerCmd(containerId).exec();
      logger.info("Started Docker container {} for job {}", containerId, jobRequest.getJobName());

      return containerId;
    } catch (Exception e) {
      logger.error("Failed to start Docker container for job: {}", jobRequest.getJobName(), e);
      return null;
    }
  }

  /**
   * Waits for a Docker container to complete execution.
   *
   * @param containerId The container ID.
   * @return True if execution was successful, false otherwise.
   */
  public boolean waitForContainer(String containerId) {
    try {
      dockerClient.waitContainerCmd(containerId).start().awaitCompletion();
      logger.info("Docker container {} completed execution successfully.", containerId);
      return true;
    } catch (Exception e) {
      logger.error("Error while waiting for container {}: {}", containerId, e.getMessage());
      return false;
    }
  }

  /**
   * Cleans up a Docker container after execution.
   *
   * @param containerId The container ID.
   */
  public void cleanupContainer(String containerId) {
    try {
      dockerClient.removeContainerCmd(containerId).exec();
      logger.info("Cleaned up Docker container {}", containerId);
    } catch (Exception e) {
      logger.error("Failed to remove container {}: {}", containerId, e.getMessage());
    }
  }
}
