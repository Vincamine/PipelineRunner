package edu.neu.cs6510.sp25.t1.api;

import org.springframework.web.bind.annotation.*;
import java.util.List;

import edu.neu.cs6510.sp25.t1.model.execution.PipelineExecution;
import edu.neu.cs6510.sp25.t1.service.ReportService;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/pipelines")
    public List<String> getAllPipelines() {
        return reportService.getAllPipelines();
    }

    @GetMapping("/{pipeline}")
    public List<PipelineExecution> getPipelineExecutions(@PathVariable String pipeline) {
        return reportService.getPipelineExecutions(pipeline);
    }

    @GetMapping("/{pipeline}/{runId}")
    public PipelineExecution getPipelineRun(@PathVariable String pipeline, @PathVariable String runId) {
        return reportService.getPipelineRun(pipeline, runId);
    }
}
