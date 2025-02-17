package edu.neu.cs6510.sp25.t1.validation;

import org.yaml.snakeyaml.error.Mark;

import java.util.Map;

/**
 * Container class for YAML loading results including data and location information.
 */
class YamlLoadResult {

  private final Map<String, Object> data;
  private final Map<String, Mark> locations;

  public YamlLoadResult(Map<String, Object> data, Map<String, Mark> locations) {
    this.data = data;
    this.locations = locations;
  }

  public Map<String, Object> getData() {
    return data;
  }

  public Map<String, Mark> getLocations() {
    return locations;
  }
}
