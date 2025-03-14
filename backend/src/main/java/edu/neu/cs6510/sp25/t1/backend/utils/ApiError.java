package edu.neu.cs6510.sp25.t1.backend.utils;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
public class ApiError {
    private HttpStatus status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
    private String message;
    private String detail;

    public ApiError(HttpStatus status, String message, String detail) {
        this.status = status;
        this.message = message;
        this.detail = detail;
        this.timestamp = LocalDateTime.now();
    }
}
