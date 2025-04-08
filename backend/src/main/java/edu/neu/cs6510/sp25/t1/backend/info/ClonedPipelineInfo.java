package edu.neu.cs6510.sp25.t1.backend.info;

import java.util.UUID;

public class ClonedPipelineInfo {
  private final String yamlPath;
  private final UUID uuid;

  public ClonedPipelineInfo(String yamlPath, UUID uuid) {
    this.yamlPath = yamlPath;
    this.uuid = uuid;
  }

  public String getYamlPath() {
    return yamlPath;
  }

  public UUID getUuid() {
    return uuid;
  }
}
