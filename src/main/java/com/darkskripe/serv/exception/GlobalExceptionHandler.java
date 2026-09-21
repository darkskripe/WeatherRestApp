package com.darkskripe.serv.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Centralized exception handling. Maps our domain exceptions to HTTP responses
 * and ensures no stack traces leak to clients. Exceptions are logged with
 * appropriate levels for later inspection.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    private final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(BadRequestException ex, HttpServletRequest req) {
        // Client error: log at WARN with the message but not the stacktrace
        log.warn("BadRequest: {}", ex.getMessage());
        ErrorResponse body = new ErrorResponse(System.currentTimeMillis(), 400, "Bad Request", ex.getMessage(), req.getRequestURI());
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException ex, HttpServletRequest req) {
        // Not found is expected in some flows; keep it clean
        log.info("NotFound: {}", ex.getMessage());
        ErrorResponse body = new ErrorResponse(System.currentTimeMillis(), 404, "Not Found", ex.getMessage(), req.getRequestURI());
        return ResponseEntity.status(404).body(body);
    }

    @ExceptionHandler(ExternalServiceException.class)
    public ResponseEntity<ErrorResponse> handleExternal(ExternalServiceException ex, HttpServletRequest req) {
        // Upstream failures are important; log full stacktrace for debugging
        log.error("External service failure: {}", ex.getMessage(), ex);
        ErrorResponse body = new ErrorResponse(System.currentTimeMillis(), 502, "Bad Gateway", "Failed to fetch data from upstream service", req.getRequestURI());
        return ResponseEntity.status(502).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(Exception ex, HttpServletRequest req) {
        // Catch-all: log error with stacktrace, return generic message to client
        log.error("Unhandled exception: {}", ex.getMessage(), ex);
        ErrorResponse body = new ErrorResponse(System.currentTimeMillis(), 500, "Internal Server Error", "An unexpected error occurred", req.getRequestURI());
        return ResponseEntity.status(500).body(body);
    }
}
