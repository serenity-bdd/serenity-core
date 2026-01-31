package net.serenitybdd.screenplay.playwright.injectors;

/**
 * Exception thrown when an actor cannot be instantiated or configured
 * during the injection process.
 */
public class CastingException extends RuntimeException {

    public CastingException(String message) {
        super(message);
    }

    public CastingException(String message, Throwable cause) {
        super(message, cause);
    }
}
