package edu.neu.cs6510.sp25.t1.cli.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.UUID;

public class K8sService {

  public static void startCicdEnvironment() {

    applyK8sConfig("k8s/pv-cicd.yaml");
    applyK8sConfig("k8s/cicd-pvc.yaml");
    applyK8sConfig("k8s/cicd-pod.yaml");
    applyK8sConfig("k8s/backend-service.yaml");

    waitForPodsReady("app=backend");
    waitForPodsReady("app=worker");
    waitForPodsReady("app=rabbitmq");
    // full pipeline path
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
