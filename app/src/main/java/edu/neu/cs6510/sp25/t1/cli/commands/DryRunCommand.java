package edu.neu.cs6510.sp25.t1.cli.commands;

import edu.neu.cs6510.sp25.t1.service.PipelineExecutionOrderGenerator;
import edu.neu.cs6510.sp25.t1.util.PipelineValidator;
import edu.neu.cs6510.sp25.t1.validation.YamlPipelineValidator;
import org.yaml.snakeyaml.Yaml;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Command to perform a "dry run" of a pipeline YAML file.
 * <p>
 * This command does NOT execute the pipeline but:
 * <ul>
 *     <li>Validates the structure, dependencies, and job configurations.</li>
 *     <li>Determines the correct execution order of jobs.</li>
 *     <li>Prints the execution order in YAML format.</li>
 * </ul>
 * </p>
 *
 * <h2>Usage Example:</h2>
 * <pre>
 *   cicd-cli dry-run -f .pipelines/pipeline.yaml
 * </pre>
 */
@Command(
    name = "dry-run",
    description = "Perform a dry run of a pipeline YAML file, printing the execution order.",
    mixinStandardHelpOptions = true
)
public class DryRunCommand implements Callable<Boolean> {

    /**
     * Path to the pipeline YAML file.
     */
    @Option(
        names = {"-f", "--file"},
        description = "Path to the pipeline YAML file",
        required = true
    ) String yamlFilePath;

    /**
     * Executes the dry-run command to validate and print the execution order.
     *
     * @return {@code true} if validation succeeds and execution order is printed, {@code false} otherwise.
     */
    @Override
    public Boolean call() {
        System.out.println("🔍 Validating pipeline configuration: " + yamlFilePath);
        
        // Initialize the validators
        final YamlPipelineValidator yamlPipelineValidator = new YamlPipelineValidator();
        final PipelineValidator pipelineValidator = new PipelineValidator(yamlPipelineValidator);

        // Validate the YAML pipeline file
        if (!pipelineValidator.validatePipelineFile(yamlFilePath)) {
            System.err.println("❌ Validation failed. Please check the pipeline file and fix any errors.");
            return false;
        }

        try {
            System.out.println("✅ Pipeline validation successful. Generating execution order...");

            // Generate execution order
            final PipelineExecutionOrderGenerator executionOrderGenerator = new PipelineExecutionOrderGenerator();
            final Map<String, Map<String, Object>> executionOrder = executionOrderGenerator.generateExecutionOrder(yamlFilePath);

            // Check if execution order is empty (indicating dependency issues or misconfiguration)
            if (executionOrder.isEmpty()) {
                System.err.println("❌ No valid execution order found. Check for dependency or syntax issues.");
                return false;
            }

            // Convert execution order to YAML format
            final Yaml yaml = new Yaml();
            final String yamlOutput = yaml.dump(executionOrder);

            // Print the generated execution order
            System.out.println("📄 Execution Order:");
            System.out.println(yamlOutput);
            System.out.println("✅ Dry run completed successfully.");

            return true;

        } catch (Exception e) {
            System.err.println("❌ Error generating execution order: " + e.getMessage());
            return false;
        }
    }
}
