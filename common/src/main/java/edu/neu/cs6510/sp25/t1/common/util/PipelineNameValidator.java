package edu.neu.cs6510.sp25.t1.common.util;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public class PipelineNameValidator {
  private final Path projectRoot = Paths.get(System.getProperty("user.dir")).getParent();
  private final Path pipelinesDir = projectRoot.resolve(".pipelines");
  private final Set<String> existingFileNames;

  public PipelineNameValidator() {
    this.existingFileNames = new HashSet<>();
    loadExistingYamlFileNames();
  }

  /**
   * Loads existing YAML file names from the .pipelines directory into a set.
   */
  private void loadExistingYamlFileNames() {
    File directory = pipelinesDir.toFile();
    if (!directory.exists() || !directory.isDirectory()) {
      System.err.println("Directory not found: " + pipelinesDir);
      return;
    }

    File[] files = directory.listFiles((dir, name) -> name.endsWith(".yaml") || name.endsWith(".yml"));
    if (files != null) {
      for (File file : files) {
        existingFileNames.add(file.getName());
      }
    }
  }

  /**
   * Checks if a given YAML file name is unique within the .pipelines directory.
   *
   * @param fileName The YAML file name to check.
   * @return true if the name is unique, false otherwise.
   */
  public boolean isYamlFileNameUnique(String fileName) {
    return !existingFileNames.contains(fileName);
  }

  /**
   * Suggests a unique YAML file name if a duplicate exists.
   *
   * @param baseFileName The base name of the YAML file.
   * @return A unique file name suggestion.
   */
  public String suggestUniqueYamlFileName(String baseFileName) {
    String suggestedName = baseFileName;
    int counter = 1;

    while (existingFileNames.contains(suggestedName)) {
      suggestedName = baseFileName.replaceAll("(\\.ya?ml)$", "_" + counter + "$1");
      counter++;
    }

    return suggestedName;
  }
}
