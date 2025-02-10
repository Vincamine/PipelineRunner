package edu.neu.cs6510.sp25.t1.cli.commands;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import edu.neu.cs6510.sp25.t1.cli.model.PipelineStatus;
import edu.neu.cs6510.sp25.t1.cli.service.StatusService;
import edu.neu.cs6510.sp25.t1.cli.util.ErrorFormatter;

/**
 * Command to check pipeline execution status.
 * <p>
 * This command retrieves and displays the status of a specified pipeline execution.
 * </p>
 */
@Command(
    name = "status",
    description = "Check the status of a pipeline execution."
)
public class StatusCommand implements Runnable {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);

    @Option(names = {"-p", "--pipeline-id"}, description = "Pipeline ID to check.", required = true)
    private String pipelineId;

    private final StatusService statusService;

    /**
     * Constructor allowing dependency injection for testing.
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
            final String errorMessage = ErrorFormatter.format("StatusCommand.java", 45, 5, e.getMessage());
            System.err.println(errorMessage);
        }
    }

    /**
     * Sets the pipeline ID for testing purposes.
     * @param pipelineId The pipeline ID to be set.
     */
    public void setPipelineId(String pipelineId) {
        this.pipelineId = pipelineId;
    }

    /**
     * Displays pipeline status details.
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
        if (status.getMessage() != null) {
            System.out.println("Message: " + status.getMessage());
        }
    }
}
