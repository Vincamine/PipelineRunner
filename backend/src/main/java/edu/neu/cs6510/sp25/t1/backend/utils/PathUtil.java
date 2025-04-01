package edu.neu.cs6510.sp25.t1.backend.utils;


import java.nio.file.Path;
import java.nio.file.Paths;

public class PathUtil {
  /**
   * Extracts the directory root (the parent of .pipelines) as a String.
   *
   * @param pipelinePath The resolved absolute path to the pipeline file
   * @return The root directory path as a String
   * @throws IllegalArgumentException if the path doesn't include ".pipelines"
   */
  public static String extractPipelineRootDirectoryAsString(Path pipelinePath) {
    if (pipelinePath == null || !pipelinePath.isAbsolute()) {
      throw new IllegalArgumentException("Provided path must be non-null and absolute");
    }

    Path parent = pipelinePath.getParent();
    while (parent != null) {
      if (parent.getFileName().toString().equals(".pipelines")) {
        Path root = parent.getParent();
        if (root != null) {
          return root.toString();
        } else {
          throw new IllegalArgumentException("No parent directory found before .pipelines");
        }
      }
      parent = parent.getParent();
    }

    throw new IllegalArgumentException("The path does not contain a .pipelines directory: " + pipelinePath);
  }

  // Optional: accept raw String input
  public static String extractPipelineRootDirectoryAsString(String filePath) {
    return extractPipelineRootDirectoryAsString(
        Paths.get(filePath).toAbsolutePath().normalize()
    );
  }
}
