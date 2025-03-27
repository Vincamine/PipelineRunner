package edu.neu.cs6510.sp25.t1.backend.service.status;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class StatusService {
  public Map<String, Object> getStatusForPipeline(String pipelineName) {
    // TODO: Implement actual logic to fetch status for the pipeline name
    return Map.of("pipeline", pipelineName, "status", "Pending"); // placeholder
  }
}
