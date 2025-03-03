package edu.neu.cs6510.sp25.t1.worker.manager;

import com.github.dockerjava.api.DockerClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Random;

import edu.neu.cs6510.sp25.t1.common.execution.JobExecution;
import edu.neu.cs6510.sp25.t1.common.manager.DockerManagerInterface;


/**
 * Manages Docker containers for job execution.
 * <p>
 * Currently, this implementation uses a **simulated execution** to allow development
 * of reporting functionality before real execution is enabled. Jobs have a **90% chance**
 * of success and a **10% chance** of failure.
 * <p>
 * To enable real execution, uncomment the actual Docker API calls.
 */
@Service
public class DockerManager implements DockerManagerInterface {
  private final DockerClient dockerClient;
  private static final Logger logger = LoggerFactory.getLogger(DockerManager.class);
  private static final Random random = new Random();

  /**
   * Default constructor.
   */
  public DockerManager() {
    this.dockerClient = null;
  }

  /**
   * Constructor with DockerClient.
   *
   * @param dockerClient DockerClient instance
   */
  public DockerManager(DockerClient dockerClient) {
    this.dockerClient = dockerClient;
  }

  /**
   * Simulates running a Docker container for job execution.
   * <p>
   * Instead of actually starting a container, this method returns a fake container ID.
   * <p>
   * **Future Implementation:** Uncomment Docker API calls to enable real execution.
   *
   * @param jobExecution The job execution details.
   * @return Simulated container ID.
   */
  public String runContainer(JobExecution jobExecution) {
    String containerId = "simulated-container-" + jobExecution.getName();
    logger.info("Simulated start of Docker container for job {}: {}", jobExecution.getName(), containerId);

        /*
        // Real Docker Execution (Uncomment to enable)
        try {
            CreateContainerResponse container = dockerClient.createContainerCmd(jobExecution.getJobName())
                    .withImage(jobExecution.getJobName())
                    .withHostConfig(HostConfig.newHostConfig().withAutoRemove(true))
                    .exec();

            String containerId = container.getId();
            dockerClient.startContainerCmd(containerId).exec();

            logger.info("Started real container {} for job {}", containerId, jobExecution.getJobName());
            return containerId;
        } catch (Exception e) {
            logger.error("Failed to start Docker container for job: {}", jobExecution.getJobName(), e);
            return null;
        }
        */

    return containerId;
  }

  /**
   * Simulates waiting for a Docker container to complete execution.
   * <p>
   * **90% of jobs succeed, 10% fail** for testing report functionality.
   * <p>
   * **Future Implementation:** Uncomment Docker API calls to enable real execution monitoring.
   *
   * @param containerId The simulated container ID.
   * @return True if execution is simulated as successful, false otherwise.
   */
  public boolean waitForContainer(String containerId) {
    boolean success = random.nextDouble() < 0.90; // 90% success rate

    if (success) {
      logger.info("Simulated Docker container {} completed successfully.", containerId);
      return true;
    } else {
      logger.error("Simulated Docker container {} failed.", containerId);
      return false;
    }

        /*
        // Real Docker Execution (Uncomment to enable)
        try {
            dockerClient.waitContainerCmd(containerId)
                    .exec(new WaitContainerResultCallback())
                    .awaitCompletion();
            return true;
        } catch (Exception e) {
            logger.error("Error while waiting for container: " + containerId, e);
            return false;
        }
        */
  }

  /**
   * Simulates cleaning up a Docker container after execution.
   * <p>
   * **Future Implementation:** Uncomment Docker API calls to enable real cleanup.
   *
   * @param containerId The simulated container ID.
   */
  public void cleanupContainer(String containerId) {
    logger.info("Simulated cleanup of Docker container {}", containerId);

        /*
        // Real Docker Cleanup (Uncomment to enable)
        try {
            dockerClient.removeContainerCmd(containerId).exec();
        } catch (Exception e) {
            logger.error("Failed to remove container: " + containerId, e);
        }
        */
  }
}