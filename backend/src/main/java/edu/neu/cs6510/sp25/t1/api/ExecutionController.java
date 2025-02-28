package edu.neu.cs6510.sp25.t1.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/executions")
public class ExecutionController {

    @GetMapping("/logs")
    public List<String> getExecutionLogs() throws IOException {
        return Files.readAllLines(Paths.get("job-executions.log"));
    }
}
