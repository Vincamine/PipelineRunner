package edu.neu.cs6510.sp25.t1.backend.api;

import edu.neu.cs6510.sp25.t1.backend.dto.PipelineDTO;
import edu.neu.cs6510.sp25.t1.backend.service.ReportService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/pipelines")
    public List<PipelineDTO> getAllPipelines() {
        return reportService.getAllPipelines();
    }

    @GetMapping("/{pipeline}")
    public List<PipelineDTO> getPipelineExecutions(@PathVariable String pipeline) {
        return reportService.getPipelineExecutions(pipeline);
    }

    @GetMapping("/{pipeline}/latest")
    public PipelineDTO getLatestPipelineRun(@PathVariable String pipeline) {
        return reportService.getLatestPipelineRun(pipeline);
    }
}
