package edu.neu.cs6510.sp25.t1.worker.executor;

import java.io.IOException;
import java.util.List;

/**
 * A simple class to run a docker container with a given image and commands.
 */
public class DockerRunner {
  private final String containerName;
  private final String image;
  private final List<String> commands;

  /**
   * Create a new DockerRunner with the given image and commands.
   *
   * @param image    The image to run.
   * @param commands The commands to run in the container.
   */
  public DockerRunner(String image, List<String> commands) {
    this.image = image;
    this.containerName = "ci-job-" + System.currentTimeMillis();
    this.commands = commands;
  }

  /**
   * Extracted method to allow mocking in tests.
   * @return A new ProcessBuilder with the given image and commands.
   */
  protected ProcessBuilder createProcessBuilder() {
    return new ProcessBuilder(
            "docker", "run", "--rm", "--name", containerName, image, "sh", "-c", String.join(" && ", commands)
    );
  }

  /**
   * Run the container with the given image and commands.
   *
   * @throws IOException          If there is an error starting the process.
   * @throws InterruptedException If the process is interrupted.
   */
  public void run() throws IOException, InterruptedException {
    Process process = createProcessBuilder().start();
    process.waitFor();
  }
}
