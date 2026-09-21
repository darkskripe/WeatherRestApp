package com.darkskripe.serv.exception;

/**
 * Wrapper for errors coming from external systems (e.g. the weather API).
 * Keep the original cause so logs retain full stack traces.
 */
public class ExternalServiceException extends RuntimeException {
    public ExternalServiceException(String message) {
        super(message);
    }

    public ExternalServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
