package edu.neu.cs6510.sp25.t1.cli.commands;

import edu.neu.cs6510.sp25.t1.cli.model.LogEntry;
import picocli.CommandLine;
import edu.neu.cs6510.sp25.t1.cli.service.LogService;
import edu.neu.cs6510.sp25.t1.cli.util.LogFormatter;

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
