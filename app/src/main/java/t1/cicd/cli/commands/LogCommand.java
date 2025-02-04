package t1.cicd.cli.commands;

import picocli.CommandLine;
import t1.cicd.cli.model.LogEntry;
import t1.cicd.cli.service.LogService;
import t1.cicd.cli.util.LogFormatter;

import java.net.http.HttpClient;
import java.util.List;

/**
 * Command to retrieve logs for a specific pipeline.
 */
@CommandLine.Command(name = "logs", description = "Retrieve logs for a pipeline based on its ID")
public class LogCommand implements Runnable {

  @CommandLine.Option(names = "--id", required = true, description = "Pipeline ID to retrieve logs for")
  private String pipelineId;

  private final LogService logService;

  /**
   * Constructor allowing dependency injection for testing.
   */
  public LogCommand(LogService logService) {
    this.logService = logService;
  }

  /**
   * Default constructor for production use.
   */
  public LogCommand() {
    this(new LogService(HttpClient.newHttpClient())); // Use real HTTP client
  }

  @Override
  public void run() {
    final List<LogEntry> logs = logService.getLogsByPipelineId(pipelineId);

    if (logs.isEmpty()) {
      System.out.println("No logs found for pipeline ID: " + pipelineId);
      return;
    }

    logs.forEach(log -> System.out.println(LogFormatter.format(log)));
  }
}
