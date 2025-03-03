package edu.neu.cs6510.sp25.t1.backend.config;

/**
 * Worker Configuration
 */
public class WorkerConfig {
  private int maxRetries;
  private int retryDelayMs;
  private int timeoutSeconds;

  /**
   * Get the maximum retries.
   *
   * @return int
   */
  public int getMaxRetries() {
    return maxRetries;
  }

  /**
   * Set the maximum retries.
   *
   * @param maxRetries int
   */
  public void setMaxRetries(int maxRetries) {
    this.maxRetries = maxRetries;
  }

  /**
   * Get the retry delay in milliseconds.
   *
   * @return int
   */
  public int getRetryDelayMs() {
    return retryDelayMs;
  }

  /**
   * Set the retry delay in milliseconds.
   *
   * @param retryDelayMs int
   */
  public void setRetryDelayMs(int retryDelayMs) {
    this.retryDelayMs = retryDelayMs;
  }

  /**
   * Get the timeout in seconds.
   *
   * @return int
   */
  public int getTimeoutSeconds() {
    return timeoutSeconds;
  }

  /**
   * Set the timeout in seconds.
   *
   * @param timeoutSeconds int
   */
  public void setTimeoutSeconds(int timeoutSeconds) {
    this.timeoutSeconds = timeoutSeconds;
  }
}