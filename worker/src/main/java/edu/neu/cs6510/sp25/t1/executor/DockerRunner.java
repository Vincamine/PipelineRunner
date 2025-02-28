package edu.neu.cs6510.sp25.t1.executor;
// package edu.neu.cs6510.sp25.t1.docker;

// import com.github.dockerjava.api.DockerClient;
// import com.github.dockerjava.api.command.CreateContainerResponse;
// import com.github.dockerjava.api.exception.DockerException;
// import com.github.dockerjava.api.model.HostConfig;
// import com.github.dockerjava.core.DockerClientBuilder;

// /**
//  * Responsible for creating and starting a Docker container for a pipeline execution.
//  * Manages Docker container execution for pipeline execution.
//  */
// public class DockerRunner {
//   private final DockerClient dockerClient;
//   private final String image;
//   private final String localDockerWindows = "tcp://localhost:2375";
//   private final String localDockerLinux = "unix:///var/run/docker.sock";

//   /**
//    * Constructor to initialize DockerRunner with the specified image.
//    * @param image The Docker image to use for the container.
//    */
//   public DockerRunner(String image) {
//     this.dockerClient = DockerClientBuilder.getInstance(localDockerWindows).build();
//     this.image = image;
//   }

//   /**
//    * Starts a new Docker container using the specified image.
//    * @param command The command to run inside the container.
//    * @return The container ID if successful.
//    */
//   public String startContainer(String... command) {
//     try {
//       System.out.println("Starting container with image: " + image);
//       CreateContainerResponse container = dockerClient.createContainerCmd(image)
//           .withHostConfig(HostConfig.newHostConfig())
//           .withCmd(command)
//           .exec();

//       String containerId = container.getId();
//       dockerClient.startContainerCmd(containerId).exec();
//       System.out.println("Container started: " + containerId);
//       return containerId;
//     } catch (DockerException e) {
//       throw new RuntimeException("Failed to start Docker container: " + e.getMessage(), e);
//     }
//   }

//   /**
//    * Getter: Returns the Docker client instance.
//    * @return The Docker client instance.
//    */
//   public DockerClient getDockerClient() {
//     return dockerClient;
//   }
// }