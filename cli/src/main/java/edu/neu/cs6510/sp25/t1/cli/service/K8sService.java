package edu.neu.cs6510.sp25.t1.cli.service;

import edu.neu.cs6510.sp25.t1.common.logging.PipelineLogger;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.Yaml;
import io.kubernetes.client.openapi.models.*;
import io.kubernetes.client.openapi.apis.CoreV1Api;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;
import java.util.UUID;

public class K8sService {

  private static final String NAMESPACE = "default";
  private static Process portForwardProcess;

  public static void startCicdEnvironment(String pipelineName) {
    try {
      ApiClient client = Config.defaultClient();
      Configuration.setDefaultApiClient(client);
      CoreV1Api api = new CoreV1Api();

      applyYaml("k8s/cicd-pod.yaml", api, pipelineName);
      waitForPod("cicd-pod", api);
    } catch (Exception e) {
      throw new RuntimeException("Failed to start CI/CD environment", e);
    }
  }

  private static void applyYaml(String filePath, CoreV1Api api, String pipelineName) throws Exception {
    try (InputStream is = new FileInputStream(filePath)) {
      Object obj = Yaml.load(new java.io.InputStreamReader(is));
      String podName = "cicd-pod-" + pipelineName;

      if (obj instanceof V1Pod pod) {
        pod.getMetadata().setName(podName);
        api.createNamespacedPod(NAMESPACE, pod, null, null, null, null);
        System.out.println("✅ Applied Pod: " + pod.getMetadata().getName());
      } else if (obj instanceof V1Service svc) {
        api.createNamespacedService(NAMESPACE, svc, null, null, null, null);
        System.out.println("✅ Applied Service: " + svc.getMetadata().getName());
      }
    }
  }

  private static void waitForPod(String podName, CoreV1Api api) throws Exception {
    System.out.println("Waiting for pod to be ready: " + podName);
    int retries = 60;

    for (int i = 0; i < retries; i++) {
      V1Pod pod = api.readNamespacedPod(podName, NAMESPACE, null);
      var status = pod.getStatus().getConditions();

      if (status != null && status.stream().anyMatch(
          c -> "Ready".equals(c.getType()) && "True".equals(c.getStatus()))) {
        System.out.println("Pod ready: " + podName);
        return;
      }

      Thread.sleep(1000);
    }

    throw new RuntimeException("Pod " + podName + " did not become ready in time.");
  }

  public static void portForwardBackendService() {
//    waitForBackendStartupInsidePod();

    try {
      System.out.println("Sleeping 50 seconds before port-forwarding to allow backend to fully start...");
      Thread.sleep(50000); // Sleep 50 seconds before port-forward

      System.out.println("Port forwarding backend service on port 8080...");
      ProcessBuilder pb = new ProcessBuilder("kubectl", "port-forward", "pod/cicd-pod", "8080:8080");
      pb.inheritIO();
      portForwardProcess = pb.start();
      Thread.sleep(3000); // Give it time to establish
    } catch (Exception e) {
      throw new RuntimeException("Failed to port-forward backend service", e);
    }
  }

  public static void stopPortForward() {
    if (portForwardProcess != null && portForwardProcess.isAlive()) {
      System.out.println("🔌 Closing port-forward on 8080...");
      portForwardProcess.destroy();
    }
  }

  public static void waitForBackendStartupInsidePod() {
    String internalUrl = "http://localhost:8080/health";
    int maxRetries = 100;
    int delayMillis = 1000;

    PipelineLogger.info("Checking if backend is responding on port 8080...");

    for (int i = 0; i < maxRetries; i++) {
      try {
        HttpURLConnection connection = (HttpURLConnection) new URL(internalUrl).openConnection();
        connection.setConnectTimeout(1000);
        connection.setReadTimeout(1000);
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode == 200) {
          PipelineLogger.info("Backend is UP and responding.");
          return;
        }
      } catch (IOException ignored) {}

      if (i % 10 == 0) {
        PipelineLogger.info("Waiting for backend to become ready... (" + (i + 1) + ")");
      }
      try {
        Thread.sleep(delayMillis);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        return;
      }
    }

    throw new RuntimeException("Backend failed to become ready after timeout.");
  }

}
