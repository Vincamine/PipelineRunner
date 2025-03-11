package edu.neu.cs6510.sp25.t1.common.validation.manager;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

/**
 * PipelineNameManager ensures that pipeline YAML file names are unique inside `.pipelines/`.
 * It provides methods to validate names and suggest alternative names if conflicts exist.
 */
public class PipelineNameManager {
  private static final Logger LOGGER = Logger.getLogger(PipelineNameManager.class.getName());
  private final Path pipelinesDir = Paths.get(System.getProperty("user.dir"), ".pipelines");
  private final Set<String> existingFileNames;

  /**
   * Initializes the PipelineNameManager and loads existing YAML file names.
   */
  public PipelineNameManager() {
    this.existingFileNames = new HashSet<>();
    loadExistingYamlFileNames();
  }

  /**
   * Loads existing YAML file names from the `.pipelines/` directory.
   */
  private void loadExistingYamlFileNames() {
    File directory = pipelinesDir.toFile();
    if (!directory.exists() || !directory.isDirectory()) {
      LOGGER.warning("Directory not found: " + pipelinesDir);
      return;
    }

    File[] files = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".yaml") || name.toLowerCase().endsWith(".yml"));
    if (files != null) {
      for (File file : files) {
        existingFileNames.add(file.getName().toLowerCase());  // Store names as lowercase for case-insensitive checks
      }
    }
  }

  /**
   * Checks if a given pipeline name is unique inside `.pipelines/`.
   *
   * @param fileName The YAML file name to check.
   * @return {@code true} if the name is unique, {@code false} otherwise.
   */
  public boolean isPipelineNameUnique(String fileName) {
    return !existingFileNames.contains(fileName.toLowerCase());
  }

  /**
   * Suggests a unique YAML file name if a duplicate exists.
   *
   * @param baseFileName The base name of the YAML file.
   * @return A unique file name suggestion.
   */
  public String suggestUniquePipelineName(String baseFileName) {
    String fileName = baseFileName.replaceAll("(\\.ya?ml)$", "");  // Remove file extension
    String extension = baseFileName.endsWith(".yml") ? ".yml" : ".yaml";

    int counter = 1;
    String suggestedName = fileName + extension;

    while (existingFileNames.contains(suggestedName.toLowerCase())) {
      suggestedName = fileName + "_" + counter + extension;
      counter++;
    }

    return suggestedName;
  }
}
