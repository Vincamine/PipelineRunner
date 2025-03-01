package edu.neu.cs6510.sp25.t1.common.validation;
// package edu.neu.cs6510.sp25.t1.validation;

// import java.nio.file.Files;
// import java.nio.file.Path;
// import java.nio.file.Paths;

// /**
//  * Utility class for validating pipeline YAML files.
//  * Ensures the file exists, is in the correct `.pipelines/` directory,
//  * and follows the correct structure.
//  */
// public class PipelineValidator {
//     private final YamlPipelineValidator yamlPipelineValidator;
//     private static final String PIPELINE_DIRECTORY = ".pipelines";

//     /**
//      * Constructs a new PipelineValidator with a YAML validator.
//      *
//      * @param yamlPipelineValidator The validator instance used for checking YAML
//      *                              structure.
//      */
//     public PipelineValidator(YamlPipelineValidator yamlPipelineValidator) {
//         this.yamlPipelineValidator = yamlPipelineValidator;
//     }

//     /**
//      * Validates the existence and correctness of a pipeline YAML file.
//      * The file is checked for existence, if it resides in the correct directory,
//      * and if it adheres to the expected pipeline structure.
//      *
//      * @param yamlFilePath The path to the YAML file.
//      * @return {@code true} if the file is valid, {@code false} otherwise.
//      */
//     public boolean validatePipelineFile(String yamlFilePath) {
//         try {
//             final Path yamlPath = Paths.get(yamlFilePath).toAbsolutePath().normalize();

//             // Check if the file exists
//             if (!Files.exists(yamlPath)) {
//                 System.err.println("YAML file not found: " + yamlFilePath);
//                 return false;
//             }

//             // Check if the file is inside the correct '.pipelines/' directory
//             final Path parentDir = yamlPath.getParent();
//             if (parentDir == null || !Files.isDirectory(parentDir)
//                     || !PIPELINE_DIRECTORY.equals(parentDir.getFileName().toString())) {
//                 System.err.println("YAML file must be inside the '.pipelines/' folder");
//                 return false;
//             }

//             // Validate the YAML file structure
//             final boolean isValid = yamlPipelineValidator.validatePipeline(yamlPath.toString());

// //            if (!isValid) {
// //                System.err.println("Pipeline validation failed.");
// //            }

//             return isValid;

//         } catch (Exception e) {
//             System.err.println("Pipeline validation error: " + e.getMessage());
//             return false;
//         }
//     }
// }
