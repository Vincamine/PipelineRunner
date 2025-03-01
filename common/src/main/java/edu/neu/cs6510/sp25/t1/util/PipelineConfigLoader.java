// package edu.neu.cs6510.sp25.t1.util;


// import edu.neu.cs6510.sp25.t1.validation.YamlLoader;
// import edu.neu.cs6510.sp25.t1.validation.YamlLoadResult;
// import edu.neu.cs6510.sp25.t1.validation.YamlPipelineValidator;

// import java.io.IOException;
// import java.util.Map;

// /**
//  * Utility class for loading and parsing pipeline configuration files.
//  * This class ensures the pipeline YAML is correctly loaded, validated,
//  * and structured before execution.
//  */
// public class PipelineConfigLoader {

//     private final YamlPipelineValidator pipelineValidator;

//     /**
//      * Default constructor using the standard pipeline validator.
//      */
//     public PipelineConfigLoader() {
//         this.pipelineValidator = new YamlPipelineValidator();
//     }

//     /**
//      * Loads a pipeline configuration file and validates its structure.
//      *
//      * @param filePath The path to the pipeline YAML file.
//      * @return The parsed pipeline configuration as a Map.
//      * @throws IOException If the file cannot be read.
//      * @throws IllegalArgumentException If the file is invalid.
//      */
//     public Map<String, Object> loadPipelineConfig(String filePath) throws IOException {
//         // Validate the pipeline structure
//         if (!pipelineValidator.validatePipeline(filePath)) {
//             throw new IllegalArgumentException("Pipeline validation failed for: " + filePath);
//         }

//         // Load the YAML file
//         YamlLoadResult loadResult = YamlLoader.loadYamlWithLocations(filePath);
//         Map<String, Object> pipelineConfig = loadResult.getData();

//         // Extract and validate pipeline root
//         if (!pipelineConfig.containsKey("pipeline")) {
//             throw new IllegalArgumentException("Invalid pipeline configuration: Missing 'pipeline' key.");
//         }

//         return pipelineConfig;
//     }
// }
