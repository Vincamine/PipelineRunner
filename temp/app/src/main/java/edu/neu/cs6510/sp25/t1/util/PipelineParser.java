package edu.neu.cs6510.sp25.t1.util;

import com.github.dockerjava.api.DockerClient;
import org.yaml.snakeyaml.Yaml;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import edu.neu.cs6510.sp25.t1.execution.StageExecutor;
import edu.neu.cs6510.sp25.t1.execution.JobExecutor;

/**
 * Parses a pipeline YAML file and extracts execution details using SnakeYAML.
 */
public class PipelineParser {
  private final String pipelineName;
  private final List<StageExecutor> stages;
  private final DockerClient dockerClient;

  public PipelineParser(String yamlFilePath, DockerClient dockerClient) throws IOException {
    this.dockerClient = dockerClient;
    Yaml yaml = new Yaml();

    try (FileInputStream fis = new FileInputStream(yamlFilePath)) {
      Map<String, Object> pipelineData = yaml.load(fis);
      Map<String, Object> pipeline = (Map<String, Object>) pipelineData.get("pipeline");

      this.pipelineName = (String) pipeline.get("name");
      this.stages = parseStages((List<Map<String, Object>>) pipeline.get("stages"), pipelineData);
    }
  }

  /**
   * Parses the stages from the YAML file and creates StageExecutor objects.
   */
  private List<StageExecutor> parseStages(List<Map<String, Object>> stageList, Map<String, Object> pipelineData) {
    List<StageExecutor> stageExecutors = new ArrayList<>();

    if (stageList == null || stageList.isEmpty()) {
      throw new IllegalArgumentException("Pipeline must have at least one stage.");
    }

    for (Map<String, Object> stageMap : stageList) {
      if (!stageMap.containsKey("name")) {
        throw new IllegalArgumentException("Stage is missing a 'name' field.");
      }

      String stageName = (String) stageMap.get("name");
      List<JobExecutor> jobExecutors = parseJobs(stageName, pipelineData);
      stageExecutors.add(new StageExecutor(stageName, jobExecutors));
    }

    return stageExecutors;
  }

  /**
   * Parses the jobs for a given stage.
   */
  private List<JobExecutor> parseJobs(String stageName, Map<String, Object> pipelineData) {
    List<JobExecutor> jobs = new ArrayList<>();

    LinkedHashMap<String, Object> pipeline = (LinkedHashMap<String, Object>) pipelineData.get("pipeline");
    List<Map<String, Object>> jobList = (List<Map<String, Object>>) pipeline.get("jobs");

    if (jobList == null) {
      throw new IllegalArgumentException("Pipeline must have at least one job.");
    }

    for (Map<String, Object> job : jobList) {
      if (!job.containsKey("stage") || !job.containsKey("name") || !job.containsKey("image") || !job.containsKey("script")) {
        throw new IllegalArgumentException("Job is missing required fields: 'name', 'stage', 'image', 'script'.");
      }

      if (stageName.equals(job.get("stage"))) {
        String name = (String) job.get("name");
        String image = (String) job.get("image");
        List<String> scriptList = (List<String>) job.get("script");
        String[] script = scriptList.toArray(new String[0]);

        jobs.add(new JobExecutor(name, image, script, dockerClient));
      }
    }

    return jobs;
  }

  public String getPipelineName() {
    return pipelineName;
  }

  public List<StageExecutor> getStages() {
    return stages;
  }
}
