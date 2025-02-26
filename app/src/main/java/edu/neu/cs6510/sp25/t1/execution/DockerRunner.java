package edu.neu.cs6510.sp25.t1.execution;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.exception.DockerException;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.core.DockerClientBuilder;

/**
 * Responsible for creating and starting a Docker container for a pipeline execution.
 */
public class DockerRunner {
  private final DockerClient dockerClient;
  private final String image;

  public DockerRunner(String image) {
    this.dockerClient = DockerClientBuilder.getInstance("tcp://localhost:2375").build();
    this.image = image;
  }

  /**
   * Starts a new Docker container using the specified image.
   * @param command The command to run inside the container.
   * @return The container ID if successful.
   */
  public String startContainer(String... command) {
    try {
      System.out.println("Starting container with image: " + image);
      CreateContainerResponse container = dockerClient.createContainerCmd(image)
          .withHostConfig(HostConfig.newHostConfig())
          .withCmd(command)
          .exec();

      String containerId = container.getId();
      dockerClient.startContainerCmd(containerId).exec();
      System.out.println("Container started: " + containerId);
      return containerId;
    } catch (DockerException e) {
      throw new RuntimeException("Failed to start Docker container: " + e.getMessage(), e);
    }
  }
}