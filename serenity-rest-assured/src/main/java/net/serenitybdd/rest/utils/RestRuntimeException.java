package net.serenitybdd.rest.utils;

public class RestRuntimeException extends RuntimeException {
    public RestRuntimeException(String message) {
        super(message);
    }

    public RestRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
