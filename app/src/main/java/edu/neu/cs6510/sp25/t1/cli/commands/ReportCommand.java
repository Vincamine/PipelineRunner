package edu.neu.cs6510.sp25.t1.cli.commands;

import edu.neu.cs6510.sp25.t1.model.ReportEntry;
import edu.neu.cs6510.sp25.t1.service.ReportService;
import edu.neu.cs6510.sp25.t1.util.ErrorHandler;
import edu.neu.cs6510.sp25.t1.util.ReportFormatter;
import picocli.CommandLine;

import java.net.http.HttpClient;
import java.util.List;

/**
 * Command to retrieve logs for a specific pipeline execution.
 * 
 * This command fetches logs from a remote storage service or database.
 */
@CommandLine.Command(name = "logs", description = "Retrieve logs for a pipeline based on its ID")
public class ReportCommand implements Runnable {

    /**
     * The ID of the pipeline whose logs are to be retrieved.
     */
    @CommandLine.Option(names = "--id", required = true, description = "Pipeline ID to retrieve logs for")
    private String pipelineId;

    private final ReportService logService;

    /**
     * Constructor allowing dependency injection for testing.
     *
     * @param logService The service responsible for fetching logs.
     */
    public ReportCommand(ReportService logService) {
        this.logService = logService;
    }

    /**
     * Default constructor for production use.
     */
    public ReportCommand() {
        this(new ReportService(HttpClient.newHttpClient()));
    }

    /**
     * Fetch logs for the given pipeline ID and print them to the console.
     */
    @Override
    public void run() {
        try {
            if (pipelineId == null || pipelineId.trim().isEmpty()) {
                System.err.println("Error: Pipeline ID is required.");
                return;
            }

            // Fetch logs for the given pipeline ID
            final List<ReportEntry> logs = logService.getLogsByPipelineId(pipelineId);

            if (logs.isEmpty()) {
                System.out.println("No logs found for pipeline ID: " + pipelineId);
                return;
            }

            // Print logs in formatted output
            logs.forEach(log -> System.out.println(ReportFormatter.format(log)));

        } catch (Exception e) {
            ErrorHandler.reportError(e.getMessage());
        }
    }
}
