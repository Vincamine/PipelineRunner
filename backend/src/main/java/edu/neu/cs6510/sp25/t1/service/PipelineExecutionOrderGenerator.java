// package edu.neu.cs6510.sp25.t1.service;

// import org.yaml.snakeyaml.Yaml;

// import java.io.FileInputStream;
// import java.io.IOException;
// import java.util.Map;
// import java.util.LinkedHashMap;
// import java.util.List;


// /**
//  * This class generates the execution order of a CI/CD pipeline based on a YAML
//  * configuration.
//  * - It extracts pipeline stages and jobs, ensuring jobs are correctly mapped to
//  * their respective stages.
//  * - It also maintains dependencies (`needs`) to process job execution order
//  * properly.
//  */
// public class PipelineExecutionOrderGenerator {

//     /**
//      * Generates the execution order of jobs based on the pipeline YAML
//      * configuration.
//      *
//      * @param yamlFilePath Path to the YAML configuration file.
//      * @return A map where each stage is mapped to its jobs and dependencies.
//      * @throws IOException If there's an error reading the file.
//      */
//     public Map<String, Map<String, Object>> generateExecutionOrder(String yamlFilePath) throws IOException {
//         final Yaml yaml = new Yaml();
//         final Map<String, Object> pipelineConfig;

//         try (FileInputStream inputStream = new FileInputStream(yamlFilePath)) {
//             pipelineConfig = yaml.load(inputStream);
//         }

//         if (pipelineConfig == null || !pipelineConfig.containsKey("pipeline")) {
//             throw new IllegalArgumentException("Invalid YAML structure: 'pipeline' key is missing.");
//         }

//         @SuppressWarnings("unchecked")
//         final Map<String, Object> pipelineMetadata = (Map<String, Object>) pipelineConfig.get("pipeline");
//         @SuppressWarnings("unchecked")
//         final List<String> stages = (List<String>) pipelineMetadata.get("stages");

//         if (stages == null || stages.isEmpty()) {
//             throw new IllegalArgumentException("Invalid YAML structure: 'stages' list is empty.");
//         }

//         if (!pipelineConfig.containsKey("jobs")) {
//             throw new IllegalArgumentException("Invalid YAML structure: 'jobs' key is missing.");
//         }

//         final Object jobObj = pipelineConfig.get("jobs");
//         if (!(jobObj instanceof List<?> jobsList)) {
//             throw new IllegalArgumentException("Invalid YAML structure: 'jobs' should be a list.");
//         }

//         // Process jobs and generate execution order
//         Map<String, Map<String, Object>> executionOrder = processJobs(jobsList, stages);

//         // Debugging Output
//         System.out.println("Extracted Execution Order:");
//         for (Map.Entry<String, Map<String, Object>> entry : executionOrder.entrySet()) {
//             System.out.println("Stage: " + entry.getKey());
//             System.out.println("  Jobs: " + entry.getValue());
//         }

//         return executionOrder;
//     }

//     /**
//      * Processes the jobs from the YAML configuration and maps them to their
//      * respective stages.
//      * It ensures jobs have valid stages and correctly maps their dependencies
//      * (`needs`).
//      *
//      * @param jobsList The list of job definitions from the YAML file.
//      * @param stages   The ordered list of stages in the pipeline.
//      * @return A map where each stage is mapped to its jobs and dependencies.
//      */
//     private Map<String, Map<String, Object>> processJobs(List<?> jobsList, List<String> stages) {
//         Map<String, Map<String, Object>> executionOrder = new LinkedHashMap<>();

//         for (String stage : stages) {
//             executionOrder.put(stage, new LinkedHashMap<>());
//         }

//         for (Object jobObj : jobsList) {
//             if (!(jobObj instanceof Map<?, ?> job)) {
//                 continue;
//             }

//             final String jobName = (String) job.get("name");
//             final String stage = (String) job.get("stage");

//             if (!executionOrder.containsKey(stage)) {
//                 throw new IllegalArgumentException("Invalid job stage: " + stage);
//             }

//             final List<String> needs = job.containsKey("needs") && job.get("needs") instanceof List<?>
//                     ? ((List<?>) job.get("needs")).stream()
//                             .filter(String.class::isInstance)
//                             .map(String.class::cast)
//                             .toList()
//                     : List.of();

//             executionOrder.get(stage).put(jobName, Map.of("needs", needs));
//             System.out.println("Job Mapped: " + jobName + " → Stage: " + stage + ", Dependencies: " + needs);
//         }

//         return executionOrder;
//     }

// }
