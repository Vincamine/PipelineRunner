package edu.neu.cs6510.sp25.t1.cli.commands;

import edu.neu.cs6510.sp25.t1.model.ReportEntry;
import edu.neu.cs6510.sp25.t1.service.ReportService;
import edu.neu.cs6510.sp25.t1.util.ErrorHandler;
import edu.neu.cs6510.sp25.t1.util.ReportFormatter;
import picocli.CommandLine;

import java.net.http.HttpClient;
import java.util.List;

/**
 * Command to retrieve reports for a specific pipeline execution.
 * 
 * This command fetches reports from a remote storage service or database.
 */
@CommandLine.Command(name = "report", description = "Retrieve reports for a pipeline based on its ID")
public class ReportCommand implements Runnable {


    /**
     * The repo of the pipeline whose reports are to be retrieved.
     */
    @CommandLine.Option(names = "--repo", description = "Repository URL to retrieve reports from")
    private String repoUrl;

    /**
     *  Retrieve local reports.
     */
    @CommandLine.Option(names = "--local", description = "Retrieve reports from the local repository in the working directory")
    private boolean isLocal;

    /**
     * The ID of the pipeline whose reports are to be retrieved.
     */
    @CommandLine.Option(names = "--pipeline", required = true, description = "Pipeline Name to retrieve reports for")
    private String pipelineId;

    /**
     * The ID of the run from a pipeline.
     */
    @CommandLine.Option(names = "--run", description = "Specific run number for a pipeline")
    private Integer runNumber;

    private final ReportService reportService;

    /**
     * Constructor allowing dependency injection for testing.
     *
     * @param reportService The service responsible for fetching reports.
     */
    public ReportCommand(ReportService reportService) {
        this.reportService = reportService;
    }

    /**
     * Default constructor for production use.
     */
    public ReportCommand() {
        this(new ReportService(HttpClient.newHttpClient()));
    }

    /**
     * Fetch reports for the given pipeline ID and print them to the console.
     */
    @Override
    public void run() {
        try {
            if (repoUrl == null && !isLocal) {
                System.err.println("Error: Either --repo or --local must be specified.");
                return;
            }
            List<ReportEntry> reports;

            if (isLocal) {
                if (runNumber != null && pipelineId != null) {
                    // Fetch specific pipeline run summary for local repository
                    reports = reportService.getLocalPipelineRunSummary(pipelineId, runNumber);
                } else if (pipelineId != null) {
                    // Fetch all runs for a specific pipeline in local repo
                    reports = reportService.getLocalPipelineRuns(pipelineId);
                } else {
                    // Fetch all pipeline runs in the local repo
                    reports = reportService.getLocalRepositoryReports();
                }
            } else {
                if (runNumber != null && pipelineId != null) {
                    // Fetch specific pipeline run summary from remote repo
                    reports = reportService.getPipelineRunSummary(repoUrl, pipelineId, runNumber);
                } else if (pipelineId != null) {
                    // Fetch all runs for a specific pipeline from remote repo
                    reports = reportService.getPipelineRuns(repoUrl, pipelineId);
                } else {
                    // Fetch all pipeline runs for a remote repository
                    reports = reportService.getRepositoryReports(repoUrl);
                }
            }
            if (reports.isEmpty()) {
                System.out.println("No reports found matching the criteria.");
                return;
            }

            // Print reports in formatted output
            reports.forEach(report -> System.out.println(ReportFormatter.format(report)));

        } catch (Exception e) {
            ErrorHandler.reportError(e.getMessage());
        }
    }
}
