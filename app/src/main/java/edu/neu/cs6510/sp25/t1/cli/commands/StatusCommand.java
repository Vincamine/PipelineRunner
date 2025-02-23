package edu.neu.cs6510.sp25.t1.cli.commands;

import edu.neu.cs6510.sp25.t1.model.PipelineStatus;
import edu.neu.cs6510.sp25.t1.service.StatusService;
import edu.neu.cs6510.sp25.t1.util.ErrorHandler;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Command to check pipeline execution status.
 * 
 * This command retrieves and displays the status of a specified pipeline
 * execution.
 */
@Command(name = "status", description = "Check the status of a pipeline execution.")
/**
 * Command to check the status of a pipeline execution.
 */
public class StatusCommand implements Runnable {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);

    /**
     * Pipeline ID to check the status.
     */
    @Option(names = { "-p", "--pipeline-id" }, description = "Pipeline ID to check.", required = true)
    private String pipelineId;

    private final StatusService statusService;

    /**
     * Constructor allowing dependency injection for testing.
     *
     * @param statusService The service responsible for retrieving pipeline status.
     */
    public StatusCommand(StatusService statusService) {
        this.statusService = statusService;
    }

    @Override
    public void run() {
        try {
            final PipelineStatus status = statusService.getPipelineStatus(pipelineId);
            displayStatus(status);
        } catch (Exception e) {
            ErrorHandler.reportError(e.getMessage());
        }
    }

    /**
     * Displays pipeline status details.
     *
     * @param status The pipeline status retrieved from the service.
     */
    private void displayStatus(PipelineStatus status) {
        System.out.println("Pipeline Status");
        System.out.println("---------------");
        System.out.println("Pipeline ID: " + status.getPipelineId());
        System.out.println("Status: " + status.getState() + " (" + status.getState().getDescription() + ")");
        System.out.println("Progress: " + status.getProgress() + "%");

        System.out.println("\nDetailed Information");
        System.out.println("-----------------------");
        System.out.println("Current Stage: " + status.getCurrentStage());

        final LocalDateTime startTime = LocalDateTime.ofInstant(status.getStartTime(), ZoneId.systemDefault());
        final LocalDateTime lastUpdated = LocalDateTime.ofInstant(status.getLastUpdated(), ZoneId.systemDefault());

        System.out.println("Start Time: " + TIME_FORMATTER.format(startTime));
        System.out.println("Last Updated: " + TIME_FORMATTER.format(lastUpdated));

        if (status.getMessage() != null && !status.getMessage().isEmpty()) {
            System.out.println("Message: " + status.getMessage());
        }
    }
}
