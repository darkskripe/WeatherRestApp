package com.darkskripe.serv.exception;

/**
 * Thrown when a requested resource cannot be found.
 * Simple unchecked exception so callers don't have to declare it.
 */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
