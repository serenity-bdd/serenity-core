package net.serenitybdd.junit.runners.integration.exceptions;

public class StringThrowableConstructorException extends Exception {
    public StringThrowableConstructorException(String message, Throwable cause) {
        super(message, cause);
    }
}