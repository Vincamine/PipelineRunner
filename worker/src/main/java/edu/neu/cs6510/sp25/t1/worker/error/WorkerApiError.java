package edu.neu.cs6510.sp25.t1.worker.error;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Standard error response format for Worker API.
 */
@Getter
public class WorkerApiError {
    private String status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
    private String message;
    private String detail;
    private String path;

    public WorkerApiError(String status, String message, String detail, String path) {
        this.status = status;
        this.message = message;
        this.detail = detail;
        this.path = path;
        this.timestamp = LocalDateTime.now();
    }
}
