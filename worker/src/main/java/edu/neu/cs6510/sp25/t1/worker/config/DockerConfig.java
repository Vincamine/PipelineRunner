package edu.neu.cs6510.sp25.t1.worker.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "docker")
public class DockerConfig {
  private String imagePullPolicy;
  private String volumeMounts;

  // Getters and Setters
  public String getImagePullPolicy() {
    return imagePullPolicy;
  }

  public void setImagePullPolicy(String imagePullPolicy) {
    this.imagePullPolicy = imagePullPolicy;
  }

  public String getVolumeMounts() {
    return volumeMounts;
  }

  public void setVolumeMounts(String volumeMounts) {
    this.volumeMounts = volumeMounts;
  }
}
