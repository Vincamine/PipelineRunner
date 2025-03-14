package edu.neu.cs6510.sp25.t1.worker.error;

/**
 * Exception thrown when Docker execution fails.
 */
public class DockerExecutionException extends WorkerException {

  private final int exitCode;

  public DockerExecutionException(String message, int exitCode) {
    super(message);
    this.exitCode = exitCode;
  }

  public DockerExecutionException(String message, Throwable cause) {
    super(message, cause);
    this.exitCode = -1;
  }

  public int getExitCode() {
    return exitCode;
  }
}
