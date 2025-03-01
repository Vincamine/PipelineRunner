package edu.neu.cs6510.sp25.t1.common.api;

import java.io.Serializable;

public class WorkerRegistrationRequest implements Serializable {
    private String workerId;
    private String ipAddress;
    private String capabilities;

    public WorkerRegistrationRequest() {}

    public WorkerRegistrationRequest(String workerId, String ipAddress, String capabilities) {
        this.workerId = workerId;
        this.ipAddress = ipAddress;
        this.capabilities = capabilities;
    }

    public String getWorkerId() { return workerId; }
    public void setWorkerId(String workerId) { this.workerId = workerId; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getCapabilities() { return capabilities; }
    public void setCapabilities(String capabilities) { this.capabilities = capabilities; }
}
