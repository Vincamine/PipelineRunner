package edu.neu.cs6510.sp25.t1.cli.commands;

import edu.neu.cs6510.sp25.t1.util.ErrorHandler;
import edu.neu.cs6510.sp25.t1.util.GitValidator;
import edu.neu.cs6510.sp25.t1.validation.YamlPipelineValidator;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * CLI command to trigger a CI/CD pipeline execution.
 * Runs the pipeline either locally or remotely.
 */
@Command(name = "run", description = "Trigger CI/CD pipeline execution")
public class RunCommand implements Runnable {

    @Option(names = "--local", description = "Run the pipeline on the local machine")
    private boolean isLocalRun;

    @Option(names = "--repo", description = "Repository location (local path or remote HTTPS URL)")
    private String repo;

    @Option(names = "--file", description = "Path to the pipeline configuration file", required = true)
    private String pipelineFile;

    @Override
    public void run() {
        try {
            System.out.println("🚀 CI/CD pipeline execution started.");

            if (isLocalRun) {
                executeLocalRun();
            } else {
                System.err.println("❌ Error: Remote execution is not supported without a backend.");
                System.exit(1);
            }
        } catch (Exception e) {
            ErrorHandler.reportError(e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Executes the pipeline locally.
     */
    private void executeLocalRun() {
        System.out.println("🔍 Validating local repository...");
        if (!GitValidator.isGitRepository()) {
            System.err.println("❌ Error: Not a valid Git repository.");
            System.exit(1);
            return;
        }

        GitValidator.validateGitRepo();

        if (!validatePipelineFile(pipelineFile)) {
            System.exit(1);
            return;
        }

        System.out.println("✅ Pipeline validation successful.");
        System.out.println("🔄 Executing pipeline...");

        executeLocalJobs();

        System.out.println("✅ Pipeline execution complete.");
    }

    /**
     * Validates the pipeline YAML file.
     * @param filePath The pipeline file path.
     * @return True if valid, false otherwise.
     */
    private boolean validatePipelineFile(String filePath) {
        final YamlPipelineValidator validator = new YamlPipelineValidator();
        if (!validator.validatePipeline(filePath)) {
            System.err.println("❌ Pipeline validation failed.");
            return false;
        }
        return true;
    }

    /**
     * Simulates local execution of jobs.
     */
    private void executeLocalJobs() {
        final String[] jobs = {"build-app", "run-tests", "deploy-app"};

        for (String job : jobs) {
            System.out.println("🔄 Running job: " + job);
            try {
                Thread.sleep(2000); // Simulate job execution time
                System.out.println("✅ Job completed: " + job);
            } catch (InterruptedException e) {
                System.err.println("❌ Job failed: " + job);
                Thread.currentThread().interrupt();
                System.exit(1);
            }
        }
    }
}
