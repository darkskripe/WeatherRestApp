package com.darkskripe.serv.exception;

/**
 * Simple DTO sent to clients on error. Uses a Java record for brevity.
 * Fields: timestamp(ms), http status, short error, human message, request path
 */
public record ErrorResponse(long timestamp, int status, String error, String message, String path) {
}
