package edu.neu.cs6510.sp25.t1.cli.utils;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateVolumeResponse;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.okhttp.OkDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import edu.neu.cs6510.sp25.t1.common.logging.PipelineLogger;

import java.io.File;
import java.io.IOException;
import java.time.Duration;

public class DockerVolumeUtil {

  private static final String VOLUME_NAME = "cicd";
  private static final String CONTAINER_MOUNT_PATH = "/mnt/pipeline";

  public static String createVolumeFromHostDir(String hostPath) {
    try {
      File file = new File(hostPath);
      File hostProjectDir = file.getParentFile().getParentFile(); // full project dir

      if (!hostProjectDir.exists() || !hostProjectDir.isDirectory()) {
        throw new IllegalArgumentException("Invalid host directory: " + hostProjectDir.getAbsolutePath());
      }

      // Init docker client
      DefaultDockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder().build();
      DockerHttpClient httpClient = new OkDockerHttpClient.Builder()
          .dockerHost(config.getDockerHost())
          .sslConfig(config.getSSLConfig())
          .connectTimeout(30)
          .readTimeout(30)
          .build();

      DockerClient dockerClient = DockerClientImpl.getInstance(config, httpClient);

      // Create volume if it doesn't exist
      try {
        dockerClient.inspectVolumeCmd(VOLUME_NAME).exec();
        PipelineLogger.info("Volume 'cicd' already exists.");
      } catch (Exception e) {
        PipelineLogger.info("Creating Docker volume: cicd");
        CreateVolumeResponse volume = dockerClient.createVolumeCmd()
            .withName(VOLUME_NAME)
            .withDriver("local")
            .exec();
      }

      String containerName = "temp-copy-container-" + System.currentTimeMillis();
      ProcessBuilder copyBuilder = new ProcessBuilder(
          "docker", "run", "--rm",
          "--name", containerName,
          "-v", hostProjectDir.getAbsolutePath() + ":/from:ro",
          "-v", VOLUME_NAME + ":" + CONTAINER_MOUNT_PATH,
          "alpine", "sh", "-c", "cp -r /from/. " + CONTAINER_MOUNT_PATH
      );

      PipelineLogger.info("Copying project to Docker volume 'cicd'...");
      Process copyProcess = copyBuilder.start();
      int exitCode = copyProcess.waitFor();

      if (exitCode != 0) {
        PipelineLogger.error("Failed to copy files from host to Docker volume.");
        return null;
      }

      // Return the path to the .yaml file inside container
      String relativePathInProject = file.getAbsolutePath().substring(hostProjectDir.getAbsolutePath().length());
      return CONTAINER_MOUNT_PATH + relativePathInProject.replace("\\", "/");

    } catch (IOException | InterruptedException e) {
      PipelineLogger.error("Docker volume creation or file copy failed: " + e.getMessage());
      return null;
    }
  }
}
