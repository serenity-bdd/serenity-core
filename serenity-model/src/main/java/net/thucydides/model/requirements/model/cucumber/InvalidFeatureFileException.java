package net.thucydides.model.requirements.model.cucumber;

import net.serenitybdd.model.exceptions.TestCompromisedException;

public class InvalidFeatureFileException extends TestCompromisedException {

    public InvalidFeatureFileException(String message) {
        super(message);
    }

    public InvalidFeatureFileException(String message, Throwable cause) {
        super(message, cause);
    }
}
