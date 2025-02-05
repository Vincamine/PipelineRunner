package t1.cicd.cli.commands;


import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import t1.cicd.cli.model.PipelineStatus;
import t1.cicd.cli.service.StatusService;


/**
 * Command to check pipeline execution status.

 * Usage examples:
 * - Check status with pipeline ID:
 *   cli status --pipeline-id 12345
 *
 * - Check status with detailed information:
 *   cli status --pipeline-id 12345 --verbose
 */
@Command(
    name = "status",
    description = "Check the status of a pipeline execution."
)
public class StatusCommand implements Runnable{
  private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofLocalizedDateTime(
      FormatStyle.MEDIUM);

  @Option(names = {"-p", "--pipeline-id"}, description = "Pipeline ID to check.", required = true)
  private String pipelineId;

  @Option(names = {"-v", "--verbose"}, description = "Show detailed status information.")
  private boolean verbose;
  private final StatusService statusService;
  public StatusCommand(){
    this.statusService = new StatusService();
  }


  @Override
  public void run() {
    try{
      PipelineStatus status = statusService.getPipelineStatus(pipelineId);
      displayStatus(status);
    }catch(Exception e){
     System.err.println("Error checking pipeline status: " + e.getMessage());
     if (verbose){
       e.printStackTrace();
     }
    }
  }

  private void displayStatus(PipelineStatus status){
    System.out.println("Pipeline Status");
    System.out.println("---------------");
    System.out.println("Pipeline ID: " + status.getPipelineId());
    System.out.println("Status: " + status.getState()+" ("+status.getState().getDescription()+")");
    System.out.println("Progress: " + status.getProgress());

    if (verbose){
      System.out.println("\nDetailed Information");
      System.out.println("-----------------------");
      System.out.println("Current Stage: "+ status.getCurrentStage());
      System.out.println("Start time: " + TIME_FORMATTER.format(status.getStartTime()));
      System.out.println("Last updated: "+ TIME_FORMATTER.format(status.getLastUpdated()));
      if(status.getMessage() != null){System.out.println("Message: " + status.getMessage());
      }
    }
  }
}
