package edu.neu.cs6510.sp25.t1.backend.api;

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
     * @throws IOException If an I/O error occurs while reading the file.
     */
    @GetMapping("/logs")
    public List<String> getExecutionLogs() throws IOException {
        return Files.readAllLines(Paths.get("job-executions.log"));
    }
}
