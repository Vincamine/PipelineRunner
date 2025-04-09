package edu.neu.cs6510.sp25.t1.cli.service;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.Yaml;
import io.kubernetes.client.openapi.models.*;
import io.kubernetes.client.openapi.apis.CoreV1Api;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

public class K8sService {

  private static final String NAMESPACE = "default";

  public static void startCicdEnvironment() {
    try {
      ApiClient client = Config.defaultClient();
      Configuration.setDefaultApiClient(client);
      CoreV1Api api = new CoreV1Api();

      applyYaml("k8s/cicd-pod.yaml", api);
      waitForPod("cicd-pod", api);
    } catch (Exception e) {
      throw new RuntimeException("Failed to start CI/CD environment", e);
    }
  }

  private static void applyYaml(String filePath, CoreV1Api api) throws Exception {
    try (InputStream is = new FileInputStream(filePath)) {
      Object obj = Yaml.load(is.toString());

      if (obj instanceof V1Pod pod) {
        api.createNamespacedPod(NAMESPACE, pod, null, null, null, null);
        System.out.println("✅ Applied Pod: " + pod.getMetadata().getName());
      } else if (obj instanceof V1Service svc) {
        api.createNamespacedService(NAMESPACE, svc, null, null, null, null);
        System.out.println("✅ Applied Service: " + svc.getMetadata().getName());
      }
    }
  }

  private static void waitForPod(String podName, CoreV1Api api) throws Exception {
    System.out.println("⏳ Waiting for pod to be ready: " + podName);
    int retries = 60;

    for (int i = 0; i < retries; i++) {
      V1Pod pod = api.readNamespacedPod(podName, NAMESPACE, null);
      var status = pod.getStatus().getConditions();

      if (status != null && status.stream().anyMatch(
          c -> "Ready".equals(c.getType()) && "True".equals(c.getStatus()))) {
        System.out.println("✅ Pod ready: " + podName);
        return;
      }

      Thread.sleep(1000);
    }

    throw new RuntimeException("Pod " + podName + " did not become ready in time.");
  }

  public static void portForwardBackendService() {
    try {
      System.out.println("Port forwarding backend service on port 8080...");
      ProcessBuilder pb = new ProcessBuilder("kubectl", "port-forward", "pod/cicd-pod", "8080:8080");
      pb.inheritIO();
      pb.start();
      Thread.sleep(3000);
    } catch (Exception e) {
      throw new RuntimeException("Failed to port-forward backend service", e);
    }
  }
}
