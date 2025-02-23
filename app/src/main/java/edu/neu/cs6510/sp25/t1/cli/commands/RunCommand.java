package edu.neu.cs6510.sp25.t1.cli.commands;

import edu.neu.cs6510.sp25.t1.util.ErrorHandler;
import edu.neu.cs6510.sp25.t1.util.GitValidator;
import edu.neu.cs6510.sp25.t1.validation.YamlPipelineValidator;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * CLI command to trigger a CI/CD pipeline execution.
 * This command executes a pipeline either locally or remotely.
 */
@Command(name = "run", description = "Trigger CI/CD pipeline execution")
/**
 * RunCommand class to handle the execution of a CI/CD pipeline.
 */
public class RunCommand implements Runnable {

    /**
     * Flag to indicate whether to run the pipeline locally.
     */
    @Option(names = "--local", description = "Run the pipeline on the local machine")
    private boolean isLocalRun;

    /**
     * Path to the repository (local or remote).
     */
    @Option(names = "--repo", description = "Repository location (local path or remote HTTPS URL)")
    private String repo;

    /**
     * Path to the pipeline configuration file.
     */
    @Option(names = "--file", description = "Path to the pipeline configuration file", required = true)
    private String pipelineFile;

    @SuppressWarnings("unused")
    private final YamlPipelineValidator validator;

    /**
     * Default constructor.
     */
    public RunCommand() {
        this(new YamlPipelineValidator());
    }

    /**
     * Constructor with a validator for testing.
     * 
     * @param validator
     */
    public RunCommand(YamlPipelineValidator validator) {
        if (validator == null) {
            throw new IllegalArgumentException("Validator cannot be null.");
        }
        this.validator = validator;
    }

    @Override
    public void run() {
        System.out.println("CI/CD pipeline execution started.");

        try {
            if (isLocalRun) {
                executeLocalRun();
            } else {
                System.err.println("Error: Remote execution is not supported without a backend.");
                throw new UnsupportedOperationException("Remote execution is not supported.");
            }
        } catch (Exception e) {
            ErrorHandler.reportError(e.getMessage());
            throw new RuntimeException("Pipeline execution failed.", e);
        }
    }

    /**
     * Executes the pipeline locally.
     */
    private void executeLocalRun() {
        System.out.println("Validating local repository...");

        if (!GitValidator.isGitRepository()) {
            throw new IllegalStateException("Error: Not a valid Git repository.");
        }

        GitValidator.validateGitRepo();

        if (!validatePipelineFile(pipelineFile)) {
            throw new IllegalArgumentException("Pipeline file validation failed.");
        }

        System.out.println("Pipeline validation successful.");
        System.out.println("Executing pipeline...");

        executeLocalJobs();

        System.out.println("Pipeline execution complete.");
    }

    /**
     * Validates the pipeline YAML file.
     * 
     * @param filePath The pipeline file path.
     * @return true if valid, false otherwise.
     */
    private boolean validatePipelineFile(String filePath) {
        final YamlPipelineValidator validator = new YamlPipelineValidator();
        if (!validator.validatePipeline(filePath)) {
            System.err.println("Pipeline validation failed.");
            return false;
        }
        return true;
    }

    /**
     * Simulates local execution of jobs.
     * 
     * @throws RuntimeException if job execution is interrupted.
     */
    private void executeLocalJobs() {
        final String[] jobs = { "build-app", "run-tests", "deploy-app" };

        for (String job : jobs) {
            System.out.println("Running job: " + job);
            try {
                Thread.sleep(2000); // Simulate job execution time
                System.out.println("Job completed: " + job);
            } catch (InterruptedException e) {
                System.err.println("Job failed: " + job);
                Thread.currentThread().interrupt();
                throw new RuntimeException("Job execution interrupted: " + job, e); // ✅ Instead of System.exit(1)
            }
        }
    }

}
