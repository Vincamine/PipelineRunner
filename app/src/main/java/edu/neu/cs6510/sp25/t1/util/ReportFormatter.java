package edu.neu.cs6510.sp25.t1.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import edu.neu.cs6510.sp25.t1.model.ReportEntry;
import edu.neu.cs6510.sp25.t1.model.StageInfo;

/**
 * Utility class for formatting report entries.
 */
public class ReportFormatter {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String format(ReportEntry report) {
        if (report == null) {
            return "No report available";
        }

        StringBuilder formattedReport = new StringBuilder();

        // Format pipeline details
        formattedReport.append(String.format("Pipeline: %s\n", report.getPipelineId()));
        formattedReport.append(String.format("Run Number: %d\n", report.getRunNumber()));
        formattedReport.append(String.format("Git Commit Hash: %s\n", report.getGitCommitHash()));
        formattedReport.append(String.format("Status: %s\n", report.getStatus()));
        formattedReport.append(String.format("Start Time: %s\n", DATE_FORMAT.format(new Date(report.getStartTime()))));
        formattedReport.append(String.format("Completion Time: %s\n", DATE_FORMAT.format(new Date(report.getCompletionTime()))));

        // Format stage details
        if (report.getStages() != null && !report.getStages().isEmpty()) {
            formattedReport.append("\nStages:\n");
            for (StageInfo stage : report.getStages()) {
                formattedReport.append(String.format("  - Stage Name: %s\n", stage.getStageName()));
                formattedReport.append(String.format("    Status: %s\n", stage.getStageStatus()));
                formattedReport.append(String.format("    Start Time: %s\n", DATE_FORMAT.format(new Date(stage.getStartTime()))));
                formattedReport.append(String.format("    Completion Time: %s\n", DATE_FORMAT.format(new Date(stage.getCompletionTime()))));
            }
        }

        return formattedReport.toString();
    }

    private ReportFormatter() {}
}
