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
     * The ID of the pipeline whose reports are to be retrieved.
     */
    @CommandLine.Option(names = "--id", required = true, description = "Pipeline ID to retrieve reports for")
    private String pipelineId;

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
            if (pipelineId == null || pipelineId.trim().isEmpty()) {
                System.err.println("Error: Pipeline ID is required.");
                return;
            }

            // Fetch reports for the given pipeline ID
            final List<ReportEntry> reports = reportService.getReportsByPipelineId(pipelineId);

            if (reports.isEmpty()) {
                System.out.println("No Reports found for pipeline ID: " + pipelineId);
                return;
            }

            // Print reports in formatted output
            reports.forEach(report -> System.out.println(ReportFormatter.format(report)));

        } catch (Exception e) {
            ErrorHandler.reportError(e.getMessage());
        }
    }
}
