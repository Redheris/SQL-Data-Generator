package dev.redheris.sqldatagenerator.cli;

public class RequestExecutionException extends RuntimeException {
    public RequestExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
