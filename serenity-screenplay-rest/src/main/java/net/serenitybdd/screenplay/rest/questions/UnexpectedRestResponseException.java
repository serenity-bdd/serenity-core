package net.serenitybdd.screenplay.rest.questions;

/**
 * Thrown when a REEST Question receives a response with a status that is not 2xx.
 */
public class UnexpectedRestResponseException extends RuntimeException {
    public UnexpectedRestResponseException(String message) {
        super(message);
    }
}
