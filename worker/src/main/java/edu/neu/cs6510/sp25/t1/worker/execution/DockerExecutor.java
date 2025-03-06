package edu.neu.cs6510.sp25.t1.worker.execution;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import edu.neu.cs6510.sp25.t1.common.dto.JobDTO;
import edu.neu.cs6510.sp25.t1.common.dto.JobExecutionDTO;
import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Executes jobs inside Docker containers.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DockerExecutor {

  /**
   * Executes a job inside a Docker container.
   *
   * @param jobExecution The job execution details.
   * @return The execution status.
   */
  public ExecutionStatus execute(JobExecutionDTO jobExecution) {
    try {
      // Get the associated Job details from JobExecutionDTO
      JobDTO job = jobExecution.getJob();
      String dockerImage = job.getDockerImage();
      List<String> script = job.getScript();

      // Build the Docker command
      String[] command = buildDockerCommand(dockerImage, script);

      // Log the command before execution
      log.info("Executing Docker command: {}", String.join(" ", command));

      // Use ProcessBuilder instead of Runtime.exec()
      ProcessBuilder processBuilder = new ProcessBuilder(command);
      processBuilder.redirectErrorStream(true);  // Merge stderr with stdout
      Process process = processBuilder.start();

      // Capture logs
      try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
        StringBuilder outputLogs = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
          outputLogs.append(line).append("\n");
        }
        log.info("Docker Execution Logs:\n{}", outputLogs);
      }

      int exitCode = process.waitFor(); // Wait for execution to finish
      log.info("Docker job exited with code: {}", exitCode);

      // Determine execution status
      return exitCode == 0 ? ExecutionStatus.SUCCESS : ExecutionStatus.FAILED;

    } catch (Exception e) {
      log.error("Docker execution failed: {}", e.getMessage(), e);
      return ExecutionStatus.FAILED;
    }
  }

  /**
   * Builds the Docker command for job execution.
   */
  private String[] buildDockerCommand(String dockerImage, List<String> script) {
    StringBuilder scriptCommands = new StringBuilder();
    for (String cmd : script) {
      scriptCommands.append(cmd).append(" && ");
    }

    // Remove last "&&" and wrap in shell execution
    if (scriptCommands.length() > 4) {
      scriptCommands.setLength(scriptCommands.length() - 4);
    }

    return new String[]{
            "docker", "run", "--rm",
            dockerImage,
            "sh", "-c", scriptCommands.toString()
    };
  }
}
