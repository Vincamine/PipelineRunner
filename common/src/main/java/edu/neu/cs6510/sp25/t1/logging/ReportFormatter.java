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

        // **Pipeline details**
        formattedReport.append(String.format("Pipeline: %s\n", safeString(report.getPipelineId(), "N/A")));
        formattedReport.append(String.format("Run Number: %d\n", report.getRunNumber()));
        formattedReport.append(String.format("Git Commit Hash: %s\n", safeString(report.getGitCommitHash(), "N/A")));
        formattedReport.append(String.format("Status: %s\n", safeString(report.getStatus(), "UNKNOWN")));
        formattedReport.append(String.format("Start Time: %s\n", formatDate(report.getStartTime())));
        formattedReport.append(String.format("Completion Time: %s\n", formatDate(report.getCompletionTime())));
        formattedReport.append(String.format("Message: %s\n", safeString(report.getMessage(), "No message available")));

        // **Stage details**
        if (report.getStages() != null && !report.getStages().isEmpty()) {
            formattedReport.append("\nStages:\n");
            for (StageInfo stage : report.getStages()) {
                formattedReport.append(String.format("  - Stage Name: %s\n", safeString(stage.getStageName(), "N/A")));
                formattedReport.append(String.format("    Status: %s\n", safeString(stage.getStageStatus(), "UNKNOWN")));
                formattedReport.append(String.format("    Start Time: %s\n", formatDate(stage.getStartTime())));
                formattedReport.append(String.format("    Completion Time: %s\n", formatDate(stage.getCompletionTime())));

                // **NEW: Include Jobs in each stage**
                if (stage.getJobs() != null && !stage.getJobs().isEmpty()) {
                    formattedReport.append(String.format("    Jobs: %s\n", String.join(", ", stage.getJobs())));
                } else {
                    formattedReport.append("    Jobs: None\n");
                }
            }
        }

        // **Additional Details (like errors)**
        if (report.getDetails() != null && !report.getDetails().isEmpty()) {
            formattedReport.append("\nDetails:\n");
            for (String detail : report.getDetails()) {
                formattedReport.append(String.format("  - %s\n", detail));
            }
        }

        return formattedReport.toString();
    }

    // Helper function to handle null/empty strings
    private static String safeString(String value, String defaultValue) {
        return (value != null && !value.isEmpty()) ? value : defaultValue;
    }

    // Helper function to format timestamps
    private static String formatDate(long timestamp) {
        return (timestamp > 0) ? DATE_FORMAT.format(new Date(timestamp)) : "N/A";
    }

    private ReportFormatter() {}
}
