package edu.neu.cs6510.sp25.t1.api;

import java.io.Serializable;

public class JobStatusUpdate implements Serializable {
    private String jobId;
    private String status;
    private String output;
    private int exitCode;

    public JobStatusUpdate() {}

    public JobStatusUpdate(String jobId, String status, String output, int exitCode) {
        this.jobId = jobId;
        this.status = status;
        this.output = output;
        this.exitCode = exitCode;
    }

    public String getJobId() { return jobId; }
    public void setJobId(String jobId) { this.jobId = jobId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getOutput() { return output; }
    public void setOutput(String output) { this.output = output; }

    public int getExitCode() { return exitCode; }
    public void setExitCode(int exitCode) { this.exitCode = exitCode; }
}
