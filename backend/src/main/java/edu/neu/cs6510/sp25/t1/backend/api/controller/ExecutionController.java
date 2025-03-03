package edu.neu.cs6510.sp25.t1.backend.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Controller for handling execution-related requests.
 */
@RestController
@RequestMapping("/api/executions")
public class ExecutionController {

  /**
   * Retrieves the execution logs from a file.
   *
   * @return A list of log entries.
   */
  @GetMapping("/logs")
  public ResponseEntity<List<String>> getExecutionLogs() {
    try {
      List<String> logs = Files.readAllLines(Paths.get("job-executions.log"));
      return ResponseEntity.ok(logs);
    } catch (IOException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(List.of("Error reading logs"));
    }
  }
}
