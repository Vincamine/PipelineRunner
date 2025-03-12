package edu.neu.cs6510.sp25.t1.worker.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Getter
@Component
@ConfigurationProperties(prefix = "docker")
public class DockerProperties {
  private String imagePullPolicy;
  private String volumeMounts;

  public void setImagePullPolicy(String imagePullPolicy) {
    this.imagePullPolicy = imagePullPolicy;
  }

  public void setVolumeMounts(String volumeMounts) {
    this.volumeMounts = volumeMounts;
  }
}
