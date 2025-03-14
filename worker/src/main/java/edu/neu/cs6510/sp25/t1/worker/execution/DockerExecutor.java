package edu.neu.cs6510.sp25.t1.worker.execution;


import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.okhttp.OkHttpDockerCmdExecFactory;
import edu.neu.cs6510.sp25.t1.worker.error.DockerExecutionException;
import edu.neu.cs6510.sp25.t1.worker.error.JobExecutionConfigException;
import org.springframework.stereotype.Component;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import edu.neu.cs6510.sp25.t1.common.dto.JobExecutionDTO;
import edu.neu.cs6510.sp25.t1.common.dto.JobDTO;
import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


/**
 * Executes jobs inside Docker containers.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DockerExecutor {

  //adding docker client
  private final DockerClient dockerClient = createDockerClient();

  private DockerClient createDockerClient() {
    DefaultDockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder().build();

    return DockerClientBuilder.getInstance(config)
        .withDockerCmdExecFactory(new OkHttpDockerCmdExecFactory())
        .build();
  }

  /**
   * Executes a job inside a Docker container.
   *
   * @param jobExecution The job execution details.
   * @return The execution status.
   */
  public ExecutionStatus execute(JobExecutionDTO jobExecution) {
    JobDTO job = jobExecution.getJob();
    if (job == null) {
      throw new JobExecutionConfigException("Job details are missing");
    }

    String dockerImage = job.getDockerImage();
    List<String> script = job.getScript();

    if (script == null || script.isEmpty()) {
      throw new JobExecutionConfigException("Script commands are missing");
    }

    if (dockerImage == null || dockerImage.trim().isEmpty()) {
      throw new JobExecutionConfigException("Docker image is not specified");
    }

    String containerID = null;

    // using docker client
    try {
      dockerClient.pullImageCmd(dockerImage).start().awaitCompletion();
      String command = String.join(" && ", script);

      var container = dockerClient.createContainerCmd(dockerImage)
          .withCmd("sh", "-c", command)
          .exec();

      containerID = container.getId();
      log.info("Container ID: {}", containerID);

      dockerClient.startContainerCmd(containerID).exec();

      dockerClient.logContainerCmd(containerID)
          .withStdOut(true)
          .withStdErr(true)
          .withFollowStream(true)
          .exec(new com.github.dockerjava.core.command.LogContainerResultCallback() {
            @Override
            public void onNext(com.github.dockerjava.api.model.Frame frame) {
              String logLine = new String(frame.getPayload()).trim();
              log.info("[{}] {}", frame.getStreamType(), logLine);
            }
          }).awaitCompletion();

      Integer exitCode = dockerClient.waitContainerCmd(containerID).start().awaitStatusCode();
      log.info("Container exited with code: {}", exitCode);

      dockerClient.removeContainerCmd(containerID).withForce(true).exec();

      return exitCode == 0 ? ExecutionStatus.SUCCESS : ExecutionStatus.FAILED;
    } catch (Exception e) {
      log.error("Docker execution failed: {}", e.getMessage(), e);
      throw new DockerExecutionException("Docker execution failed: " + e.getMessage(), e);
    } finally {
      if (containerID != null) {
        try {
          dockerClient.removeContainerCmd(containerID).withForce(true).exec();
          log.info("Cleaned up container {}", containerID);
        } catch (Exception cleanupEx) {
          log.error("Failed to cleanup container {}", containerID, cleanupEx);
        }
      }
    }
  }
//      // Capture logs
//      StringBuilder outputLogs = new StringBuilder();
//      try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
//        String line;
//        while ((line = reader.readLine()) != null) {
//          outputLogs.append(line).append("\n");
//          log.debug("Docker output: {}", line);
//        }
//        log.info("Docker Execution Logs:\n{}", outputLogs);
//      }
//
//      int exitCode = process.waitFor();
//      log.info("Docker job exited with code: {}", exitCode);
//
//      if (exitCode != 0) {
//        String errorMessage = "Docker execution failed with exit code: " + exitCode;
//        log.error("❌ {}", errorMessage);
//        log.error("Execution logs: {}", outputLogs.toString());
//        throw new DockerExecutionException(errorMessage + "\nLogs: " + outputLogs.toString(), exitCode);
//      }
//
//      return ExecutionStatus.SUCCESS;
//
//    } catch (DockerExecutionException | JobExecutionConfigException e) {
//      throw e;
//    } catch (Exception e) {
//      log.error("❌ Docker execution failed: {}", e.getMessage(), e);
//      throw new DockerExecutionException("Docker execution failed: " + e.getMessage(), e);
//    }


//  private String[] buildDockerCommand(String dockerImage, List<String> script) {
//    StringBuilder scriptCommands = new StringBuilder();
//    for (String cmd : script) {
//      scriptCommands.append(cmd).append(" && ");
//    }
//
//    if (scriptCommands.length() > 4) {
//      scriptCommands.setLength(scriptCommands.length() - 4);
//    }
//
//    return new String[]{
//            "docker", "run", "--rm",
//            dockerImage,
//            "sh", "-c", scriptCommands.toString()
//    };
//  }
}
