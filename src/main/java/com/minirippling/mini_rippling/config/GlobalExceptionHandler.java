package com.minirippling.mini_rippling.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(
            RuntimeException ex,
            HttpServletRequest request) {

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        // map specific messages to correct HTTP status codes
        String message = ex.getMessage();
        if (message != null) {
            if (message.contains("not found") || message.contains("Not Found")) {
                status = HttpStatus.NOT_FOUND;
            } else if (message.contains("already exists") ||
                    message.contains("already clocked") ||
                    message.contains("already run")) {
                status = HttpStatus.CONFLICT;
            } else if (message.contains("Invalid email") ||
                    message.contains("disabled")) {
                status = HttpStatus.UNAUTHORIZED;
            } else if (message.contains("already approved") ||
                    message.contains("already rejected")) {
                status = HttpStatus.BAD_REQUEST;
            }
        }

        return ResponseEntity.status(status).body(Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status", status.value(),
                "error", message != null ? message : "An error occurred",
                "path", request.getRequestURI()
        ));
    }
}