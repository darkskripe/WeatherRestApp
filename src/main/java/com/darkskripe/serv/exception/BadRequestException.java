package com.darkskripe.serv.exception;

/**
 * Thrown when the client supplied invalid input.
 * Keeps controller/service validation simple and explicit.
 */
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
