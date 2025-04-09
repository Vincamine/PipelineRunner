package edu.neu.cs6510.sp25.t1.backend.info;

import lombok.Getter;

import java.util.UUID;

@Getter
public class ClonedPipelineInfo {
  private final String yamlPath;
  private final UUID uuid;

  public ClonedPipelineInfo(String yamlPath, UUID uuid) {
    this.yamlPath = yamlPath;
    this.uuid = uuid;
  }

}
