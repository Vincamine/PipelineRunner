package edu.neu.cs6510.sp25.t1.common.api;

import java.io.Serializable;

public class WorkerRegistrationResponse implements Serializable {
    private boolean success;
    private String message;

    public WorkerRegistrationResponse() {}

    public WorkerRegistrationResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
