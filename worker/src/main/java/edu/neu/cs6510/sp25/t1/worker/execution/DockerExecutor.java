package edu.neu.cs6510.sp25.t1.worker.execution;


import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateVolumeResponse;
import com.github.dockerjava.api.model.AccessMode;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.Volume;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.okhttp.OkHttpDockerCmdExecFactory;
import edu.neu.cs6510.sp25.t1.worker.error.DockerExecutionException;
import edu.neu.cs6510.sp25.t1.worker.error.JobExecutionConfigException;
import edu.neu.cs6510.sp25.t1.worker.utils.FindPipelineName;
import org.springframework.stereotype.Component;
import java.io.BufferedReader;
import java.io.File;
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
  private final FindPipelineName findPipelineName;

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

    // get pipelineName
    String pipelineName = findPipelineName.getPipelineName(jobExecution);

    // 55-56 is about to make container path unique for every job
    String jobId = String.valueOf(job.getId());

    // create container path
    String containerPath = "/app/" + pipelineName;

    String dockerImage = job.getDockerImage();
    List<String> script = job.getScript();

    // retrieve working Directory from job database
    String workingDirectory = job.getWorkingDir();

    if (script == null || script.isEmpty()) {
      throw new JobExecutionConfigException("Script commands are missing");
    }

    if (dockerImage == null || dockerImage.trim().isEmpty()) {
      throw new JobExecutionConfigException("Docker image is not specified");
    }

    // confirm we have workDir
    if (workingDirectory == null || workingDirectory.trim().isEmpty()) {
      throw new JobExecutionConfigException("Working directory is not specified");
    }

    // verified the file exists in the working Dir
    File dir = new File(workingDirectory);
    if (!dir.exists() || !dir.isDirectory()) {
      throw new JobExecutionConfigException("Working directory does not exist or is not a directory");
    }

    String containerID = null;
    String volumeName = "pipeline-volume-" + pipelineName;
    boolean debugging = false;

    log.info("Executing job {} in container path {} with working directory {}", jobId, containerPath, workingDirectory);
    // using docker client
    try {
      dockerClient.pullImageCmd(dockerImage).start().awaitCompletion();
      log.info("Pulled Docker image: {}", dockerImage);
      String command = String.join(" && ", script);

      boolean exists = dockerClient.listVolumesCmd().exec().getVolumes().stream()
          .anyMatch(v -> volumeName.equals(v.getName()));
      if (!exists) {
        dockerClient.createVolumeCmd().withName(volumeName).exec();
        log.info("Created Docker volume: {}", volumeName);
      } else {
        log.info("Using existing Docker volume: {}", volumeName);
      }

//      Volume volume = new Volume(containerPath);
//      Bind bind = new Bind(volumeName, volume, AccessMode.rw);

//      Bind bind = new Bind(workingDirectory, volume);
//      log.info("Creating container with command: {}, volume {}, containerPath {}", command, volume, containerPath);

      try {
        log.info("Copying files from host dir to Docker volume using init container");
        dockerClient.pullImageCmd("busybox:latest").start().awaitCompletion();
        String initContainerId = dockerClient.createContainerCmd("busybox")
            .withCmd("sh", "-c", "cp -a /source/. /target/")
            .withBinds(
                new Bind(workingDirectory, new Volume("/source"), AccessMode.ro),
                new Bind(volumeName, new Volume("/target"), AccessMode.rw)
            )
            .exec()
            .getId();

        dockerClient.startContainerCmd(initContainerId).exec();
        dockerClient.waitContainerCmd(initContainerId).start().awaitStatusCode();
        dockerClient.removeContainerCmd(initContainerId).withForce(true).exec();

        log.info("Copied files to volume: {}", volumeName);
      } catch (Exception copyEx) {
        throw new DockerExecutionException("Failed to copy files into volume: " + volumeName, copyEx);
      }

      Volume volume = new Volume(containerPath);
      Bind bind = new Bind(volumeName, volume, AccessMode.rw);

      log.info("Creating job container with command: {}", command);

      var container = dockerClient.createContainerCmd(dockerImage)
          .withCmd("sh", "-c", command)
          .withBinds(bind)
          .withWorkingDir(containerPath)
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

      // need ?
      //dockerClient.removeContainerCmd(containerID).withForce(true).exec();

      return exitCode == 0 ? ExecutionStatus.SUCCESS : ExecutionStatus.FAILED;
    } catch (Exception e) {
      log.error("Docker execution failed: {}", e.getMessage(), e);
      throw new DockerExecutionException("Docker execution failed: " + e.getMessage(), e);
    } finally {
      if (containerID != null) {
        try {
          if(debugging) {
            log.info("Debug mode, skip cleaning up container with ID: {}", containerID);
          }
          else {
            dockerClient.removeContainerCmd(containerID).withForce(true).exec();
          }
          log.info("Cleaned up container {}", containerID);
        } catch (Exception cleanupEx) {
          log.error("Failed to cleanup container {}", containerID, cleanupEx);
        }
      }
    }
  }
}
