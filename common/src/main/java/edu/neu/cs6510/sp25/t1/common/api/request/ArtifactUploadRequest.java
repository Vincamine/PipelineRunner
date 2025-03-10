package edu.neu.cs6510.sp25.t1.common.api.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

/**
 * Represents an artifact upload request from the worker to the backend.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ArtifactUploadRequest {
  private UUID jobExecutionId;
  private List<String> artifactPaths;
}
