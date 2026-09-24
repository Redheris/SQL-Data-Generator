package dev.redheris.sqldatagenerator.request.model;

public class RequestValidationException extends RuntimeException {
    public RequestValidationException(Throwable cause) {
        super(cause);
    }
}
