package edu.neu.cs6510.sp25.t1.cli.commands;

import edu.neu.cs6510.sp25.t1.model.ReportEntry;
import edu.neu.cs6510.sp25.t1.service.ReportService;
import edu.neu.cs6510.sp25.t1.util.ReportErrorHandler;
import edu.neu.cs6510.sp25.t1.util.ReportFormatter;
import picocli.CommandLine;

import java.net.http.HttpClient;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Command line interface for retrieving pipeline execution reports.
 * This class implements the main functionality for the 'report' command,
 * allowing users to retrieve various types of pipeline execution reports.
 *
 * The command supports several levels of reporting:
 * - List all available pipelines
 * - Get all executions for a pipeline
 * - Get specific run details
 * - Get stage-level reports
 * - Get job-level reports
 * 
 *
 * Example usage:
 * report --repo https://github.com/user/repo --pipeline main-pipeline
 * report --local --pipeline main-pipeline --run 123
 * report --repo https://github.com/user/repo --pipeline main-pipeline --run 123
 * --stage build
 * 
 */
@CommandLine.Command(name = "report", description = "Retrieve reports for pipeline executions", mixinStandardHelpOptions = true)
/**
 * ReportCommand class to handle the retrieval of pipeline execution reports.
 */
public class ReportCommand implements Runnable {

    @CommandLine.Option(names = "--repo", description = "Repository URL to retrieve reports from")
    private String repoUrl;

    @CommandLine.Option(names = "--local", description = "Retrieve reports from the local repository in the working directory")
    private boolean isLocal;

    @CommandLine.Option(names = "--pipeline", description = "Pipeline Name to retrieve reports for")
    private String pipelineId;

    @CommandLine.Option(names = "--run", description = "Unique identifier for a specific pipeline execution")
    private Integer runNumber;

    @CommandLine.Option(names = "--stage", description = "Stage name to get specific stage report")
    private String stageName;

    @CommandLine.Option(names = "--job", description = "Job name to get specific job report")
    private String jobName;

    private final ReportService reportService;

    /**
     * Constructor for testing purposes with a mock service.
     *
     * @param reportService The service to use for fetching reports
     */
    public ReportCommand(ReportService reportService) {
        this.reportService = reportService;
    }

    /**
     * Default constructor for production use.
     * Initializes a new ReportService with default HttpClient.
     */
    public ReportCommand() {
        this(new ReportService(HttpClient.newHttpClient()));
    }

    /**
     * Main execution method for the report command.
     * Validates input parameters, retrieves reports, and displays them.
     *
     * @throws CommandLine.ParameterException if validation fails
     * @throws CommandLine.ExecutionException if there's an error during execution
     */
    @Override
    public void run() {
        try {
            validateInput();
            List<ReportEntry> reports = retrieveReports();
            displayReports(reports);
        } catch (CommandLine.ParameterException e) {
            ReportErrorHandler.reportError(e.getMessage());
            throw e; // Let picocli handle the exit code
        } catch (Exception e) {
            ReportErrorHandler.reportError("Unexpected error occurred", e);
            throw new CommandLine.ExecutionException(
                    new CommandLine(this),
                    "Command execution failed: " + e.getMessage());
        }
    }

    /**
     * Validates the command input parameters.
     * Ensures required parameters are present and combinations are valid.
     *
     * @throws CommandLine.ParameterException if validation fails
     */
    private void validateInput() {
        List<String> errors = new ArrayList<>();

        if (repoUrl == null && !isLocal) {
            errors.add("Either --repo or --local must be specified.");
        }

        if (jobName != null && stageName == null) {
            errors.add("Stage name must be specified when requesting job report.");
        }

        if ((stageName != null || jobName != null) && (pipelineId == null || runNumber == null)) {
            errors.add("Pipeline name and run number must be specified for stage/job reports.");
        }

        // If any validation errors were collected, throw a single exception with all
        // errors listed.
        // This allows the user to see all issues at once instead of fixing them one by
        // one.
        if (!errors.isEmpty()) {
            throw new CommandLine.ParameterException(
                    new CommandLine(this),
                    String.join("\n", errors));
        }
    }

    /**
     * Retrieves reports based on the provided command parameters.
     *
     * @return List of report entries
     * @throws IllegalArgumentException if repository is invalid
     * @throws SecurityException        if repository access is denied
     */
    private List<ReportEntry> retrieveReports() {
        try {
            if (pipelineId == null) {
                return handleListPipelines();
            }

            if (jobName != null) {
                return handleJobReport();
            }

            if (stageName != null) {
                return handleStageReport();
            }

            if (runNumber != null) {
                return handleSpecificRunReport();
            }

            return handleAllRunsReport();

        } catch (IllegalArgumentException e) {
            ReportErrorHandler.reportInvalidRepository(repoUrl, e);
            throw e;
        } catch (SecurityException e) {
            ReportErrorHandler.reportRepositoryAccessError(repoUrl, e);
            throw e;
        }
    }

    /**
     * Lists all available pipelines.
     *
     * @return List containing a single report entry with pipeline names
     */
    /**
     * Lists all available pipelines.
     *
     * @return List containing a single report entry with pipeline names
     */
    private List<ReportEntry> handleListPipelines() {
        List<String> pipelineNames = reportService.getAllPipelineNames(isLocal ? null : repoUrl);
        return List.of(new ReportEntry(
                "pipelines", // pipelineId
                "INFO", // level
                "Available Pipelines: " + String.join(", ", pipelineNames), // message
                System.currentTimeMillis(), // timestamp
                "INFO", // status
                Collections.emptyList(), // stages
                Collections.emptyList(), // details
                0, // Run number (default value since it's not applicable here)
                "N/A", // Git commit hash (default value for list query)
                System.currentTimeMillis(), // Start time (default value)
                System.currentTimeMillis() // Completion time (default value)
        ));
    }

    /**
     * Retrieves report for a specific job.
     *
     * @return List of report entries for the job
     */
    private List<ReportEntry> handleJobReport() {
        return reportService.getJobReport(
                isLocal ? null : repoUrl,
                pipelineId,
                runNumber,
                stageName,
                jobName);
    }

    /**
     * Retrieves report for a specific stage.
     *
     * @return List of report entries for the stage
     */
    private List<ReportEntry> handleStageReport() {
        return reportService.getStageReport(
                isLocal ? null : repoUrl,
                pipelineId,
                runNumber,
                stageName);
    }

    /**
     * Retrieves report for a specific pipeline run.
     *
     * @return List of report entries for the run
     */
    private List<ReportEntry> handleSpecificRunReport() {
        return isLocal
                ? reportService.getLocalPipelineRunSummary(pipelineId, runNumber)
                : reportService.getPipelineRunSummary(repoUrl, pipelineId, runNumber);
    }

    /**
     * Retrieves reports for all runs of a pipeline.
     *
     * @return List of report entries for all runs
     */
    private List<ReportEntry> handleAllRunsReport() {
        return isLocal
                ? reportService.getLocalPipelineRuns(pipelineId)
                : reportService.getPipelineRuns(repoUrl, pipelineId);
    }

    /**
     * Displays the retrieved reports.
     * If no reports are found, appropriate error messages are displayed.
     *
     * @param reports List of report entries to display
     */
    private void displayReports(List<ReportEntry> reports) {
        if (reports == null || reports.isEmpty()) {
            handleEmptyReports();
            return;
        }
        reports.forEach(report -> System.out.println(ReportFormatter.format(report)));
    }

    /**
     * Handles cases where no reports are found.
     * Provides appropriate error messages based on the query type.
     */
    private void handleEmptyReports() {
        if (jobName != null) {
            ReportErrorHandler.reportError(
                    String.format("No report found for job '%s' in stage '%s' of run #%d in pipeline '%s'",
                            jobName, stageName, runNumber, pipelineId));
        } else if (stageName != null) {
            ReportErrorHandler.reportError(
                    String.format("No report found for stage '%s' of run #%d in pipeline '%s'",
                            stageName, runNumber, pipelineId));
        } else if (runNumber != null) {
            ReportErrorHandler.reportPipelineRunNotFound(pipelineId, runNumber);
        } else if (pipelineId != null) {
            ReportErrorHandler.reportMissingPipeline(pipelineId);
        } else {
            System.out.println("No pipelines found.");
        }
    }
}