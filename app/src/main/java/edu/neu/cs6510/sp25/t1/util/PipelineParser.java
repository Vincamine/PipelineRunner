package edu.neu.cs6510.sp25.t1.util;

import com.github.dockerjava.api.DockerClient;
import org.yaml.snakeyaml.Yaml;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
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
      this.pipelineName = (String) ((Map<String, Object>) pipelineData.get("pipeline")).get("name");
      this.stages = parseStages((List<String>) ((Map<String, Object>) pipelineData.get("pipeline")).get("stages"), pipelineData);
    }
  }

  private List<StageExecutor> parseStages(List<String> stageNames, Map<String, Object> pipelineData) {
    List<StageExecutor> stageExecutors = new ArrayList<>();
    for (String stage : stageNames) {
      List<JobExecutor> jobExecutors = parseJobs(stage, pipelineData);
      stageExecutors.add(new StageExecutor(stage, jobExecutors));
    }
    return stageExecutors;
  }

  private List<JobExecutor> parseJobs(String stage, Map<String, Object> pipelineData) {
    List<JobExecutor> jobs = new ArrayList<>();
    List<Map<String, Object>> jobList = (List<Map<String, Object>>) pipelineData.get("jobs");
    for (Map<String, Object> job : jobList) {
      if (stage.equals(job.get("stage"))) {
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
