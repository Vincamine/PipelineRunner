package edu.neu.cs6510.sp25.t1.common.api.request;

import java.io.Serializable;

/**
 * Represents a request to register a worker.
 * This class is used for sending worker registration requests to the backend.
 */
public class WorkerRegistrationRequest implements Serializable {
  private String workerId;
  private String ipAddress;
  private String capabilities;

  /**
   * Default constructor.
   * Initializes default values to prevent null references.
   */
  public WorkerRegistrationRequest() {
  }

  /**
   * Constructor with worker details.
   *
   * @param workerId     Worker ID.
   * @param ipAddress    Worker IP address.
   * @param capabilities Worker capabilities.
   */
  public WorkerRegistrationRequest(String workerId, String ipAddress, String capabilities) {
    this.workerId = workerId;
    this.ipAddress = ipAddress;
    this.capabilities = capabilities;
  }

  /**
   * Gets the worker ID.
   *
   * @return Worker ID.
   */
  public String getWorkerId() {
    return workerId;
  }

  /**
   * Sets the worker ID.
   *
   * @param workerId Worker ID.
   */
  public void setWorkerId(String workerId) {
    this.workerId = workerId;
  }

  /**
   * Gets the worker IP address.
   *
   * @return Worker IP address.
   */
  public String getIpAddress() {
    return ipAddress;
  }

  /**
   * Sets the worker IP address.
   *
   * @param ipAddress Worker IP address.
   */
  public void setIpAddress(String ipAddress) {
    this.ipAddress = ipAddress;
  }

  /**
   * Gets the worker capabilities.
   *
   * @return Worker capabilities.
   */
  public String getCapabilities() {
    return capabilities;
  }

  /**
   * Sets the worker capabilities.
   *
   * @param capabilities Worker capabilities.
   */
  public void setCapabilities(String capabilities) {
    this.capabilities = capabilities;
  }
}
