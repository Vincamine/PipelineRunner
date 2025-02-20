package edu.neu.cs6510.sp25.t1.cli.commands;

import edu.neu.cs6510.sp25.t1.util.PipelineValidator;
import edu.neu.cs6510.sp25.t1.validation.YamlPipelineValidator;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.concurrent.Callable;

/**
 * Command to validate a pipeline YAML file.
 * 
 * This command ensures that the pipeline YAML file:
 * - Has a valid structure
 * - Contains no cyclic dependencies
 * - Defines valid job configurations
 */
@Command(name = "check", description = "Validate a pipeline YAML file", mixinStandardHelpOptions = true)
public class CheckCommand implements Callable<Boolean> {

    @Option(names = { "-f",
            "--file" }, description = "Path to the pipeline YAML file", required = false, defaultValue = ".pipelines/pipeline.yaml")
    private String yamlFilePath;

    private final PipelineValidator pipelineValidator;

    /** Default constructor that creates a new PipelineValidator. */
    public CheckCommand() {
        this(new PipelineValidator(new YamlPipelineValidator()));
    }

    /**
     * Constructor for injecting a mock PipelineValidator.
     * @param pipelineValidator The pipeline validator to be used for validation.
     */
    public CheckCommand(PipelineValidator pipelineValidator) {
        this.pipelineValidator = pipelineValidator;
    }


    /** Executes the check command. */
    @Override
    public Boolean call() {
        final boolean isValid = pipelineValidator.validatePipelineFile(yamlFilePath);

        if (!isValid) {
            System.err.println("Pipeline validation failed. Please check your YAML file.");
            return false;
        }

        return true;
    }
}
