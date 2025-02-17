package edu.neu.cs6510.sp25.t1.service;

import org.yaml.snakeyaml.Yaml;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

/**
 * Service class responsible for parsing a pipeline YAML file and determining execution order.
 * <p>
 * This class reads a YAML configuration file defining pipeline stages and jobs,
 * then generates an execution order while respecting dependencies.
 * <br><br>
 * 🚀 **Mock Implementation** for demo purposes.
 * - This service **simulates** parsing pipeline execution order.
 * - Once the backend API is available, **modify it to integrate with actual execution logic**.
 * </p>
 *
 * <h2>Expected YAML Format:</h2>
 * <pre>
 * pipeline:
 *   name: my_pipeline
 *   stages:
 *     - build
 *     - test
 *     - deploy
 * jobs:
 *   - name: compile
 *     stage: build
 *     needs: []
 *   - name: test
 *     stage: test
 *     needs: [compile]
 *   - name: deploy
 *     stage: deploy
 *     needs: [test]
 * </pre>
 *
 * @see edu.neu.cs6510.sp25.t1.validation.YamlPipelineValidator
 */
public class PipelineExecutionOrderGenerator {

    /**
     * Parses a pipeline YAML file and determines the execution order of jobs and stages.
     *
     * @param yamlFilePath The path to the pipeline YAML file.
     * @return A {@link LinkedHashMap} representing the execution order.
     * @throws IOException If the file cannot be read or is incorrectly formatted.
     */
    public Map<String, Map<String, Object>> generateExecutionOrder(String yamlFilePath) throws IOException {
        final Yaml yaml = new Yaml();
        final Map<String, Object> pipelineConfig;

        try (FileInputStream inputStream = new FileInputStream(yamlFilePath)) {
            pipelineConfig = yaml.load(inputStream);
        }

        if (pipelineConfig == null || !pipelineConfig.containsKey("pipeline")) {
            throw new IllegalArgumentException("Invalid YAML structure: 'pipeline' key is missing.");
        }

        final Map<String, Object> pipelineMetadata = (Map<String, Object>) pipelineConfig.get("pipeline");
        final List<String> stages = (List<String>) pipelineMetadata.get("stages");

        if (stages == null || stages.isEmpty()) {
            throw new IllegalArgumentException("Invalid YAML structure: 'stages' list is empty.");
        }

        if (!pipelineConfig.containsKey("jobs")) {
            throw new IllegalArgumentException("Invalid YAML structure: 'jobs' key is missing.");
        }

        final Object jobObj = pipelineConfig.get("jobs");
        if (!(jobObj instanceof List<?> jobsList)) {
            throw new IllegalArgumentException("Invalid YAML structure: 'jobs' should be a list.");
        }

        return processJobs(jobsList, stages);
    }

    /**
     * Processes jobs from the pipeline configuration and determines the execution order.
     *
     * @param jobsList The list of job configurations.
     * @param stages The list of defined pipeline stages.
     * @return A LinkedHashMap maintaining the correct execution order.
     */
    private Map<String, Map<String, Object>> processJobs(List<?> jobsList, List<String> stages) {
        final Map<String, Map<String, Object>> executionOrder = new LinkedHashMap<>();

        // Initialize stages in execution order
        for (String stage : stages) {
            executionOrder.put(stage, new LinkedHashMap<>());
        }

        for (Object jobObj : jobsList) {
            if (!(jobObj instanceof Map<?, ?> job)) continue;

            final String jobName = (String) job.get("name");
            final String stage = (String) job.get("stage");

            if (!executionOrder.containsKey(stage)) {
                throw new IllegalArgumentException("Invalid job stage: " + stage);
            }

            executionOrder.get(stage).put(jobName, new LinkedHashMap<>());
        }

        return executionOrder;
    }
}
