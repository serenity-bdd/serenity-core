package net.serenitybdd.model.exceptions;

public class TestCompromisedException extends RuntimeException implements CausesCompromisedTestFailure {

    public TestCompromisedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public TestCompromisedException(Throwable cause) {
        super(cause);
    }

    public TestCompromisedException(String message, Throwable cause) {
        super(message, cause);
    }

    public TestCompromisedException(String message) {
        super(message);
    }
}
