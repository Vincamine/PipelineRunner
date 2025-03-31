package edu.neu.cs6510.sp25.t1.worker.execution;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Volume;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.okhttp.OkHttpDockerCmdExecFactory;
import com.github.dockerjava.api.model.Bind;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class DockerExecutorTest {
    // Test for DockerExecutor class
    private DockerClient dockerClient;;

    private DockerClient createDockerClient() {
        DefaultDockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder().build();

        return DockerClientBuilder.getInstance(config)
                .withDockerCmdExecFactory(new OkHttpDockerCmdExecFactory())
                .build();
    }

    @BeforeEach
    void setUp() {
        // Initialize DockerExecutor
        // DockerExecutor dockerExecutor = new DockerExecutor();
        dockerClient = createDockerClient();

    }

    @Test
    void testDockerClient() {
        // Test Docker client connection
        try {
            dockerClient.pingCmd().exec();
            System.out.println("Docker client is connected.");
        } catch (Exception e) {
            System.err.println("Failed to connect to Docker client: " + e.getMessage());
        }
    }

//    void testListImagesCmd() {
//        // Test listing images
//        try {
//            ArrayList<String> imageIds_Client = new ArrayList<>();
//            // List images using Docker Java API
//            dockerClient.listImagesCmd().exec().forEach(image -> {
//                System.out.println("Image ID: " + image.getId().replaceAll("sha256:", ""));
//                System.out.println("Image Repo Tags: " + String.join(", ", image.getRepoTags()));
//                imageIds_Client.add(image.getId().replaceAll("sha256:", "").substring(0, 12));
//
//            });
//
//            // Use terminal command to list and parse images, so that comparison can be made
//            ProcessBuilder processBuilder = new ProcessBuilder("docker", "images");
//            processBuilder.redirectErrorStream(true);
//            Process process = processBuilder.start();
//
//            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
//            String line;
//            // Parse the output of the command to put the image ids into an array
//            // Parse the output of the command to put the image tags into an array
//            ArrayList<String> imageIds = new ArrayList<>();
//            reader.readLine(); // Skip the header line
//            while ((line = reader.readLine()) != null) {
//                String[] parts = line.split("\\s+");
//                if (parts.length > 1) {
//                    imageIds.add(parts[2]);
//                }
//                System.out.println(line);
//            }
//
//            assert process.waitFor() == 0 : "Failed to list images using terminal command";
//            for (int i = 0; i < imageIds_Client.size(); i++) {
//                // assert imageIds_Client.get(i).equals(imageIds.get(i)) : "Image ID mismatch at
//                // index " + i+
//                // " Client: " + imageIds_Client.get(i) + ", Terminal: " + imageIds.get(i);
//                // assert imageTags_Client.get(i).equals(imageTags.get(i)) : "Image Tag mismatch
//                // at index " + i +
//                // " Client: " + imageTags_Client.get(i) + ", Terminal: " + imageTags.get(i);
//                assert imageIds.contains(imageIds_Client.get(i)) : "Image ID not found at index " + i +
//                        " Client: " + imageIds_Client.get(i) + ", Terminal: " + imageIds.toString();
//
//                System.out.println("Image ID: " + imageIds_Client.get(i));
//            }
//            assert imageIds_Client.size() == imageIds.size() : "Image IDs size mismatch";
//            System.out.println("Images listed successfully.");
//
//        } catch (Exception e) {
//            System.err.println("Failed to list images: " + e.getMessage());
//        }
//    }

    @Test
    void testListContainersCmd() {
        // Test listing containers

        try {
            ArrayList<String> containerIds_Client = new ArrayList<>();
            // List containers using Docker Java API
            dockerClient.listContainersCmd().withShowAll(true).exec().forEach(container -> {
                System.out.println("Container ID: " + container.getId().substring(0, 12));
                System.out.println("Container Names: " + String.join(", ", container.getNames()));
                containerIds_Client.add(container.getId().substring(0, 12));
            });

            // Use terminal command to list and parse containers, so that comparison can be
            // made
            ProcessBuilder processBuilder = new ProcessBuilder("docker", "ps", "-a");
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            // Parse the output of the command to put the container ids into an array
            ArrayList<String> containerIds = new ArrayList<>();
            reader.readLine(); // Skip the header line
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\s+");
                if (parts.length > 1) {
                    containerIds.add(parts[0]);
                }
                System.out.println(line);
            }

            assert process.waitFor() == 0 : "Failed to list containers using terminal command";
            for (int i = 0; i < containerIds_Client.size(); i++) {
                assert containerIds.contains(containerIds_Client.get(i)) : "Container ID not found at index " + i +
                        " Client: " + containerIds_Client.get(i) + ", Terminal: " + containerIds.toString();
                System.out.println("Container ID: " + containerIds_Client.get(i));
            }
            assert containerIds_Client.size() == containerIds.size() : "Container IDs size mismatch";
            System.out.println("Containers listed successfully.");

        } catch (Exception e) {
            System.err.println("Failed to list containers: " + e.getMessage());
        }
    }

    @Test
    void testCreateContainerCmd() {
        // Test creating a container
        try {
            // creating container with command: ./gradlew build, volumn /app/c03a708f-4622-493e-828a-5d894e4207d1, containerPath /app/c03a708f-4622-493e-828a-5d894e4207d1, workdir /Users/wsq/Nextcloud/CS6510/project/t1-cicd
            // eating container with command: ./gradlew javadoc, volumn /app/96b5cafe-472a-4e20-93ff-aa7cdf35a951, containerPath /app/96b5cafe-472a-4e20-93ff-aa7cdf35a951, workdir /Users/wsq/Nextcloud/CS6510/project/t1-cicd
            String imageName = "openjdk:21-jdk-slim";
            String containerName = "test-container";
            String command = "./gradlew build";
            String workingDirectory = "/Users/wsq/Nextcloud/CS6510/project/t1-cicd";
            String containerPath = "/app/5b4696e4-c389-4101-a137-f5e633ece264";
            Volume volume = new Volume(containerPath);
            Bind bind = new Bind(workingDirectory, volume);

            var newcontainer = dockerClient.createContainerCmd(imageName).withCmd("sh", "-c", command).withBinds(bind)
                    .withWorkingDir(workingDirectory)
                    .exec();
            // Verify the new container using ListContainersCmd
            // boolean containerFound =
            // dockerClient.listContainersCmd().withShowAll(true).exec().stream()
            // .anyMatch(container -> container.getId().equals("/" + containerName));
            boolean containerFound = dockerClient.listContainersCmd().withShowAll(true).exec().stream()
                    .anyMatch(container -> container.getId().equals(newcontainer.getId()));

            assert containerFound : "Container not found: " + containerName;

            System.out.println("Container created successfully.");
        } catch (Exception e) {
            System.err.println("Failed to create container: " + e.getMessage());
        }
    }

    @Test
    void testStartContainerCmd() {
        // Test starting a container
        try {
            String containerId = "151d374746fa1cc05316f497631bb1510b868f04cc1da6ff892348eba764ecef"; // Replace with your container ID
            dockerClient.startContainerCmd(containerId).exec();
            System.out.println("Container started successfully.");
        } catch (Exception e) {
            System.err.println("Failed to start container: " + e.getMessage());
        }
    }
}
