package edu.neu.cs6510.sp25.t1.cli.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.UUID;

public class K8sService {

  public static String startCicdEnvironment(String filePathToMount) {
    String[] baseMountOut = new String[1]; // for getting mount base path
    String minikubeFullPath = mountHostDirToMinikube(filePathToMount, baseMountOut);

    generatePvYaml(baseMountOut[0]); // use /mnt/pipeline/UUID

    applyK8sConfig("k8s/pv-cicd.yaml");
    applyK8sConfig("k8s/cicd-pvc.yaml");
    applyK8sConfig("k8s/cicd-pod.yaml");
    applyK8sConfig("k8s/backend-service.yaml");

    waitForPodsReady("app=backend");
    waitForPodsReady("app=worker");
    waitForPodsReady("app=rabbitmq");

    return minikubeFullPath; // full pipeline path
  }



  private static String mountHostDirToMinikube(String hostPath, String[] baseMountPathOut) {
    try {
      File inputFile = new File(hostPath).getCanonicalFile();
      File hostProjectDir = inputFile.getParentFile().getParentFile(); // demo-project dir

      if (!hostProjectDir.exists() || !hostProjectDir.isDirectory()) {
        throw new IllegalArgumentException("Invalid host directory: " + hostProjectDir.getAbsolutePath());
      }

      String uuid = UUID.randomUUID().toString();
      String minikubeMountBase = "/mnt/pipeline/" + uuid;

      // Set output param
      baseMountPathOut[0] = minikubeMountBase;

      String relativePath = inputFile.getAbsolutePath()
          .substring(hostProjectDir.getAbsolutePath().length())
          .replace("\\", "/");
      if (relativePath.startsWith("/")) {
        relativePath = relativePath.substring(1);
      }

      String minikubeFullPath = minikubeMountBase + "/" + relativePath;

      System.out.println("Resolved host path: " + hostProjectDir.getAbsolutePath());
      System.out.println("Mounting to Minikube at: " + minikubeMountBase);

      String safeHostPath = hostProjectDir.getAbsolutePath().replace("\\", "/");
      String command = String.format("minikube mount \"%s:%s\"", safeHostPath, minikubeMountBase);

      ProcessBuilder pb = new ProcessBuilder("cmd", "/c", command);
      pb.inheritIO();
      pb.start();

      Thread.sleep(3000); // Let it settle
      return minikubeFullPath;
    } catch (IOException | InterruptedException e) {
      throw new RuntimeException("Failed to mount host directory to Minikube: " + hostPath, e);
    }
  }

  private static void generatePvYaml(String hostPathInMinikube) {
    String yaml = String.format("""
      apiVersion: v1
      kind: PersistentVolume
      metadata:
        name: cicd
      spec:
        capacity:
          storage: 1Gi
        accessModes:
          - ReadWriteMany
        hostPath:
          path: "%s"
      """, hostPathInMinikube);

    try {
      File pvFile = new File("k8s/pv-cicd.yaml");
      pvFile.getParentFile().mkdirs();
      java.nio.file.Files.writeString(pvFile.toPath(), yaml);
      System.out.println("✅ Generated dynamic pv-cicd.yaml at " + pvFile.getAbsolutePath());
    } catch (IOException e) {
      throw new RuntimeException("Failed to write pv-cicd.yaml", e);
    }
  }




  private static void applyK8sConfig(String yamlPath) {
    try {
      ProcessBuilder pb = new ProcessBuilder("kubectl", "apply", "-f", yamlPath);
      pb.inheritIO();
      Process process = pb.start();
      process.waitFor();
    } catch (Exception e) {
      throw new RuntimeException("Failed to apply K8s config: " + yamlPath, e);
    }
  }

  private static void waitForPodsReady(String labelSelector) {
    try {
      ProcessBuilder pb = new ProcessBuilder(
          "kubectl", "wait", "--for=condition=Ready", "pods", "-l", labelSelector, "--timeout=60s"
      );
      pb.inheritIO();
      Process process = pb.start();
      process.waitFor();
    } catch (Exception e) {
      throw new RuntimeException("Pod readiness check failed for label: " + labelSelector, e);
    }
  }

  public static void portForwardBackendService() {
    try {
      System.out.println("Port forwarding backend service on port 8080...");
      ProcessBuilder pb = new ProcessBuilder("kubectl", "port-forward", "pod/cicd-pod", "8080:8080");
      pb.inheritIO();
      pb.start();
      Thread.sleep(3000); // Give it a few seconds to forward
    } catch (Exception e) {
      throw new RuntimeException("Failed to port-forward backend service", e);
    }
  }


}
