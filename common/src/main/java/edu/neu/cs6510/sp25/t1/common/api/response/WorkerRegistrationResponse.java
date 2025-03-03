package edu.neu.cs6510.sp25.t1.common.api.response;

import java.io.Serializable;

/**
 * WorkerRegistrationResponse is a class that represents the response from the Master to the Worker
 * when the Worker registers itself with the Master.
 */
public class WorkerRegistrationResponse implements Serializable {
  private boolean success;
  private String message;

  /**
   * Default constructor.
   */
  public WorkerRegistrationResponse() {
  }

  /**
   * Constructor.
   *
   * @param success boolean
   * @param message String
   */
  public WorkerRegistrationResponse(boolean success, String message) {
    this.success = success;
    this.message = message;
  }

  /**
   * Getter for success.
   *
   * @return boolean
   */
  public boolean isSuccess() {
    return success;
  }

  /**
   * Setter for success.
   *
   * @param success boolean
   */
  public void setSuccess(boolean success) {
    this.success = success;
  }

  /**
   * Getter for message.
   *
   * @return String
   */
  public String getMessage() {
    return message;
  }

  /**
   * Setter for message.
   *
   * @param message String
   */
  public void setMessage(String message) {
    this.message = message;
  }
}
